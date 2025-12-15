package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * Use case for adding a search query to history
 */
class AddSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<Unit> {
        return searchRepository.addSearchHistory(query)
    }
}
