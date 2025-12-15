package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.MediaItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for MediaItem entities
 * Provides CRUD operations for local media item caching
 */
@Dao
interface MediaItemDao {

    /**
     * Get all media items for a specific user ordered by newest first
     */
    @Query("SELECT * FROM media_items WHERE userId = :userId ORDER BY addedTimestamp DESC")
    fun getAllMediaItems(userId: String): Flow<List<MediaItemEntity>>

    /**
     * Get all media items without user filter (for internal operations like favorites lookup)
     * Note: Use with caution - prefer user-scoped methods when possible
     */
    @Query("SELECT * FROM media_items ORDER BY addedTimestamp DESC")
    fun getAllMediaItemsUnscoped(): Flow<List<MediaItemEntity>>

    /**
     * Get media items for a specific library and user
     */
    @Query("SELECT * FROM media_items WHERE userId = :userId AND libraryId = :libraryId ORDER BY addedTimestamp DESC")
    fun getMediaItemsByLibrary(userId: String, libraryId: String): Flow<List<MediaItemEntity>>

    /**
     * Get media items for multiple libraries and specific user
     */
    @Query("SELECT * FROM media_items WHERE userId = :userId AND libraryId IN (:libraryIds) ORDER BY addedTimestamp DESC")
    fun getMediaItemsByLibraries(userId: String, libraryIds: List<String>): Flow<List<MediaItemEntity>>

    /**
     * Get downloaded media items for a specific user
     */
    @Query("SELECT * FROM media_items WHERE userId = :userId AND isDownloaded = 1 ORDER BY addedTimestamp DESC")
    fun getDownloadedMediaItems(userId: String): Flow<List<MediaItemEntity>>

    /**
     * Get a specific media item by ID and user
     */
    @Query("SELECT * FROM media_items WHERE userId = :userId AND id = :itemId LIMIT 1")
    suspend fun getMediaItemById(userId: String, itemId: String): MediaItemEntity?

    /**
     * Get a specific media item by ID only (for internal operations like downloads)
     * Note: itemId is globally unique, so this is safe without userId filter
     */
    @Query("SELECT * FROM media_items WHERE id = :itemId LIMIT 1")
    suspend fun getMediaItemByIdOnly(itemId: String): MediaItemEntity?

    /**
     * Search media items by title for a specific user
     */
    @Query("SELECT * FROM media_items WHERE userId = :userId AND title LIKE '%' || :query || '%' ORDER BY addedTimestamp DESC")
    fun searchMediaItems(userId: String, query: String): Flow<List<MediaItemEntity>>

    /**
     * Insert a single media item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItemEntity)

    /**
     * Insert multiple media items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(items: List<MediaItemEntity>)

    /**
     * Update a media item
     */
    @Update
    suspend fun updateMediaItem(item: MediaItemEntity)

    /**
     * Update watched percentage for a media item (user-scoped for security)
     */
    @Query("UPDATE media_items SET watchedPercentage = :percentage WHERE userId = :userId AND id = :itemId")
    suspend fun updateWatchedPercentage(userId: String, itemId: String, percentage: Float)

    /**
     * Update watched percentage by itemId only (for internal operations like watch history)
     * Note: itemId is globally unique, so this is safe without userId filter
     */
    @Query("UPDATE media_items SET watchedPercentage = :percentage WHERE id = :itemId")
    suspend fun updateWatchedPercentageByItemId(itemId: String, percentage: Float)

    /**
     * Mark a media item as downloaded (user-scoped for security)
     */
    @Query("UPDATE media_items SET isDownloaded = :isDownloaded, localFilePath = :filePath WHERE userId = :userId AND id = :itemId")
    suspend fun updateDownloadStatus(userId: String, itemId: String, isDownloaded: Boolean, filePath: String?)

    /**
     * Mark a media item as downloaded by itemId only (for internal download operations)
     * Note: itemId is globally unique, so this is safe without userId filter
     */
    @Query("UPDATE media_items SET isDownloaded = :isDownloaded, localFilePath = :filePath WHERE id = :itemId")
    suspend fun updateDownloadStatusByItemId(itemId: String, isDownloaded: Boolean, filePath: String?)

    /**
     * Delete a specific media item (user-scoped for security)
     */
    @Query("DELETE FROM media_items WHERE userId = :userId AND id = :itemId")
    suspend fun deleteMediaItem(userId: String, itemId: String)

    /**
     * Delete all media items (for all users - use with caution)
     */
    @Query("DELETE FROM media_items")
    suspend fun deleteAllMediaItems()

    /**
     * Delete all media items for a specific user
     */
    @Query("DELETE FROM media_items WHERE userId = :userId")
    suspend fun deleteAllMediaItemsForUser(userId: String)

    /**
     * Delete media items for a specific library and user
     */
    @Query("DELETE FROM media_items WHERE userId = :userId AND libraryId = :libraryId")
    suspend fun deleteMediaItemsByLibrary(userId: String, libraryId: String)

    /**
     * Get count of media items for a specific user
     */
    @Query("SELECT COUNT(*) FROM media_items WHERE userId = :userId")
    suspend fun getMediaItemCount(userId: String): Int

    /**
     * Get count of downloaded media items for a specific user
     */
    @Query("SELECT COUNT(*) FROM media_items WHERE userId = :userId AND isDownloaded = 1")
    suspend fun getDownloadedMediaItemCount(userId: String): Int
}
