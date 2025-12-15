package com.kidplayer.app.domain.repository

import com.kidplayer.app.domain.model.ContinueWatchingItem
import com.kidplayer.app.domain.model.WatchHistory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for watch history operations
 */
interface WatchHistoryRepository {

    /**
     * Get continue watching items (in-progress videos)
     */
    fun getContinueWatching(limit: Int = 20): Flow<List<ContinueWatchingItem>>

    /**
     * Get recent watch history
     */
    fun getRecentWatchHistory(limit: Int = 50): Flow<List<WatchHistory>>

    /**
     * Get watch history for a specific media item
     */
    fun getWatchHistoryForItem(mediaItemId: String): Flow<List<WatchHistory>>

    /**
     * Record a watch history entry
     */
    suspend fun recordWatchHistory(
        mediaItemId: String,
        watchedPercentage: Float,
        duration: Long,
        positionMs: Long
    ): Result<Unit>

    /**
     * Clear watch history for a specific item
     */
    suspend fun clearWatchHistoryForItem(mediaItemId: String): Result<Unit>

    /**
     * Clear all watch history
     */
    suspend fun clearAllWatchHistory(): Result<Unit>
}
