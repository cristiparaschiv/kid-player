package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SearchHistory entities
 * Provides operations for managing search history
 */
@Dao
interface SearchHistoryDao {

    /**
     * Get recent search history ordered by most recent first
     */
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    fun getRecentSearchHistory(limit: Int = 10): Flow<List<SearchHistoryEntity>>

    /**
     * Search in search history
     */
    @Query("SELECT * FROM search_history WHERE query LIKE '%' || :query || '%' ORDER BY searchedAt DESC LIMIT 5")
    fun searchHistory(query: String): Flow<List<SearchHistoryEntity>>

    /**
     * Insert a search history entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity)

    /**
     * Delete a specific search history entry
     */
    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistory(id: Long)

    /**
     * Delete all search history
     */
    @Query("DELETE FROM search_history")
    suspend fun deleteAllSearchHistory()

    /**
     * Delete search history older than a specific timestamp
     */
    @Query("DELETE FROM search_history WHERE searchedAt < :timestamp")
    suspend fun deleteOldSearchHistory(timestamp: Long)
}
