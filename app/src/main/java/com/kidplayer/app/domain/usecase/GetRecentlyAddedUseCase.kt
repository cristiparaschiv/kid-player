package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case for getting recently added media items
 * Returns newest videos across all libraries
 */
class GetRecentlyAddedUseCase @Inject constructor(
    private val jellyfinRepository: JellyfinRepository
) {
    suspend operator fun invoke(limit: Int = 20): Result<List<MediaItem>> {
        // Get all media items and sort by newest first
        return when (val result = jellyfinRepository.getMediaItems(null)) {
            is Result.Success -> {
                val sorted = result.data
                    .sortedByDescending { it.addedTimestamp }
                    .take(limit)
                Result.Success(sorted)
            }
            is Result.Error -> result
            is Result.Loading -> result
        }
    }
}
