package com.kidplayer.app.domain.model

/**
 * Domain model for continue watching items
 * Combines media item with watch progress
 */
data class ContinueWatchingItem(
    val mediaItem: MediaItem,
    val watchedPercentage: Float,
    val positionMs: Long,
    val lastWatchedAt: Long
)
