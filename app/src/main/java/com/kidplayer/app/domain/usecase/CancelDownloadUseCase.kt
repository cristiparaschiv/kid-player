package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.manager.DownloadManager
import javax.inject.Inject

/**
 * Use case for cancelling a video download
 */
class CancelDownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    /**
     * Cancel a download by download ID
     */
    suspend fun byDownloadId(downloadId: String): Result<Unit> {
        return downloadManager.cancelDownload(downloadId)
    }

    /**
     * Cancel a download by media item ID
     */
    suspend operator fun invoke(mediaItemId: String): Result<Unit> {
        return downloadManager.cancelDownloadByMediaItemId(mediaItemId)
    }

    /**
     * Cancel all active downloads
     */
    suspend fun cancelAll(): Result<Int> {
        return downloadManager.cancelAllDownloads()
    }
}
