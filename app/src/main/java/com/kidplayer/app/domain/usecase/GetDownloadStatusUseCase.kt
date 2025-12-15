package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.local.entity.DownloadEntity
import com.kidplayer.app.data.manager.DownloadManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting download status
 */
class GetDownloadStatusUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    /**
     * Get download status by download ID
     */
    suspend fun byDownloadId(downloadId: String): DownloadEntity? {
        return downloadManager.getDownloadStatus(downloadId)
    }

    /**
     * Get download status by media item ID
     */
    suspend operator fun invoke(mediaItemId: String): DownloadEntity? {
        return downloadManager.getDownloadStatusByMediaItemId(mediaItemId)
    }

    /**
     * Observe download status changes
     */
    fun observe(downloadId: String): Flow<DownloadEntity?> {
        return downloadManager.observeDownload(downloadId)
    }

    /**
     * Check if media item is being downloaded
     */
    suspend fun isDownloading(mediaItemId: String): Boolean {
        return downloadManager.isDownloading(mediaItemId)
    }

    /**
     * Check if media item is downloaded
     */
    suspend fun isDownloaded(mediaItemId: String): Boolean {
        return downloadManager.isDownloaded(mediaItemId)
    }
}
