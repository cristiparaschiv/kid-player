package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.manager.DownloadManager
import javax.inject.Inject

/**
 * Use case for deleting a downloaded video
 */
class DeleteDownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    /**
     * Delete a download by download ID
     */
    suspend operator fun invoke(downloadId: String): Result<Unit> {
        return downloadManager.deleteDownload(downloadId)
    }
}
