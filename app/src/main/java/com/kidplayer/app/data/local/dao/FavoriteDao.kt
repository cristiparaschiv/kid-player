package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Favorite entities
 * Provides operations for managing user's favorite videos
 */
@Dao
interface FavoriteDao {

    /**
     * Get all favorites ordered by most recently added
     */
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    /**
     * Get favorites with auto-download enabled
     */
    @Query("SELECT * FROM favorites WHERE autoDownload = 1")
    fun getAutoDownloadFavorites(): Flow<List<FavoriteEntity>>

    /**
     * Check if a media item is favorited
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mediaItemId = :mediaItemId LIMIT 1)")
    fun isFavorite(mediaItemId: String): Flow<Boolean>

    /**
     * Get a specific favorite
     */
    @Query("SELECT * FROM favorites WHERE mediaItemId = :mediaItemId LIMIT 1")
    suspend fun getFavorite(mediaItemId: String): FavoriteEntity?

    /**
     * Insert a favorite
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    /**
     * Update auto-download status for a favorite
     */
    @Query("UPDATE favorites SET autoDownload = :autoDownload WHERE mediaItemId = :mediaItemId")
    suspend fun updateAutoDownload(mediaItemId: String, autoDownload: Boolean)

    /**
     * Delete a favorite
     */
    @Query("DELETE FROM favorites WHERE mediaItemId = :mediaItemId")
    suspend fun deleteFavorite(mediaItemId: String)

    /**
     * Delete all favorites
     */
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()

    /**
     * Get count of favorites
     */
    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getFavoritesCount(): Int
}
