package com.kidplayer.app.domain.usecase

import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.mapper.EntityMapper
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.repository.JellyfinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for getting downloaded media items
 * Downloads are user-scoped to ensure proper data isolation
 */
class GetDownloadedItemsUseCase @Inject constructor(
    private val mediaItemDao: MediaItemDao,
    private val jellyfinRepository: JellyfinRepository
) {
    /**
     * Get all downloaded media items for the current user
     * @return Flow of downloaded media items
     */
    operator fun invoke(): Flow<List<MediaItem>> = flow {
        val server = jellyfinRepository.getServerConfig()
        if (server != null) {
            mediaItemDao.getDownloadedMediaItems(server.userId)
                .collect { entities ->
                    emit(entities.map { EntityMapper.entityToMediaItem(it) })
                }
        } else {
            emit(emptyList())
        }
    }

    /**
     * Get count of downloaded media items for the current user
     */
    suspend fun getCount(): Int {
        val server = jellyfinRepository.getServerConfig()
        return if (server != null) {
            mediaItemDao.getDownloadedMediaItemCount(server.userId)
        } else {
            0
        }
    }
}
