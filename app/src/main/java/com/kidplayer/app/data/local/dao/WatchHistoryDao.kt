package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for WatchHistory entities
 * Provides operations for tracking and querying watch history
 */
@Dao
interface WatchHistoryDao {

    /**
     * Get recent watch history ordered by most recent first
     */
    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT :limit")
    fun getRecentWatchHistory(limit: Int = 50): Flow<List<WatchHistoryEntity>>

    /**
     * Get watch history for a specific media item
     */
    @Query("SELECT * FROM watch_history WHERE mediaItemId = :mediaItemId ORDER BY watchedAt DESC")
    fun getWatchHistoryForItem(mediaItemId: String): Flow<List<WatchHistoryEntity>>

    /**
     * Get the most recent watch entry for a media item
     */
    @Query("SELECT * FROM watch_history WHERE mediaItemId = :mediaItemId ORDER BY watchedAt DESC LIMIT 1")
    suspend fun getLatestWatchHistoryForItem(mediaItemId: String): WatchHistoryEntity?

    /**
     * Get media items that are in progress (watched > 0 and < 90%)
     * Returns the most recent watch entry for each media item
     */
    @Query("""
        SELECT * FROM watch_history w1
        WHERE w1.watchedPercentage > 0 AND w1.watchedPercentage < 90
        AND w1.watchedAt = (
            SELECT MAX(w2.watchedAt)
            FROM watch_history w2
            WHERE w2.mediaItemId = w1.mediaItemId
        )
        ORDER BY w1.watchedAt DESC
        LIMIT :limit
    """)
    fun getContinueWatching(limit: Int = 20): Flow<List<WatchHistoryEntity>>

    /**
     * Insert a watch history entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: WatchHistoryEntity)

    /**
     * Delete watch history for a specific media item
     */
    @Query("DELETE FROM watch_history WHERE mediaItemId = :mediaItemId")
    suspend fun deleteWatchHistoryForItem(mediaItemId: String)

    /**
     * Delete all watch history
     */
    @Query("DELETE FROM watch_history")
    suspend fun deleteAllWatchHistory()

    /**
     * Delete watch history older than a specific timestamp
     */
    @Query("DELETE FROM watch_history WHERE watchedAt < :timestamp")
    suspend fun deleteOldWatchHistory(timestamp: Long)
}
