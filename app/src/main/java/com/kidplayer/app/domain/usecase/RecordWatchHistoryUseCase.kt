package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.WatchHistoryRepository
import javax.inject.Inject

/**
 * Use case for recording watch history
 * Called during video playback to track progress
 */
class RecordWatchHistoryUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
    suspend operator fun invoke(
        mediaItemId: String,
        watchedPercentage: Float,
        duration: Long,
        positionMs: Long
    ): Result<Unit> {
        return watchHistoryRepository.recordWatchHistory(
            mediaItemId = mediaItemId,
            watchedPercentage = watchedPercentage,
            duration = duration,
            positionMs = positionMs
        )
    }
}
