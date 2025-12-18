package com.kidplayer.app.domain.model

/**
 * Represents a media item (video) from Jellyfin
 * This is the domain model for videos shown in the browse screen
 */
data class MediaItem(
    val id: String,
    val title: String,
    val overview: String? = null,
    val thumbnailUrl: String? = null,
    val backdropUrl: String? = null,
    val duration: Long = 0L, // Duration in ticks (Jellyfin uses ticks, 1 tick = 100 nanoseconds)
    val jellyfinItemId: String,
    val type: MediaType = MediaType.VIDEO,
    val seriesName: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val year: Int? = null,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val watchedPercentage: Float = 0f,
    val localFilePath: String? = null,
    val libraryId: String? = null,
    val addedTimestamp: Long = 0L,
    val playbackPositionTicks: Long = 0L // Resume position from Jellyfin (in ticks)
) {
    /**
     * Returns the duration in seconds
     */
    fun getDurationInSeconds(): Long {
        return duration / 10_000_000 // Convert ticks to seconds
    }

    /**
     * Returns the duration in minutes
     */
    fun getDurationInMinutes(): Long {
        return getDurationInSeconds() / 60
    }

    /**
     * Returns a formatted duration string (e.g., "1h 30m")
     */
    fun getFormattedDuration(): String {
        val totalMinutes = getDurationInMinutes()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    /**
     * Returns true if this media item has been watched
     */
    fun isWatched(): Boolean {
        return watchedPercentage >= 0.9f
    }

    /**
     * Returns the resume position in milliseconds
     * Converts from Jellyfin ticks (1 tick = 100 nanoseconds) to milliseconds
     */
    fun getResumePositionMs(): Long {
        return playbackPositionTicks / 10_000 // Convert ticks to milliseconds
    }

    /**
     * Returns true if there's a resume position available
     */
    fun hasResumePosition(): Boolean {
        return playbackPositionTicks > 0 && watchedPercentage > 0.01f && watchedPercentage < 0.95f
    }

    /**
     * Returns true if this is an episode
     */
    fun isEpisode(): Boolean {
        return type == MediaType.EPISODE
    }

    /**
     * Returns formatted episode info (e.g., "S1 E5")
     */
    fun getEpisodeInfo(): String? {
        return if (seasonNumber != null && episodeNumber != null) {
            "S$seasonNumber E$episodeNumber"
        } else {
            null
        }
    }

    /**
     * Returns the display title (includes series info for episodes)
     */
    fun getDisplayTitle(): String {
        return when (type) {
            MediaType.EPISODE -> {
                val episodeInfo = if (seasonNumber != null && episodeNumber != null) {
                    "S${seasonNumber}E${episodeNumber}"
                } else ""

                if (seriesName != null && episodeInfo.isNotEmpty()) {
                    "$seriesName - $episodeInfo: $title"
                } else if (seriesName != null) {
                    "$seriesName: $title"
                } else {
                    title
                }
            }
            else -> title
        }
    }
}

/**
 * Types of media items supported
 */
enum class MediaType {
    VIDEO,
    MOVIE,
    EPISODE
}
