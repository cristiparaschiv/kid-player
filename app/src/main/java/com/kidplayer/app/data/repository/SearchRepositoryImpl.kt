package com.kidplayer.app.data.repository

import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.dao.SearchHistoryDao
import com.kidplayer.app.data.local.entity.SearchHistoryEntity
import com.kidplayer.app.data.local.mapper.EntityMapper
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.repository.JellyfinRepository
import com.kidplayer.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of SearchRepository
 * Manages search operations and search history
 * Search is user-scoped to ensure proper data isolation
 */
class SearchRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
    private val mediaItemDao: MediaItemDao,
    private val jellyfinRepository: JellyfinRepository
) : SearchRepository {

    override suspend fun searchMediaItems(query: String): Result<List<MediaItem>> {
        return try {
            val server = jellyfinRepository.getServerConfig()
            if (server != null) {
                val results = mediaItemDao.searchMediaItems(server.userId, query).first()
                val mediaItems = results.map { EntityMapper.entityToMediaItem(it) }
                Timber.d("Search for '$query' returned ${mediaItems.size} results for user: ${server.userId}")
                Result.success(mediaItems)
            } else {
                Timber.w("Search failed: No authenticated user")
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching media items")
            Result.failure(e)
        }
    }

    override fun getRecentSearchHistory(limit: Int): Flow<List<String>> {
        return searchHistoryDao.getRecentSearchHistory(limit).map { entities ->
            entities.map { it.query }.distinct()
        }
    }

    override suspend fun addSearchHistory(query: String): Result<Unit> {
        return try {
            if (query.isNotBlank()) {
                val entity = SearchHistoryEntity(
                    query = query.trim(),
                    searchedAt = System.currentTimeMillis()
                )
                searchHistoryDao.insertSearchHistory(entity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding search history")
            Result.failure(e)
        }
    }

    override suspend fun clearSearchHistory(): Result<Unit> {
        return try {
            searchHistoryDao.deleteAllSearchHistory()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error clearing search history")
            Result.failure(e)
        }
    }
}
