package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching media items locally
 * This allows offline browsing and tracking download status
 */
@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey
    val id: String,

    val title: String,

    val overview: String? = null,

    val thumbnailUrl: String? = null,

    val backdropUrl: String? = null,

    val duration: Long = 0L, // Duration in ticks

    val jellyfinItemId: String,

    val type: String, // "VIDEO", "MOVIE", "EPISODE"

    val seriesName: String? = null,

    val seasonNumber: Int? = null,

    val episodeNumber: Int? = null,

    val year: Int? = null,

    val isDownloaded: Boolean = false,

    val downloadProgress: Float = 0f, // Download progress (0.0 to 1.0)

    val watchedPercentage: Float = 0f,

    val localFilePath: String? = null,

    val libraryId: String? = null,

    val userId: String, // Jellyfin user ID - ensures cache isolation between users

    val addedTimestamp: Long = System.currentTimeMillis(),

    val lastModifiedTimestamp: Long = System.currentTimeMillis(),

    val playbackPositionTicks: Long = 0L // Resume position from Jellyfin (in ticks)
)
