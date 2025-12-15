package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to report playback progress to Jellyfin server
 * This allows the server to track watch progress and resume position
 */
class ReportPlaybackProgressUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Report current playback progress
     *
     * @param itemId Media item ID
     * @param positionMs Current playback position in milliseconds
     * @param isPaused Whether playback is currently paused
     */
    suspend operator fun invoke(
        itemId: String,
        positionMs: Long,
        isPaused: Boolean = false
    ): Result<Unit> {
        // Convert milliseconds to ticks (Jellyfin uses ticks: 1 tick = 100 nanoseconds)
        val positionTicks = positionMs * 10_000
        return repository.reportPlaybackProgress(itemId, positionTicks, isPaused)
    }
}
