package com.kidplayer.app.domain.model

/**
 * Domain model for watch history
 * Represents a record of a video being watched
 */
data class WatchHistory(
    val id: Long = 0,
    val mediaItemId: String,
    val watchedAt: Long,
    val watchedPercentage: Float,
    val duration: Long,
    val positionMs: Long
)
