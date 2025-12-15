package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for checking if a media item is favorited
 */
class IsFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(mediaItemId: String): Flow<Boolean> {
        return favoritesRepository.isFavorite(mediaItemId)
    }
}
