package com.kidplayer.app.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kidplayer.app.data.local.dao.DownloadDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.entity.DownloadStatus
import com.kidplayer.app.data.manager.DownloadManager
import com.kidplayer.app.data.util.StorageManager
import com.kidplayer.app.domain.repository.JellyfinRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Periodic worker for syncing content and managing automatic downloads
 * Runs daily to:
 * 1. Refresh media library from Jellyfin
 * 2. Clean up old watched downloads
 * 3. Queue new downloads to maintain 1-2 hours of content
 */
@HiltWorker
class PeriodicSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val jellyfinRepository: JellyfinRepository,
    private val downloadManager: DownloadManager,
    private val mediaItemDao: MediaItemDao,
    private val downloadDao: DownloadDao,
    private val storageManager: StorageManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting periodic sync")

            // Step 1: Refresh media library from Jellyfin
            refreshMediaLibrary()

            // Step 2: Clean up old watched downloads to free space
            cleanupOldDownloads()

            // Step 3: Queue new downloads to maintain target duration
            queueNewDownloads()

            Timber.d("Periodic sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Periodic sync failed")
            Result.retry()
        }
    }

    /**
     * Refresh media library from Jellyfin
     */
    private suspend fun refreshMediaLibrary() {
        try {
            Timber.d("Refreshing media library")
            // Fetch latest media items from all libraries
            jellyfinRepository.getMediaItems(libraryId = null, limit = 100)
            Timber.d("Media library refreshed")
        } catch (e: Exception) {
            Timber.e(e, "Error refreshing media library")
            // Don't fail the whole sync if refresh fails
        }
    }

    /**
     * Clean up old watched downloads
     * Delete downloads for videos that have been watched (>90%)
     */
    private suspend fun cleanupOldDownloads() {
        try {
            Timber.d("Cleaning up old downloads")

            // Check if storage is low
            val isStorageLow = storageManager.isStorageLow()

            // Get all completed downloads (unscoped for background worker)
            val completedDownloads = downloadDao.getDownloadsByStatusUnscoped(DownloadStatus.COMPLETED)

            completedDownloads.collect { downloads ->
                var deletedCount = 0

                // Sort by completion timestamp (oldest first)
                val sortedDownloads = downloads.sortedBy { it.completedTimestamp ?: Long.MAX_VALUE }

                for (download in sortedDownloads) {
                    val mediaItem = mediaItemDao.getMediaItemByIdOnly(download.mediaItemId)

                    // Delete if watched or if storage is low
                    val shouldDelete = when {
                        mediaItem == null -> true // Media item deleted
                        mediaItem.watchedPercentage >= 0.9f -> true // Watched
                        isStorageLow -> true // Storage low, delete even unwatched
                        else -> false
                    }

                    if (shouldDelete) {
                        download.localFilePath?.let { filePath ->
                            if (storageManager.deleteDownloadedFile(filePath)) {
                                // Update media item
                                mediaItemDao.updateDownloadStatusByItemId(
                                    itemId = download.mediaItemId,
                                    isDownloaded = false,
                                    filePath = null
                                )

                                // Delete download record
                                downloadDao.deleteDownload(download.id)
                                deletedCount++

                                Timber.d("Deleted download: ${mediaItem?.title}")
                            }
                        }

                        // Stop deleting if storage is no longer low
                        if (isStorageLow && !storageManager.isStorageLow()) {
                            break
                        }
                    }
                }

                Timber.d("Cleanup completed: deleted $deletedCount downloads")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error cleaning up old downloads")
        }
    }

    /**
     * Queue new downloads to maintain target duration of offline content
     * Target: 1-2 hours of unwatched content
     */
    private suspend fun queueNewDownloads() {
        try {
            Timber.d("Queueing new downloads")

            // Check storage space
            if (storageManager.isStorageLow()) {
                Timber.w("Storage is low, skipping new downloads")
                return
            }

            // Get all media items
            val allMediaItems = mediaItemDao.getAllMediaItemsUnscoped()

            allMediaItems.collect { mediaItems ->
                // Filter: not downloaded, not watched, not currently downloading
                val candidatesForDownload = mediaItems.filter { mediaItem ->
                    !mediaItem.isDownloaded &&
                            mediaItem.watchedPercentage < 0.1f &&
                            downloadDao.getDownloadByMediaItemIdUnscoped(mediaItem.id) == null
                }

                // Sort by newest first (most recent content)
                val sortedCandidates = candidatesForDownload.sortedByDescending { it.addedTimestamp }

                // Calculate total duration of already downloaded unwatched content
                val downloadedUnwatched = mediaItems.filter { it.isDownloaded && it.watchedPercentage < 0.1f }
                val currentDurationSeconds = downloadedUnwatched.sumOf { it.duration / 10_000_000 } // Convert ticks to seconds

                Timber.d("Current offline content duration: ${currentDurationSeconds / 60} minutes")

                // Target: 1.5 hours (5400 seconds)
                val targetDurationSeconds = 5400L
                var accumulatedDuration = currentDurationSeconds
                var queuedCount = 0

                // Queue downloads until we reach target duration
                for (mediaItem in sortedCandidates) {
                    if (accumulatedDuration >= targetDurationSeconds) {
                        break
                    }

                    // Check storage space before each download
                    val estimatedSize = (mediaItem.duration / 10_000_000 / 60) * 2 * 1024 * 1024 // ~2MB per minute
                    if (!storageManager.hasEnoughSpace(estimatedSize)) {
                        Timber.w("Insufficient storage space for: ${mediaItem.title}")
                        break
                    }

                    // Queue download
                    val result = downloadManager.startDownload(
                        mediaItemId = mediaItem.id,
                        wifiOnly = true
                    )

                    if (result.isSuccess) {
                        accumulatedDuration += mediaItem.duration / 10_000_000
                        queuedCount++
                        Timber.d("Queued download: ${mediaItem.title}")
                    } else {
                        Timber.w("Failed to queue download: ${mediaItem.title}, error: ${result.exceptionOrNull()?.message}")
                    }

                    // Limit to 10 downloads per sync to avoid overwhelming the system
                    if (queuedCount >= MAX_DOWNLOADS_PER_SYNC) {
                        Timber.d("Reached max downloads per sync: $MAX_DOWNLOADS_PER_SYNC")
                        break
                    }
                }

                Timber.d("Queued $queuedCount new downloads, total offline duration: ${accumulatedDuration / 60} minutes")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error queueing new downloads")
        }
    }

    companion object {
        private const val MAX_DOWNLOADS_PER_SYNC = 10
    }
}
