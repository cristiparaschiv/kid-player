package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to report playback stopped to Jellyfin server
 * This should be called when the user exits the player or video ends
 */
class StopPlaybackUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Report that playback has stopped
     *
     * @param itemId Media item ID
     * @param positionMs Final playback position in milliseconds
     */
    suspend operator fun invoke(
        itemId: String,
        positionMs: Long
    ): Result<Unit> {
        // Convert milliseconds to ticks (Jellyfin uses ticks: 1 tick = 100 nanoseconds)
        val positionTicks = positionMs * 10_000
        return repository.reportPlaybackStopped(itemId, positionTicks)
    }
}
