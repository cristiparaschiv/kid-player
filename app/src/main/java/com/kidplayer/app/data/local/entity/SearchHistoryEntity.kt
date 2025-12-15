package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing search history
 * Helps kids quickly repeat previous searches
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val query: String,

    val searchedAt: Long = System.currentTimeMillis()
)
