package com.kidplayer.app.data.manager

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kidplayer.app.data.local.dao.DownloadDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.entity.DownloadEntity
import com.kidplayer.app.data.local.entity.DownloadStatus
import com.kidplayer.app.data.util.StorageManager
import com.kidplayer.app.data.worker.VideoDownloadWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages video downloads using WorkManager
 * Handles queueing, cancellation, and status tracking
 */
@Singleton
class DownloadManager @Inject constructor(
    private val workManager: WorkManager,
    private val downloadDao: DownloadDao,
    private val mediaItemDao: MediaItemDao,
    private val storageManager: StorageManager,
    private val securePreferences: com.kidplayer.app.data.local.SecurePreferences
) {

    /**
     * Start downloading a media item
     * @param mediaItemId Media item to download
     * @param wifiOnly Only download on WiFi
     * @return Download ID
     */
    suspend fun startDownload(
        mediaItemId: String,
        wifiOnly: Boolean = true
    ): Result<String> {
        return try {
            // Get current user ID
            val userId = securePreferences.getUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // Check if already downloading or downloaded
            val existingDownload = downloadDao.getDownloadByMediaItemId(userId, mediaItemId)
            if (existingDownload != null) {
                when (existingDownload.status) {
                    DownloadStatus.COMPLETED -> {
                        return Result.failure(Exception("Already downloaded"))
                    }
                    DownloadStatus.DOWNLOADING, DownloadStatus.PENDING -> {
                        return Result.failure(Exception("Download already in progress"))
                    }
                    DownloadStatus.FAILED, DownloadStatus.CANCELLED -> {
                        // Delete old failed/cancelled download and continue
                        downloadDao.deleteDownload(existingDownload.id)
                    }
                }
            }

            // Get media item to estimate size
            val mediaItem = mediaItemDao.getMediaItemByIdOnly(mediaItemId)
                ?: return Result.failure(Exception("Media item not found"))

            // Check storage space (estimate based on duration, ~2MB per minute for HD)
            val estimatedSize = (mediaItem.duration / 10_000_000 / 60) * 2 * 1024 * 1024 // bytes
            if (!storageManager.hasEnoughSpace(estimatedSize)) {
                return Result.failure(Exception("Insufficient storage space"))
            }

            // Create download entity
            val downloadId = UUID.randomUUID().toString()
            val download = DownloadEntity(
                id = downloadId,
                mediaItemId = mediaItemId,
                userId = userId,
                status = DownloadStatus.PENDING,
                progress = 0f,
                totalBytes = estimatedSize
            )
            downloadDao.insertDownload(download)

            // Create work constraints
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(
                    if (wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
                )
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()

            // Create work request
            val workRequest = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
                .setInputData(
                    workDataOf(
                        VideoDownloadWorker.KEY_DOWNLOAD_ID to downloadId,
                        VideoDownloadWorker.KEY_MEDIA_ITEM_ID to mediaItemId
                    )
                )
                .setConstraints(constraints)
                .addTag(TAG_DOWNLOAD)
                .addTag(TAG_DOWNLOAD_PREFIX + mediaItemId)
                .build()

            // Update download with work request ID
            downloadDao.updateWorkRequestId(downloadId, workRequest.id.toString())

            // Enqueue work
            workManager.enqueueUniqueWork(
                "download_$mediaItemId",
                ExistingWorkPolicy.KEEP,
                workRequest
            )

            Timber.d("Download started: $downloadId for media item: $mediaItemId")
            Result.success(downloadId)
        } catch (e: Exception) {
            Timber.e(e, "Error starting download for media item: $mediaItemId")
            Result.failure(e)
        }
    }

    /**
     * Cancel a download
     * @param downloadId Download to cancel
     */
    suspend fun cancelDownload(downloadId: String): Result<Unit> {
        return try {
            val download = downloadDao.getDownloadById(downloadId)
                ?: return Result.failure(Exception("Download not found"))

            // Cancel work if in progress
            download.workRequestId?.let { workRequestId ->
                workManager.cancelWorkById(UUID.fromString(workRequestId))
            }

            // Update download status
            downloadDao.updateDownloadStatus(downloadId, DownloadStatus.CANCELLED)

            // Delete partial file if exists
            download.localFilePath?.let { filePath ->
                storageManager.deleteDownloadedFile(filePath)
            }

            Timber.d("Download cancelled: $downloadId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling download: $downloadId")
            Result.failure(e)
        }
    }

    /**
     * Cancel download by media item ID
     */
    suspend fun cancelDownloadByMediaItemId(mediaItemId: String): Result<Unit> {
        return try {
            val userId = securePreferences.getUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val download = downloadDao.getDownloadByMediaItemId(userId, mediaItemId)
                ?: return Result.failure(Exception("Download not found"))

            cancelDownload(download.id)
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling download for media item: $mediaItemId")
            Result.failure(e)
        }
    }

    /**
     * Cancel all active downloads
     */
    suspend fun cancelAllDownloads(): Result<Int> {
        return try {
            val userId = securePreferences.getUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // Cancel all work with download tag
            workManager.cancelAllWorkByTag(TAG_DOWNLOAD)

            // Update all active downloads to cancelled
            val activeDownloads = downloadDao.getPendingDownloads(userId)
            activeDownloads.forEach { download ->
                downloadDao.updateDownloadStatus(download.id, DownloadStatus.CANCELLED)
            }

            Timber.d("Cancelled ${activeDownloads.size} downloads")
            Result.success(activeDownloads.size)
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling all downloads")
            Result.failure(e)
        }
    }

    /**
     * Get download status by ID
     */
    suspend fun getDownloadStatus(downloadId: String): DownloadEntity? {
        return downloadDao.getDownloadById(downloadId)
    }

    /**
     * Get download status by media item ID
     */
    suspend fun getDownloadStatusByMediaItemId(mediaItemId: String): DownloadEntity? {
        val userId = securePreferences.getUserId() ?: return null
        return downloadDao.getDownloadByMediaItemId(userId, mediaItemId)
    }

    /**
     * Observe download status
     */
    fun observeDownload(mediaItemId: String): Flow<DownloadEntity?> {
        return flow {
            val userId = securePreferences.getUserId()
            if (userId != null) {
                downloadDao.observeDownloadByMediaItemId(userId, mediaItemId).collect { emit(it) }
            } else {
                emit(null)
            }
        }
    }

    /**
     * Get all active downloads
     */
    fun getActiveDownloads(): Flow<List<DownloadEntity>> {
        return flow {
            val userId = securePreferences.getUserId()
            if (userId != null) {
                downloadDao.getActiveDownloads(userId).collect { emit(it) }
            } else {
                emit(emptyList())
            }
        }
    }

    /**
     * Get all completed downloads
     */
    fun getCompletedDownloads(): Flow<List<DownloadEntity>> {
        return flow {
            val userId = securePreferences.getUserId()
            if (userId != null) {
                downloadDao.getCompletedDownloads(userId).collect { emit(it) }
            } else {
                emit(emptyList())
            }
        }
    }

    /**
     * Get download work info
     */
    fun getDownloadWorkInfo(workRequestId: String): Flow<WorkInfo?> {
        return workManager.getWorkInfoByIdFlow(UUID.fromString(workRequestId))
    }

    /**
     * Check if a media item is being downloaded
     */
    suspend fun isDownloading(mediaItemId: String): Boolean {
        val userId = securePreferences.getUserId() ?: return false
        val download = downloadDao.getDownloadByMediaItemId(userId, mediaItemId)
        return download?.status in listOf(DownloadStatus.PENDING, DownloadStatus.DOWNLOADING)
    }

    /**
     * Check if a media item is downloaded
     */
    suspend fun isDownloaded(mediaItemId: String): Boolean {
        val userId = securePreferences.getUserId() ?: return false
        val download = downloadDao.getDownloadByMediaItemId(userId, mediaItemId)
        return download?.status == DownloadStatus.COMPLETED
    }

    /**
     * Delete a downloaded file and update database
     */
    suspend fun deleteDownload(downloadId: String): Result<Unit> {
        return try {
            val download = downloadDao.getDownloadById(downloadId)
                ?: return Result.failure(Exception("Download not found"))

            // Delete file
            download.localFilePath?.let { filePath ->
                storageManager.deleteDownloadedFile(filePath)
            }

            // Update media item
            mediaItemDao.updateDownloadStatusByItemId(
                itemId = download.mediaItemId,
                isDownloaded = false,
                filePath = null
            )

            // Delete download record
            downloadDao.deleteDownload(downloadId)

            Timber.d("Download deleted: $downloadId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting download: $downloadId")
            Result.failure(e)
        }
    }

    /**
     * Clean up old completed downloads
     * Removes downloads that are completed and media items are watched
     */
    suspend fun cleanupOldDownloads(): Result<Int> {
        return try {
            val userId = securePreferences.getUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val completedDownloads = downloadDao.getCompletedDownloads(userId)
            var deletedCount = 0

            completedDownloads.collect { downloads ->
                downloads.forEach { download ->
                    val mediaItem = mediaItemDao.getMediaItemByIdOnly(download.mediaItemId)
                    if (mediaItem != null && mediaItem.watchedPercentage >= 0.9f) {
                        // Media item is watched, delete download
                        deleteDownload(download.id)
                        deletedCount++
                    }
                }
            }

            Timber.d("Cleaned up $deletedCount old downloads")
            Result.success(deletedCount)
        } catch (e: Exception) {
            Timber.e(e, "Error cleaning up old downloads")
            Result.failure(e)
        }
    }

    /**
     * Retry a failed download
     */
    suspend fun retryDownload(downloadId: String): Result<String> {
        return try {
            val download = downloadDao.getDownloadById(downloadId)
                ?: return Result.failure(Exception("Download not found"))

            if (download.status != DownloadStatus.FAILED) {
                return Result.failure(Exception("Download is not in failed state"))
            }

            // Delete old download
            downloadDao.deleteDownload(downloadId)

            // Start new download
            startDownload(download.mediaItemId)
        } catch (e: Exception) {
            Timber.e(e, "Error retrying download: $downloadId")
            Result.failure(e)
        }
    }

    /**
     * Get download statistics
     */
    suspend fun getDownloadStats(): DownloadStats {
        val userId = securePreferences.getUserId()
        if (userId == null) {
            return DownloadStats(
                totalDownloads = 0,
                activeDownloads = 0,
                failedDownloads = 0,
                totalSizeBytes = 0L
            )
        }

        val totalDownloads = downloadDao.getDownloadCountByStatus(userId, DownloadStatus.COMPLETED)
        val activeDownloads = downloadDao.getDownloadCountByStatus(userId, DownloadStatus.DOWNLOADING) +
                downloadDao.getDownloadCountByStatus(userId, DownloadStatus.PENDING)
        val failedDownloads = downloadDao.getDownloadCountByStatus(userId, DownloadStatus.FAILED)
        val totalSize = downloadDao.getTotalDownloadedSize(userId) ?: 0L

        return DownloadStats(
            totalDownloads = totalDownloads,
            activeDownloads = activeDownloads,
            failedDownloads = failedDownloads,
            totalSizeBytes = totalSize
        )
    }

    companion object {
        private const val TAG_DOWNLOAD = "video_download"
        private const val TAG_DOWNLOAD_PREFIX = "download_"
    }
}

/**
 * Download statistics data class
 */
data class DownloadStats(
    val totalDownloads: Int,
    val activeDownloads: Int,
    val failedDownloads: Int,
    val totalSizeBytes: Long
)
