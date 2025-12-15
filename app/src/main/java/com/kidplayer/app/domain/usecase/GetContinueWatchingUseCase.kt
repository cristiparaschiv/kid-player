package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.ContinueWatchingItem
import com.kidplayer.app.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting continue watching items
 * Returns videos that have been partially watched
 */
class GetContinueWatchingUseCase @Inject constructor(
    private val watchHistoryRepository: WatchHistoryRepository
) {
    operator fun invoke(limit: Int = 20): Flow<List<ContinueWatchingItem>> {
        return watchHistoryRepository.getContinueWatching(limit)
    }
}
