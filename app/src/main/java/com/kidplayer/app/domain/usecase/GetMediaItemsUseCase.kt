package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.PaginatedResult
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to get media items from Jellyfin
 * Provides flexible querying with pagination support
 */
class GetMediaItemsUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Get media items
     *
     * @param libraryId Optional library ID to filter by
     * @param limit Maximum number of items to return
     * @param startIndex Pagination start index
     * @return Result containing list of media items or error
     */
    suspend operator fun invoke(
        libraryId: String? = null,
        limit: Int = 100,
        startIndex: Int = 0
    ): Result<List<MediaItem>> {
        // Validate pagination parameters
        if (limit <= 0) {
            return Result.Error("Limit must be greater than 0")
        }

        if (startIndex < 0) {
            return Result.Error("Start index cannot be negative")
        }

        return repository.getMediaItems(
            libraryId = libraryId,
            limit = limit,
            startIndex = startIndex
        )
    }

    /**
     * Get latest/recently added media items
     *
     * @param libraryId Optional library ID to filter by
     * @param limit Maximum number of items to return
     * @return Result containing list of media items or error
     */
    suspend fun getLatest(
        libraryId: String? = null,
        limit: Int = 20
    ): Result<List<MediaItem>> {
        if (limit <= 0) {
            return Result.Error("Limit must be greater than 0")
        }

        return repository.getLatestMediaItems(
            libraryId = libraryId,
            limit = limit
        )
    }

    /**
     * Get media items with pagination metadata
     *
     * @param libraryId Optional library ID to filter by
     * @param limit Maximum number of items to return
     * @param startIndex Pagination start index
     * @return Result containing paginated media items
     */
    suspend fun getPaginated(
        libraryId: String? = null,
        limit: Int = 100,
        startIndex: Int = 0
    ): Result<PaginatedResult<MediaItem>> {
        // Validate pagination parameters
        if (limit <= 0) {
            return Result.Error("Limit must be greater than 0")
        }

        if (startIndex < 0) {
            return Result.Error("Start index cannot be negative")
        }

        return repository.getMediaItemsPaginated(
            libraryId = libraryId,
            limit = limit,
            startIndex = startIndex
        )
    }
}
