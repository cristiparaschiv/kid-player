package com.kidplayer.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.network.NetworkMonitor
import com.kidplayer.app.data.network.NetworkState
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import com.kidplayer.app.domain.usecase.CancelDownloadUseCase
import com.kidplayer.app.domain.usecase.GetFavoritesUseCase
import com.kidplayer.app.domain.usecase.GetLibrariesUseCase
import com.kidplayer.app.domain.usecase.GetMediaItemsUseCase
import com.kidplayer.app.domain.usecase.GetPlaylistsUseCase
import com.kidplayer.app.domain.usecase.StartDownloadUseCase
import com.kidplayer.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Home screen
 * Phase 6: Updated to show ALL media content with pagination like Browse screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jellyfinRepository: JellyfinRepository,
    private val getLibrariesUseCase: GetLibrariesUseCase,
    private val getMediaItemsUseCase: GetMediaItemsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val networkState: StateFlow<NetworkState> = networkMonitor.networkState

    companion object {
        private const val PAGE_SIZE = 100 // Load 100 items per page
    }

    init {
        checkServerConfiguration()
    }

    /**
     * Check if server is configured and load content accordingly
     */
    private fun checkServerConfiguration() {
        viewModelScope.launch {
            val serverConfig = jellyfinRepository.getServerConfig()
            val isConfigured = serverConfig != null

            _uiState.update { it.copy(isServerConfigured = isConfigured) }

            if (isConfigured) {
                // Server configured - load media content
                loadLibraries()
                loadPlaylists()
                loadFavorites()
            } else {
                // No server configured - stay in offline mode
                Timber.d("No server configured - running in offline mode")
            }
        }
    }

    /**
     * Load favorite video IDs
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favorites ->
                val favoriteIds = favorites.map { it.id }.toSet()
                _uiState.update { it.copy(favoriteIds = favoriteIds) }
            }
        }
    }

    /**
     * Toggle favorite status for a media item
     */
    fun toggleFavorite(mediaItemId: String) {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(mediaItemId)
            if (result.isSuccess) {
                val isFavorite = result.getOrNull() ?: false
                Timber.d("Toggled favorite for $mediaItemId: $isFavorite")
            } else {
                Timber.e("Error toggling favorite: ${result.exceptionOrNull()?.message}")
            }
        }
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

                    // Load all media items from all libraries
                    loadMediaItems(null)
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
     * Load playlists from repository
     */
    private fun loadPlaylists() {
        viewModelScope.launch {
            when (val result = getPlaylistsUseCase()) {
                is Result.Success -> {
                    val playlists = result.data
                    Timber.d("Loaded ${playlists.size} playlists")
                    _uiState.update { it.copy(playlists = playlists) }
                }
                is Result.Error -> {
                    Timber.w("Error loading playlists: ${result.message}")
                    // Don't show error for playlists - just leave them empty
                }
                is Result.Loading -> {
                    // Loading
                }
            }
        }
    }

    /**
     * Load media items from all libraries or specific library/playlist
     * Uses pagination to load initial batch of items
     *
     * @param libraryId Library ID to load items from (null for all libraries)
     * @param playlistId Playlist ID to load items from (takes precedence over libraryId)
     */
    private fun loadMediaItems(libraryId: String?, playlistId: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    currentPage = 0,
                    mediaItems = emptyList() // Clear existing items
                )
            }

            // For playlists, use playlistId as parentId
            val parentId = playlistId ?: libraryId

            when (val result = getMediaItemsUseCase.getPaginated(
                libraryId = parentId,
                limit = PAGE_SIZE,
                startIndex = 0
            )) {
                is Result.Success -> {
                    val paginatedResult = result.data
                    Timber.d("Loaded ${paginatedResult.items.size} of ${paginatedResult.totalCount} media items")

                    // Randomize video order for kids to discover new content each time
                    val shuffledItems = paginatedResult.items.shuffled()

                    _uiState.update {
                        it.copy(
                            mediaItems = shuffledItems,
                            selectedLibraryId = if (playlistId == null) libraryId else null,
                            selectedPlaylistId = playlistId,
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

            // Use playlist ID if in playlist mode, otherwise library ID
            val parentId = currentState.selectedPlaylistId ?: currentState.selectedLibraryId

            when (val result = getMediaItemsUseCase.getPaginated(
                libraryId = parentId,
                limit = PAGE_SIZE,
                startIndex = startIndex
            )) {
                is Result.Success -> {
                    val paginatedResult = result.data
                    Timber.d("Loaded ${paginatedResult.items.size} more items (total: ${currentState.mediaItems.size + paginatedResult.items.size} of ${paginatedResult.totalCount})")

                    // Note: We don't shuffle on pagination to maintain continuity
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
     * Clears any playlist selection
     */
    fun selectLibrary(libraryId: String?) {
        Timber.d("Selecting library: $libraryId")
        loadMediaItems(libraryId, playlistId = null)
    }

    /**
     * Select a playlist to display its media items
     * Clears any library selection
     */
    fun selectPlaylist(playlistId: String) {
        Timber.d("Selecting playlist: $playlistId")
        loadMediaItems(libraryId = null, playlistId = playlistId)
    }

    /**
     * Refresh libraries, playlists, and media items
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            // Reload libraries
            when (val result = getLibrariesUseCase()) {
                is Result.Success -> {
                    val libraries = result.data.filter { it.isVideoLibrary() }
                    _uiState.update { it.copy(libraries = libraries) }
                }
                is Result.Error -> {
                    Timber.w("Error refreshing libraries: ${result.message}")
                }
                is Result.Loading -> { }
            }

            // Reload playlists
            when (val result = getPlaylistsUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(playlists = result.data) }
                }
                is Result.Error -> {
                    Timber.w("Error refreshing playlists: ${result.message}")
                }
                is Result.Loading -> { }
            }

            // Reload media items (reset to first page)
            val currentState = _uiState.value
            val parentId = currentState.selectedPlaylistId ?: currentState.selectedLibraryId
            when (val itemsResult = getMediaItemsUseCase.getPaginated(
                libraryId = parentId,
                limit = PAGE_SIZE,
                startIndex = 0
            )) {
                is Result.Success -> {
                    val paginatedResult = itemsResult.data
                    // Randomize video order on refresh for discovery
                    val shuffledItems = paginatedResult.items.shuffled()
                    _uiState.update {
                        it.copy(
                            mediaItems = shuffledItems,
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
        val currentState = _uiState.value
        if (currentState.libraries.isEmpty()) {
            loadLibraries()
            loadPlaylists()
        } else {
            loadMediaItems(
                libraryId = currentState.selectedLibraryId,
                playlistId = currentState.selectedPlaylistId
            )
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
                    // Already downloaded - do nothing
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
