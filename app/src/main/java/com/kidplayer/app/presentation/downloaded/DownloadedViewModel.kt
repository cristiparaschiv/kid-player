package com.kidplayer.app.presentation.downloaded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.local.dao.DownloadDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.mapper.EntityMapper
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.PinVerificationResult
import com.kidplayer.app.domain.usecase.CancelDownloadUseCase
import com.kidplayer.app.domain.usecase.DeleteDownloadUseCase
import com.kidplayer.app.domain.usecase.VerifyParentPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Downloaded screen
 * Displays all downloaded media items with management options
 */
@HiltViewModel
class DownloadedViewModel @Inject constructor(
    private val downloadDao: DownloadDao,
    private val mediaItemDao: MediaItemDao,
    private val cancelDownloadUseCase: CancelDownloadUseCase,
    private val deleteDownloadUseCase: DeleteDownloadUseCase,
    private val verifyParentPinUseCase: VerifyParentPinUseCase,
    private val securePreferences: com.kidplayer.app.data.local.SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadedUiState())
    val uiState: StateFlow<DownloadedUiState> = _uiState.asStateFlow()

    init {
        loadDownloadedItems()
    }

    /**
     * Load all downloaded and downloading media items
     */
    private fun loadDownloadedItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Get current user ID
                val userId = securePreferences.getUserId()
                if (userId == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "User not authenticated"
                        )
                    }
                    return@launch
                }

                // Combine completed downloads and active downloads
                combine(
                    downloadDao.getCompletedDownloads(userId),
                    downloadDao.getActiveDownloads(userId)
                ) { completed, active ->
                    Pair(completed, active)
                }.collect { (completedDownloads, activeDownloads) ->

                    // Get media items for completed downloads and convert to domain models
                    val completedMediaItems = completedDownloads.mapNotNull { download ->
                        mediaItemDao.getMediaItemByIdOnly(download.mediaItemId)?.let { entity ->
                            EntityMapper.entityToMediaItem(entity)
                        }
                    }.sortedByDescending { it.title }

                    // Get media items for active downloads and convert to domain models
                    // Use progress from DownloadEntity (updated by worker) for accurate real-time progress
                    val activeMediaItems = activeDownloads.mapNotNull { download ->
                        mediaItemDao.getMediaItemByIdOnly(download.mediaItemId)?.let { entity ->
                            EntityMapper.entityToMediaItem(entity).copy(
                                downloadProgress = download.progress // Use progress from download entity
                            )
                        }
                    }.sortedByDescending { it.title }

                    _uiState.update {
                        it.copy(
                            downloadedItems = completedMediaItems,
                            downloadingItems = activeMediaItems,
                            isLoading = false,
                            error = null
                        )
                    }

                    Timber.d("Loaded ${completedMediaItems.size} downloaded items and ${activeMediaItems.size} downloading items")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading downloaded items")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load downloads: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Refresh downloaded items
     */
    fun refresh() {
        loadDownloadedItems()
    }

    /**
     * Cancel an active download
     */
    fun cancelDownload(mediaItemId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Cancelling download: $mediaItemId")
                val result = cancelDownloadUseCase(mediaItemId)

                if (result.isFailure) {
                    val error = result.exceptionOrNull()?.message ?: "Failed to cancel download"
                    Timber.e("Error cancelling download: $error")
                    _uiState.update { it.copy(error = error) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error cancelling download")
                _uiState.update { it.copy(error = "Failed to cancel download: ${e.message}") }
            }
        }
    }

    /**
     * Request to delete a downloaded item - shows PIN dialog first
     */
    fun requestDeleteDownload(mediaItemId: String) {
        _uiState.update {
            it.copy(
                showPinDialog = true,
                pendingDeleteMediaItemId = mediaItemId,
                pinError = false,
                pinErrorMessage = null
            )
        }
    }

    /**
     * Verify PIN and delete if successful
     */
    fun verifyPinAndDelete(pin: String) {
        viewModelScope.launch {
            val pendingMediaItemId = _uiState.value.pendingDeleteMediaItemId
            if (pendingMediaItemId == null) {
                dismissPinDialog()
                return@launch
            }

            when (verifyParentPinUseCase(pin)) {
                is PinVerificationResult.Success -> {
                    // PIN correct - proceed with delete
                    dismissPinDialog()
                    performDelete(pendingMediaItemId)
                }
                is PinVerificationResult.Failure -> {
                    // Wrong PIN
                    _uiState.update {
                        it.copy(
                            pinError = true,
                            pinErrorMessage = "Incorrect PIN. Please try again."
                        )
                    }
                }
                is PinVerificationResult.NotSet -> {
                    // No PIN set - allow delete (parent hasn't set up PIN yet)
                    dismissPinDialog()
                    performDelete(pendingMediaItemId)
                }
            }
        }
    }

    /**
     * Dismiss PIN dialog
     */
    fun dismissPinDialog() {
        _uiState.update {
            it.copy(
                showPinDialog = false,
                pendingDeleteMediaItemId = null,
                pinError = false,
                pinErrorMessage = null
            )
        }
    }

    /**
     * Actually delete a downloaded item (internal, called after PIN verification)
     */
    private fun performDelete(mediaItemId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Deleting download: $mediaItemId")

                // Get current user ID
                val userId = securePreferences.getUserId()
                if (userId == null) {
                    _uiState.update { it.copy(error = "User not authenticated") }
                    return@launch
                }

                // Get the download entity to get the download ID
                val download = downloadDao.getDownloadByMediaItemId(userId, mediaItemId)
                if (download != null) {
                    val result = deleteDownloadUseCase(download.id)

                    if (result.isFailure) {
                        val error = result.exceptionOrNull()?.message ?: "Failed to delete download"
                        Timber.e("Error deleting download: $error")
                        _uiState.update { it.copy(error = error) }
                    } else {
                        Timber.d("Download deleted successfully")
                    }
                } else {
                    Timber.w("Download not found for media item: $mediaItemId")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting download")
                _uiState.update { it.copy(error = "Failed to delete download: ${e.message}") }
            }
        }
    }

    /**
     * Dismiss current error
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Retry loading after error
     */
    fun retry() {
        loadDownloadedItems()
    }
}

/**
 * UI state for Downloaded screen
 */
data class DownloadedUiState(
    val downloadedItems: List<MediaItem> = emptyList(),
    val downloadingItems: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    // PIN dialog state for delete protection
    val showPinDialog: Boolean = false,
    val pendingDeleteMediaItemId: String? = null,
    val pinError: Boolean = false,
    val pinErrorMessage: String? = null
) {
    fun isEmpty(): Boolean = downloadedItems.isEmpty() && downloadingItems.isEmpty() && !isLoading

    fun hasError(): Boolean = error != null

    fun getTotalCount(): Int = downloadedItems.size + downloadingItems.size
}
