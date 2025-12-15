package com.kidplayer.app.data.repository

import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.dao.WatchHistoryDao
import com.kidplayer.app.data.local.entity.WatchHistoryEntity
import com.kidplayer.app.data.local.mapper.EntityMapper
import com.kidplayer.app.domain.model.ContinueWatchingItem
import com.kidplayer.app.domain.model.WatchHistory
import com.kidplayer.app.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of WatchHistoryRepository
 * Manages watch history tracking and continue watching functionality
 */
class WatchHistoryRepositoryImpl @Inject constructor(
    private val watchHistoryDao: WatchHistoryDao,
    private val mediaItemDao: MediaItemDao
) : WatchHistoryRepository {

    override fun getContinueWatching(limit: Int): Flow<List<ContinueWatchingItem>> {
        return watchHistoryDao.getContinueWatching(limit).combine(
            mediaItemDao.getAllMediaItemsUnscoped()
        ) { historyList, mediaItems ->
            historyList.mapNotNull { history ->
                val mediaItem = mediaItems.find { it.id == history.mediaItemId }
                if (mediaItem != null) {
                    ContinueWatchingItem(
                        mediaItem = EntityMapper.entityToMediaItem(mediaItem),
                        watchedPercentage = history.watchedPercentage,
                        positionMs = history.positionMs,
                        lastWatchedAt = history.watchedAt
                    )
                } else {
                    Timber.w("Media item not found for continue watching: ${history.mediaItemId}")
                    null
                }
            }
        }
    }

    override fun getRecentWatchHistory(limit: Int): Flow<List<WatchHistory>> {
        return watchHistoryDao.getRecentWatchHistory(limit).map { entities ->
            entities.map { entity ->
                WatchHistory(
                    id = entity.id,
                    mediaItemId = entity.mediaItemId,
                    watchedAt = entity.watchedAt,
                    watchedPercentage = entity.watchedPercentage,
                    duration = entity.duration,
                    positionMs = entity.positionMs
                )
            }
        }
    }

    override fun getWatchHistoryForItem(mediaItemId: String): Flow<List<WatchHistory>> {
        return watchHistoryDao.getWatchHistoryForItem(mediaItemId).map { entities ->
            entities.map { entity ->
                WatchHistory(
                    id = entity.id,
                    mediaItemId = entity.mediaItemId,
                    watchedAt = entity.watchedAt,
                    watchedPercentage = entity.watchedPercentage,
                    duration = entity.duration,
                    positionMs = entity.positionMs
                )
            }
        }
    }

    override suspend fun recordWatchHistory(
        mediaItemId: String,
        watchedPercentage: Float,
        duration: Long,
        positionMs: Long
    ): Result<Unit> {
        return try {
            val entity = WatchHistoryEntity(
                mediaItemId = mediaItemId,
                watchedPercentage = watchedPercentage,
                duration = duration,
                positionMs = positionMs,
                watchedAt = System.currentTimeMillis()
            )
            watchHistoryDao.insertWatchHistory(entity)

            // Also update the media item's watched percentage
            mediaItemDao.updateWatchedPercentageByItemId(mediaItemId, watchedPercentage)

            Timber.d("Recorded watch history for $mediaItemId: ${watchedPercentage}%")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error recording watch history")
            Result.failure(e)
        }
    }

    override suspend fun clearWatchHistoryForItem(mediaItemId: String): Result<Unit> {
        return try {
            watchHistoryDao.deleteWatchHistoryForItem(mediaItemId)
            mediaItemDao.updateWatchedPercentageByItemId(mediaItemId, 0f)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error clearing watch history")
            Result.failure(e)
        }
    }

    override suspend fun clearAllWatchHistory(): Result<Unit> {
        return try {
            watchHistoryDao.deleteAllWatchHistory()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error clearing all watch history")
            Result.failure(e)
        }
    }
}
