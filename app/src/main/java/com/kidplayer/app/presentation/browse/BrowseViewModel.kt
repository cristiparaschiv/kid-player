package com.kidplayer.app.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.network.NetworkMonitor
import com.kidplayer.app.data.network.NetworkState
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.usecase.CancelDownloadUseCase
import com.kidplayer.app.domain.usecase.GetDownloadStatusUseCase
import com.kidplayer.app.domain.usecase.GetLibrariesUseCase
import com.kidplayer.app.domain.usecase.GetMediaItemsUseCase
import com.kidplayer.app.domain.usecase.StartDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Browse screen
 * Manages libraries and media items loading with StateFlow
 */
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val getLibrariesUseCase: GetLibrariesUseCase,
    private val getMediaItemsUseCase: GetMediaItemsUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase,
    private val getDownloadStatusUseCase: GetDownloadStatusUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    val networkState: StateFlow<NetworkState> = networkMonitor.networkState

    companion object {
        private const val PAGE_SIZE = 100 // Load 100 items per page
    }

    init {
        loadLibraries()
    }

    /**
     * Load libraries from repository
     */
    private fun loadLibraries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getLibrariesUseCase()) {
                is Result.Success -> {
                    val libraries = result.data
                    Timber.d("Loaded ${libraries.size} libraries")

                    // Filter to only video libraries
                    val videoLibraries = libraries.filter { it.isVideoLibrary() }

                    _uiState.update {
                        it.copy(
                            libraries = videoLibraries,
                            isLoading = false,
                            error = null
                        )
                    }

                    // If we have libraries, load media items from the first one
                    if (videoLibraries.isNotEmpty()) {
                        selectLibrary(videoLibraries.first().id)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                is Result.Error -> {
                    Timber.e("Error loading libraries: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    /**
     * Load media items from a specific library or all libraries
     * Uses pagination to load initial batch of items
     */
    private fun loadMediaItems(libraryId: String?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    currentPage = 0,
                    mediaItems = emptyList() // Clear existing items
                )
            }

            when (val result = getMediaItemsUseCase.getPaginated(
                libraryId = libraryId,
                limit = PAGE_SIZE,
                startIndex = 0
            )) {
                is Result.Success -> {
                    val paginatedResult = result.data
                    Timber.d("Loaded ${paginatedResult.items.size} of ${paginatedResult.totalCount} media items")

                    _uiState.update {
                        it.copy(
                            mediaItems = paginatedResult.items,
                            selectedLibraryId = libraryId,
                            totalItemCount = paginatedResult.totalCount,
                            hasMoreItems = paginatedResult.hasMore,
                            currentPage = 0,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    Timber.e("Error loading media items: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    /**
     * Load more media items (pagination)
     */
    fun loadMoreItems() {
        val currentState = _uiState.value

        // Don't load if already loading or no more items
        if (!currentState.canLoadMore()) {
            Timber.d("Cannot load more: isLoading=${currentState.isLoading}, isLoadingMore=${currentState.isLoadingMore}, hasMore=${currentState.hasMoreItems}")
            return
        }

        viewModelScope.launch {
            val nextPage = currentState.currentPage + 1
            val startIndex = nextPage * PAGE_SIZE

            Timber.d("Loading more items: page $nextPage, startIndex $startIndex")

            _uiState.update { it.copy(isLoadingMore = true, error = null) }

            when (val result = getMediaItemsUseCase.getPaginated(
                libraryId = currentState.selectedLibraryId,
                limit = PAGE_SIZE,
                startIndex = startIndex
            )) {
                is Result.Success -> {
                    val paginatedResult = result.data
                    Timber.d("Loaded ${paginatedResult.items.size} more items (total: ${currentState.mediaItems.size + paginatedResult.items.size} of ${paginatedResult.totalCount})")

                    _uiState.update {
                        it.copy(
                            mediaItems = it.mediaItems + paginatedResult.items,
                            totalItemCount = paginatedResult.totalCount,
                            hasMoreItems = paginatedResult.hasMore,
                            currentPage = nextPage,
                            isLoadingMore = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    Timber.e("Error loading more items: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    /**
     * Select a library to display its media items
     */
    fun selectLibrary(libraryId: String) {
        Timber.d("Selecting library: $libraryId")
        loadMediaItems(libraryId)
    }

    /**
     * Refresh both libraries and media items
     */
    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            // Reload libraries
            when (val result = getLibrariesUseCase()) {
                is Result.Success -> {
                    val libraries = result.data.filter { it.isVideoLibrary() }
                    _uiState.update { it.copy(libraries = libraries) }

                    // Reload media items for current library (reset to first page)
                    val currentLibraryId = _uiState.value.selectedLibraryId
                    if (currentLibraryId != null) {
                        when (val itemsResult = getMediaItemsUseCase.getPaginated(
                            libraryId = currentLibraryId,
                            limit = PAGE_SIZE,
                            startIndex = 0
                        )) {
                            is Result.Success -> {
                                val paginatedResult = itemsResult.data
                                _uiState.update {
                                    it.copy(
                                        mediaItems = paginatedResult.items,
                                        totalItemCount = paginatedResult.totalCount,
                                        hasMoreItems = paginatedResult.hasMore,
                                        currentPage = 0,
                                        isRefreshing = false,
                                        error = null
                                    )
                                }
                            }
                            is Result.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isRefreshing = false,
                                        error = itemsResult.message
                                    )
                                }
                            }
                            is Result.Loading -> {
                                // Already in loading state
                            }
                        }
                    } else {
                        _uiState.update { it.copy(isRefreshing = false) }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    /**
     * Dismiss the current error
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Retry loading after an error
     */
    fun retry() {
        if (_uiState.value.libraries.isEmpty()) {
            loadLibraries()
        } else {
            loadMediaItems(_uiState.value.selectedLibraryId)
        }
    }

    /**
     * Handle download button click
     * Start download if not downloaded, cancel if downloading
     */
    fun onDownloadClick(mediaItemId: String) {
        viewModelScope.launch {
            try {
                val mediaItem = _uiState.value.mediaItems.find { it.id == mediaItemId }
                if (mediaItem == null) {
                    Timber.w("Media item not found: $mediaItemId")
                    return@launch
                }

                when {
                    // Already downloaded - do nothing (could add delete option)
                    mediaItem.isDownloaded -> {
                        Timber.d("Media item already downloaded: $mediaItemId")
                    }
                    // Downloading - cancel download
                    mediaItem.downloadProgress > 0f && mediaItem.downloadProgress < 1f -> {
                        Timber.d("Cancelling download: $mediaItemId")
                        val result = cancelDownloadUseCase(mediaItemId)
                        if (result.isFailure) {
                            Timber.e("Error cancelling download: ${result.exceptionOrNull()?.message}")
                        }
                    }
                    // Not downloaded - start download
                    else -> {
                        Timber.d("Starting download: $mediaItemId")
                        val result = startDownloadUseCase(
                            mediaItemId = mediaItemId,
                            wifiOnly = true
                        )
                        if (result.isSuccess) {
                            Timber.d("Download started: ${result.getOrNull()}")
                        } else {
                            Timber.e("Error starting download: ${result.exceptionOrNull()?.message}")
                            _uiState.update {
                                it.copy(error = "Failed to start download: ${result.exceptionOrNull()?.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling download click")
                _uiState.update {
                    it.copy(error = "Download error: ${e.message}")
                }
            }
        }
    }
}
