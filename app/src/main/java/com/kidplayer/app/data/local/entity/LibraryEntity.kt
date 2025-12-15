package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching Jellyfin libraries locally
 * This enables offline library browsing
 */
@Entity(tableName = "libraries")
data class LibraryEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val collectionType: String? = null,

    val isEnabled: Boolean = true,

    val userId: String, // Jellyfin user ID - ensures cache isolation between users

    val addedTimestamp: Long = System.currentTimeMillis(),

    val lastModifiedTimestamp: Long = System.currentTimeMillis()
)
