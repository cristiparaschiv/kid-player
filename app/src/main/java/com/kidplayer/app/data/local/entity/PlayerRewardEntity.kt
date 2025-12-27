package com.kidplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing player reward stars
 * Kids collect stars by playing games as achievements
 */
@Entity(tableName = "player_rewards")
data class PlayerRewardEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for simplicity

    val totalStars: Int = 0,

    val lastUpdated: Long = System.currentTimeMillis()
)
