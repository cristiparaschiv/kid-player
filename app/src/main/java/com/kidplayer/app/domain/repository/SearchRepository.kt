package com.kidplayer.app.domain.repository

import com.kidplayer.app.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for search operations
 */
interface SearchRepository {

    /**
     * Search for media items by query
     */
    suspend fun searchMediaItems(query: String): Result<List<MediaItem>>

    /**
     * Get recent search history
     */
    fun getRecentSearchHistory(limit: Int = 10): Flow<List<String>>

    /**
     * Add a search query to history
     */
    suspend fun addSearchHistory(query: String): Result<Unit>

    /**
     * Clear all search history
     */
    suspend fun clearSearchHistory(): Result<Unit>
}
