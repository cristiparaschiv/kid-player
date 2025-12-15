package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for toggling favorite status of a media item
 * Returns true if favorited, false if unfavorited
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(mediaItemId: String): Result<Boolean> {
        return favoritesRepository.toggleFavorite(mediaItemId)
    }
}
