package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting recent search history
 */
class GetSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<String>> {
        return searchRepository.getRecentSearchHistory(limit)
    }
}
