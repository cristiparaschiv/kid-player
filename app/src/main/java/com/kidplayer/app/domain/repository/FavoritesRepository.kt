package com.kidplayer.app.domain.repository

import com.kidplayer.app.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for favorites operations
 */
interface FavoritesRepository {

    /**
     * Get all favorite media items
     */
    fun getFavorites(): Flow<List<MediaItem>>

    /**
     * Check if a media item is favorited
     */
    fun isFavorite(mediaItemId: String): Flow<Boolean>

    /**
     * Add a media item to favorites
     */
    suspend fun addFavorite(mediaItemId: String, autoDownload: Boolean = false): Result<Unit>

    /**
     * Remove a media item from favorites
     */
    suspend fun removeFavorite(mediaItemId: String): Result<Unit>

    /**
     * Toggle favorite status for a media item
     */
    suspend fun toggleFavorite(mediaItemId: String): Result<Boolean>

    /**
     * Update auto-download setting for a favorite
     */
    suspend fun updateAutoDownload(mediaItemId: String, autoDownload: Boolean): Result<Unit>

    /**
     * Get favorites with auto-download enabled
     */
    fun getAutoDownloadFavorites(): Flow<List<MediaItem>>
}
