package com.kidplayer.app.data.repository

import com.kidplayer.app.data.local.dao.FavoriteDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.entity.FavoriteEntity
import com.kidplayer.app.data.local.mapper.EntityMapper
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of FavoritesRepository
 * Manages user's favorite videos
 */
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val mediaItemDao: MediaItemDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<MediaItem>> {
        return favoriteDao.getAllFavorites().combine(
            mediaItemDao.getAllMediaItemsUnscoped()
        ) { favorites, mediaItems ->
            val favoriteIds = favorites.map { it.mediaItemId }.toSet()
            mediaItems
                .filter { it.id in favoriteIds }
                .map { EntityMapper.entityToMediaItem(it) }
        }
    }

    override fun isFavorite(mediaItemId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(mediaItemId)
    }

    override suspend fun addFavorite(mediaItemId: String, autoDownload: Boolean): Result<Unit> {
        return try {
            val favorite = FavoriteEntity(
                mediaItemId = mediaItemId,
                autoDownload = autoDownload,
                addedAt = System.currentTimeMillis()
            )
            favoriteDao.insertFavorite(favorite)
            Timber.d("Added favorite: $mediaItemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding favorite")
            Result.failure(e)
        }
    }

    override suspend fun removeFavorite(mediaItemId: String): Result<Unit> {
        return try {
            favoriteDao.deleteFavorite(mediaItemId)
            Timber.d("Removed favorite: $mediaItemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error removing favorite")
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(mediaItemId: String): Result<Boolean> {
        return try {
            val existing = favoriteDao.getFavorite(mediaItemId)
            if (existing != null) {
                favoriteDao.deleteFavorite(mediaItemId)
                Result.success(false)
            } else {
                val favorite = FavoriteEntity(
                    mediaItemId = mediaItemId,
                    addedAt = System.currentTimeMillis()
                )
                favoriteDao.insertFavorite(favorite)
                Result.success(true)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite")
            Result.failure(e)
        }
    }

    override suspend fun updateAutoDownload(mediaItemId: String, autoDownload: Boolean): Result<Unit> {
        return try {
            favoriteDao.updateAutoDownload(mediaItemId, autoDownload)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating auto-download")
            Result.failure(e)
        }
    }

    override fun getAutoDownloadFavorites(): Flow<List<MediaItem>> {
        return favoriteDao.getAutoDownloadFavorites().combine(
            mediaItemDao.getAllMediaItemsUnscoped()
        ) { autoDownloadFavorites, mediaItems ->
            val autoDownloadIds = autoDownloadFavorites.map { it.mediaItemId }.toSet()
            mediaItems
                .filter { it.id in autoDownloadIds }
                .map { EntityMapper.entityToMediaItem(it) }
        }
    }
}
