package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for getting the next video to play
 * For series: returns next episode
 * For movies: returns a random video from same library
 * When offline: only returns downloaded videos
 */
class GetNextVideoUseCase @Inject constructor(
    private val jellyfinRepository: JellyfinRepository
) {
    /**
     * Get the next video to play
     * @param currentVideoId The ID of the currently playing video
     * @param offlineOnly When true, only return downloaded videos (for offline mode)
     */
    suspend operator fun invoke(currentVideoId: String, offlineOnly: Boolean = false): Result<MediaItem?> {
        return try {
            // Get all media items
            when (val result = jellyfinRepository.getMediaItems(null)) {
                is Result.Success -> {
                    val allItems = result.data
                    val currentItem = allItems.find { it.id == currentVideoId }

                    if (currentItem == null) {
                        Timber.w("Current item not found: $currentVideoId")
                        return Result.Success(null)
                    }

                    val nextItem = findNextVideo(currentItem, allItems, offlineOnly)
                    Result.Success(nextItem)
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting next video")
            Result.Error("Failed to get next video: ${e.message}")
        }
    }

    /**
     * Find the next video to play
     * @param offlineOnly When true, only consider downloaded videos
     */
    private fun findNextVideo(
        currentItem: MediaItem,
        allItems: List<MediaItem>,
        offlineOnly: Boolean
    ): MediaItem? {
        // Filter to downloaded-only if in offline mode
        val availableItems = if (offlineOnly) {
            allItems.filter { it.isDownloaded }.also {
                Timber.d("Offline mode: ${it.size} downloaded videos available for autoplay")
            }
        } else {
            allItems
        }

        // If it's an episode, try to find next episode
        if (currentItem.isEpisode()) {
            val seriesName = currentItem.seriesName
            val seasonNumber = currentItem.seasonNumber
            val episodeNumber = currentItem.episodeNumber

            if (seriesName != null && seasonNumber != null && episodeNumber != null) {
                // Try to find next episode in same season
                val nextEpisode = availableItems.find {
                    it.seriesName == seriesName &&
                    it.seasonNumber == seasonNumber &&
                    it.episodeNumber == episodeNumber + 1
                }

                if (nextEpisode != null) {
                    Timber.d("Found next episode: ${nextEpisode.title}")
                    return nextEpisode
                }

                // Try first episode of next season
                val nextSeasonFirstEpisode = availableItems.find {
                    it.seriesName == seriesName &&
                    it.seasonNumber == seasonNumber + 1 &&
                    it.episodeNumber == 1
                }

                if (nextSeasonFirstEpisode != null) {
                    Timber.d("Found first episode of next season: ${nextSeasonFirstEpisode.title}")
                    return nextSeasonFirstEpisode
                }
            }
        }

        // For movies or if no next episode found, return a random video from same library
        val sameLibraryItems = availableItems.filter {
            it.libraryId == currentItem.libraryId && it.id != currentItem.id
        }

        return sameLibraryItems.randomOrNull()?.also {
            Timber.d("Selected random next video: ${it.title}")
        }
    }
}
