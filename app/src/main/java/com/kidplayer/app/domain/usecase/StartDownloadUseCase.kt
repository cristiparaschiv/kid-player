package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.manager.DownloadManager
import javax.inject.Inject

/**
 * Use case for starting a video download
 */
class StartDownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    /**
     * Start downloading a media item
     * @param mediaItemId Media item to download
     * @param wifiOnly Only download on WiFi (default: true)
     * @return Download ID or error
     */
    suspend operator fun invoke(
        mediaItemId: String,
        wifiOnly: Boolean = true
    ): Result<String> {
        return downloadManager.startDownload(mediaItemId, wifiOnly)
    }
}
