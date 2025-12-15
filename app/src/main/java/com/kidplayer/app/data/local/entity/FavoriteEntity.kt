package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing user's favorite videos
 * Allows kids to easily access their favorite content
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val mediaItemId: String,

    val addedAt: Long = System.currentTimeMillis(),

    val autoDownload: Boolean = false
)
