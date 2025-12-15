package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting favorite media items
 */
class GetFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return favoritesRepository.getFavorites()
    }
}
