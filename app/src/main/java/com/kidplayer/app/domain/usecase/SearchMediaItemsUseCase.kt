package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * Use case for searching media items
 */
class SearchMediaItemsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<MediaItem>> {
        return searchRepository.searchMediaItems(query)
    }
}
