package com.kidplayer.app.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.kidplayer.app.R
import com.kidplayer.app.data.local.SecurePreferences
import com.kidplayer.app.data.local.dao.DownloadDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.entity.DownloadStatus
import com.kidplayer.app.data.remote.JellyfinApiProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * WorkManager worker for downloading videos in the background
 * Supports progress reporting, retry logic, and foreground notifications
 */
@HiltWorker
class VideoDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val jellyfinApiProvider: JellyfinApiProvider,
    private val downloadDao: DownloadDao,
    private val mediaItemDao: MediaItemDao,
    private val securePreferences: SecurePreferences
) : CoroutineWorker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val downloadId = inputData.getString(KEY_DOWNLOAD_ID)
        val mediaItemId = inputData.getString(KEY_MEDIA_ITEM_ID)

        if (downloadId == null || mediaItemId == null) {
            Timber.e("Download ID or Media Item ID is null")
            return Result.failure()
        }

        return try {
            // Get download entity
            val download = downloadDao.getDownloadById(downloadId)
            if (download == null) {
                Timber.e("Download not found: $downloadId")
                return Result.failure()
            }

            // Get media item
            val mediaItem = mediaItemDao.getMediaItemByIdOnly(mediaItemId)
            if (mediaItem == null) {
                Timber.e("Media item not found: $mediaItemId")
                downloadDao.markDownloadAsFailed(downloadId, "Media item not found")
                return Result.failure()
            }

            // Get server URL and auth token
            val serverUrl = securePreferences.getServerUrl()
            val authToken = securePreferences.getAuthToken()
            if (serverUrl.isNullOrBlank() || authToken.isNullOrBlank()) {
                Timber.e("Server URL or auth token not found")
                downloadDao.markDownloadAsFailed(downloadId, "Not authenticated")
                return Result.failure()
            }

            // Get the API for the configured server
            val jellyfinApi = jellyfinApiProvider.getApi(serverUrl)

            // Set worker as foreground service
            setForeground(createForegroundInfo(mediaItem.title, 0f))

            // Update download status to DOWNLOADING
            downloadDao.updateDownloadStatus(downloadId, DownloadStatus.DOWNLOADING)

            // Create downloads directory
            val downloadsDir = File(context.getExternalFilesDir(null), "downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            // Create output file
            val fileName = "${mediaItem.jellyfinItemId}.mp4"
            val outputFile = File(downloadsDir, fileName)

            // Download video
            Timber.d("Starting download: ${mediaItem.title}")
            val response = jellyfinApi.downloadVideo(
                itemId = mediaItem.jellyfinItemId,
                authToken = authToken
            )

            if (!response.isSuccessful) {
                val errorMsg = "Download failed: HTTP ${response.code()}"
                Timber.e(errorMsg)
                downloadDao.markDownloadAsFailed(downloadId, errorMsg)
                return Result.failure()
            }

            val responseBody = response.body()
            if (responseBody == null) {
                val errorMsg = "Download response body is null"
                Timber.e(errorMsg)
                downloadDao.markDownloadAsFailed(downloadId, errorMsg)
                return Result.failure()
            }

            // Get total size
            val totalBytes = responseBody.contentLength()
            Timber.d("Download size: $totalBytes bytes")

            // Download with progress
            responseBody.byteStream().use { input ->
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8 * 1024) // 8KB buffer
                    var bytesRead: Int
                    var downloadedBytes = 0L
                    var lastProgressUpdate = 0f

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        // Calculate progress
                        val progress = if (totalBytes > 0) {
                            downloadedBytes.toFloat() / totalBytes.toFloat()
                        } else {
                            0f
                        }

                        // Update progress every 5%
                        if (progress - lastProgressUpdate >= 0.05f || progress >= 0.99f) {
                            lastProgressUpdate = progress

                            // Update database
                            downloadDao.updateDownloadProgress(
                                downloadId = downloadId,
                                progress = progress,
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes
                            )

                            // Update media item
                            mediaItemDao.updateMediaItem(
                                mediaItem.copy(downloadProgress = progress)
                            )

                            // Update WorkManager progress
                            setProgress(
                                workDataOf(
                                    KEY_PROGRESS to progress,
                                    KEY_DOWNLOADED_BYTES to downloadedBytes,
                                    KEY_TOTAL_BYTES to totalBytes
                                )
                            )

                            // Update notification
                            setForeground(createForegroundInfo(mediaItem.title, progress))

                            Timber.d("Download progress: ${(progress * 100).toInt()}%")
                        }
                    }
                }
            }

            // Mark as completed
            val filePath = outputFile.absolutePath
            downloadDao.completeDownload(
                downloadId = downloadId,
                status = DownloadStatus.COMPLETED,
                filePath = filePath
            )

            // Update media item
            mediaItemDao.updateDownloadStatusByItemId(
                itemId = mediaItemId,
                isDownloaded = true,
                filePath = filePath
            )

            // Update media item progress to 1.0
            mediaItemDao.updateMediaItem(
                mediaItem.copy(
                    isDownloaded = true,
                    downloadProgress = 1.0f,
                    localFilePath = filePath
                )
            )

            Timber.d("Download completed: ${mediaItem.title}")

            // Show completion notification
            showCompletionNotification(mediaItem.title)

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Download failed for item: $mediaItemId")

            // Mark as failed in database
            downloadId?.let {
                downloadDao.markDownloadAsFailed(it, e.message ?: "Unknown error")
            }

            // Retry up to 3 times
            if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                Timber.d("Retrying download (attempt ${runAttemptCount + 1}/$MAX_RETRY_ATTEMPTS)")
                Result.retry()
            } else {
                Timber.e("Max retry attempts reached")
                Result.failure(
                    workDataOf(KEY_ERROR_MESSAGE to (e.message ?: "Download failed"))
                )
            }
        }
    }

    /**
     * Create foreground info for download notification
     * Uses DATA_SYNC foreground service type for Android 14+
     */
    private fun createForegroundInfo(title: String, progress: Float): ForegroundInfo {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Downloading: $title")
            .setContentText(if (progress > 0) "${(progress * 100).toInt()}%" else "Starting...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, (progress * 100).toInt(), progress == 0f)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ requires foreground service type
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Show completion notification
     */
    private fun showCompletionNotification(title: String) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Download Complete")
            .setContentText(title)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(title.hashCode(), notification)
    }

    /**
     * Create notification channel for Android O+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for video downloads"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val KEY_DOWNLOAD_ID = "download_id"
        const val KEY_MEDIA_ITEM_ID = "media_item_id"
        const val KEY_PROGRESS = "progress"
        const val KEY_DOWNLOADED_BYTES = "downloaded_bytes"
        const val KEY_TOTAL_BYTES = "total_bytes"
        const val KEY_ERROR_MESSAGE = "error_message"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "video_downloads"
        private const val MAX_RETRY_ATTEMPTS = 3
    }
}
