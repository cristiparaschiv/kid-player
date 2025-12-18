package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to get a single media item by ID
 * Fetches fresh data from Jellyfin API to get current playback position
 */
class GetMediaItemUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    suspend operator fun invoke(itemId: String): Result<MediaItem> {
        return repository.getMediaItem(itemId)
    }
}
