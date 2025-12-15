package com.kidplayer.app.data.local.mapper

import com.kidplayer.app.data.local.entity.LibraryEntity
import com.kidplayer.app.data.local.entity.MediaItemEntity
import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.MediaType

/**
 * Mapper object for converting between domain models and Room entities
 */
object EntityMapper {

    /**
     * Convert MediaItem domain model to MediaItemEntity
     */
    fun mediaItemToEntity(
        mediaItem: MediaItem,
        userId: String,
        libraryId: String? = null
    ): MediaItemEntity {
        return MediaItemEntity(
            id = mediaItem.id,
            title = mediaItem.title,
            overview = mediaItem.overview,
            thumbnailUrl = mediaItem.thumbnailUrl,
            backdropUrl = mediaItem.backdropUrl,
            duration = mediaItem.duration,
            jellyfinItemId = mediaItem.jellyfinItemId,
            type = mediaItem.type.name,
            seriesName = mediaItem.seriesName,
            seasonNumber = mediaItem.seasonNumber,
            episodeNumber = mediaItem.episodeNumber,
            year = mediaItem.year,
            isDownloaded = mediaItem.isDownloaded,
            downloadProgress = mediaItem.downloadProgress,
            watchedPercentage = mediaItem.watchedPercentage,
            localFilePath = mediaItem.localFilePath,
            libraryId = libraryId ?: mediaItem.libraryId,
            userId = userId,
            addedTimestamp = if (mediaItem.addedTimestamp > 0) mediaItem.addedTimestamp else System.currentTimeMillis(),
            lastModifiedTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Convert MediaItemEntity to MediaItem domain model
     */
    fun entityToMediaItem(entity: MediaItemEntity): MediaItem {
        val mediaType = try {
            MediaType.valueOf(entity.type)
        } catch (e: IllegalArgumentException) {
            MediaType.VIDEO
        }

        return MediaItem(
            id = entity.id,
            title = entity.title,
            overview = entity.overview,
            thumbnailUrl = entity.thumbnailUrl,
            backdropUrl = entity.backdropUrl,
            duration = entity.duration,
            jellyfinItemId = entity.jellyfinItemId,
            type = mediaType,
            seriesName = entity.seriesName,
            seasonNumber = entity.seasonNumber,
            episodeNumber = entity.episodeNumber,
            year = entity.year,
            isDownloaded = entity.isDownloaded,
            downloadProgress = entity.downloadProgress,
            watchedPercentage = entity.watchedPercentage,
            localFilePath = entity.localFilePath,
            libraryId = entity.libraryId,
            addedTimestamp = entity.addedTimestamp
        )
    }

    /**
     * Convert list of MediaItem to list of MediaItemEntity
     */
    fun mediaItemListToEntityList(
        mediaItems: List<MediaItem>,
        userId: String,
        libraryId: String? = null
    ): List<MediaItemEntity> {
        return mediaItems.map { mediaItemToEntity(it, userId, libraryId) }
    }

    /**
     * Convert list of MediaItemEntity to list of MediaItem
     */
    fun entityListToMediaItemList(entities: List<MediaItemEntity>): List<MediaItem> {
        return entities.map { entityToMediaItem(it) }
    }

    /**
     * Convert Library domain model to LibraryEntity
     */
    fun libraryToEntity(library: Library, userId: String): LibraryEntity {
        return LibraryEntity(
            id = library.id,
            name = library.name,
            collectionType = library.collectionType,
            isEnabled = library.isEnabled,
            userId = userId,
            addedTimestamp = System.currentTimeMillis(),
            lastModifiedTimestamp = System.currentTimeMillis()
        )
    }

    /**
     * Convert LibraryEntity to Library domain model
     */
    fun entityToLibrary(entity: LibraryEntity): Library {
        return Library(
            id = entity.id,
            name = entity.name,
            collectionType = entity.collectionType,
            isEnabled = entity.isEnabled
        )
    }

    /**
     * Convert list of Library to list of LibraryEntity
     */
    fun libraryListToEntityList(libraries: List<Library>, userId: String): List<LibraryEntity> {
        return libraries.map { libraryToEntity(it, userId) }
    }

    /**
     * Convert list of LibraryEntity to list of Library
     */
    fun entityListToLibraryList(entities: List<LibraryEntity>): List<Library> {
        return entities.map { entityToLibrary(it) }
    }
}
