package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for tracking watch history
 * Records each time a video is watched with progress tracking
 */
@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val mediaItemId: String,

    val watchedAt: Long = System.currentTimeMillis(),

    val watchedPercentage: Float = 0f,

    val duration: Long = 0L,

    val positionMs: Long = 0L
)
