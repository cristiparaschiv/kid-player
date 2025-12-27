package com.kidplayer.app.data.local.dao

import androidx.room.*
import com.kidplayer.app.data.local.entity.PlayerRewardEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for PlayerReward entities
 * Provides operations for managing player stars
 */
@Dao
interface PlayerRewardDao {

    /**
     * Get the player rewards as a Flow for reactive updates
     */
    @Query("SELECT * FROM player_rewards WHERE id = 1 LIMIT 1")
    fun observeRewards(): Flow<PlayerRewardEntity?>

    /**
     * Get the current player rewards
     */
    @Query("SELECT * FROM player_rewards WHERE id = 1 LIMIT 1")
    suspend fun getRewards(): PlayerRewardEntity?

    /**
     * Get total stars count
     */
    @Query("SELECT totalStars FROM player_rewards WHERE id = 1 LIMIT 1")
    suspend fun getTotalStars(): Int?

    /**
     * Insert or update player rewards
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(reward: PlayerRewardEntity)

    /**
     * Add stars to the total
     */
    @Query("UPDATE player_rewards SET totalStars = totalStars + :amount, lastUpdated = :timestamp WHERE id = 1")
    suspend fun addStars(amount: Int, timestamp: Long = System.currentTimeMillis())

    /**
     * Initialize rewards if not exists
     */
    @Transaction
    suspend fun initializeIfNeeded() {
        if (getRewards() == null) {
            insertOrUpdate(PlayerRewardEntity())
        }
    }
}
