package com.kidplayer.app.data.remote.mapper

import com.kidplayer.app.data.remote.dto.AuthResponse
import com.kidplayer.app.data.remote.dto.LibraryDto
import com.kidplayer.app.data.remote.dto.MediaItemDto
import com.kidplayer.app.domain.model.JellyfinServer
import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.MediaType

/**
 * Mapper object to convert Jellyfin API DTOs to domain models
 */
object JellyfinMapper {

    /**
     * Maps AuthResponse to JellyfinServer domain model
     */
    fun mapAuthResponseToServer(
        response: AuthResponse,
        serverUrl: String,
        username: String
    ): JellyfinServer {
        return JellyfinServer(
            url = serverUrl,
            username = username,
            userId = response.user.id,
            authToken = response.accessToken
        )
    }

    /**
     * Maps LibraryDto to Library domain model
     */
    fun mapLibraryDtoToLibrary(dto: LibraryDto): Library {
        return Library(
            id = dto.id,
            name = dto.name,
            collectionType = dto.collectionType,
            isEnabled = true
        )
    }

    /**
     * Maps a list of LibraryDto to list of Library domain models
     */
    fun mapLibraryDtoListToLibraryList(dtoList: List<LibraryDto>): List<Library> {
        return dtoList.map { mapLibraryDtoToLibrary(it) }
    }

    /**
     * Maps MediaItemDto to MediaItem domain model
     */
    fun mapMediaItemDtoToMediaItem(
        dto: MediaItemDto,
        serverUrl: String,
        authToken: String
    ): MediaItem {
        val baseUrl = serverUrl.trimEnd('/')

        // Determine media type
        val mediaType = when (dto.type.lowercase()) {
            "movie" -> MediaType.MOVIE
            "episode" -> MediaType.EPISODE
            else -> MediaType.VIDEO
        }

        // Build thumbnail URL if image tag exists
        val thumbnailUrl = dto.imageTags?.get("Primary")?.let {
            "$baseUrl/Items/${dto.id}/Images/Primary?maxWidth=500&tag=$it&api_key=$authToken"
        }

        // Build backdrop URL if backdrop image tag exists
        val backdropUrl = dto.backdropImageTags?.firstOrNull()?.let { tag ->
            "$baseUrl/Items/${dto.id}/Images/Backdrop?maxWidth=1280&tag=$tag&api_key=$authToken"
        }

        // Calculate watched percentage
        val watchedPercentage = if (dto.runTimeTicks != null &&
            dto.userData?.playbackPositionTicks != null &&
            dto.runTimeTicks > 0) {
            (dto.userData.playbackPositionTicks.toFloat() / dto.runTimeTicks.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        return MediaItem(
            id = dto.id,
            title = dto.name,
            overview = dto.overview,
            thumbnailUrl = thumbnailUrl,
            backdropUrl = backdropUrl,
            duration = dto.runTimeTicks ?: 0L,
            jellyfinItemId = dto.id,
            type = mediaType,
            seriesName = dto.seriesName,
            seasonNumber = dto.parentIndexNumber,
            episodeNumber = dto.indexNumber,
            year = dto.productionYear,
            isDownloaded = false, // Will be updated from local database
            watchedPercentage = watchedPercentage,
            localFilePath = null,
            playbackPositionTicks = dto.userData?.playbackPositionTicks ?: 0L
        )
    }

    /**
     * Maps a list of MediaItemDto to list of MediaItem domain models
     */
    fun mapMediaItemDtoListToMediaItemList(
        dtoList: List<MediaItemDto>,
        serverUrl: String,
        authToken: String
    ): List<MediaItem> {
        return dtoList
            .filter { !it.isFolder } // Filter out folders
            .map { mapMediaItemDtoToMediaItem(it, serverUrl, authToken) }
    }

    /**
     * Builds a streaming URL for a media item
     * Uses Jellyfin's stream endpoint which includes both video and audio
     *
     * Note: Using Static=true for direct streaming without transcoding.
     * The stream endpoint automatically includes all tracks (video + audio).
     */
    fun buildStreamingUrl(
        serverUrl: String,
        itemId: String,
        authToken: String,
        mediaSourceId: String? = null
    ): String {
        val baseUrl = serverUrl.trimEnd('/')
        val sourceParam = mediaSourceId?.let { "&MediaSourceId=$it" } ?: ""

        // Use simple streaming URL - Jellyfin's stream endpoint includes audio by default
        // Static=true means direct stream without transcoding (better quality, lower server load)
        return "$baseUrl/Videos/$itemId/stream?Static=true&api_key=$authToken$sourceParam"
    }

    /**
     * Builds a direct play URL for a media item
     */
    fun buildDirectPlayUrl(
        serverUrl: String,
        itemId: String,
        authToken: String
    ): String {
        val baseUrl = serverUrl.trimEnd('/')
        return "$baseUrl/Items/$itemId/Download?api_key=$authToken"
    }

    /**
     * Builds a thumbnail URL for a media item
     */
    fun buildThumbnailUrl(
        serverUrl: String,
        itemId: String,
        authToken: String,
        maxWidth: Int = 500
    ): String {
        val baseUrl = serverUrl.trimEnd('/')
        return "$baseUrl/Items/$itemId/Images/Primary?maxWidth=$maxWidth&api_key=$authToken"
    }
}
