package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to get streaming URL for a media item
 * Returns the direct streaming URL that can be used with ExoPlayer
 */
class GetStreamingUrlUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    suspend operator fun invoke(itemId: String): Result<String> {
        return repository.getStreamingUrl(itemId)
    }
}
