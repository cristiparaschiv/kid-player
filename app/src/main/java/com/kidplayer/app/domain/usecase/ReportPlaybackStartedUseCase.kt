package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to report playback started to Jellyfin server
 * Must be called when playback begins to create a session
 * Required before Progress/Stopped calls will properly save position
 */
class ReportPlaybackStartedUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Report that playback has started
     *
     * @param itemId Media item ID
     */
    suspend operator fun invoke(itemId: String): Result<Unit> {
        return repository.reportPlaybackStarted(itemId)
    }
}
