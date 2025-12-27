package com.kidplayer.app.domain.reward

import com.kidplayer.app.data.local.dao.PlayerRewardDao
import com.kidplayer.app.data.local.entity.PlayerRewardEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for player star rewards
 * Handles earning, tracking, and persisting stars across game sessions
 */
@Singleton
class RewardManager @Inject constructor(
    private val playerRewardDao: PlayerRewardDao
) {
    /**
     * Observe total stars as a Flow for reactive UI updates
     */
    val totalStarsFlow: Flow<Int> = playerRewardDao.observeRewards()
        .map { it?.totalStars ?: 0 }

    /**
     * Initialize rewards if needed (create initial record)
     */
    suspend fun initialize() {
        playerRewardDao.initializeIfNeeded()
        Timber.d("RewardManager initialized")
    }

    /**
     * Get current total stars
     */
    suspend fun getTotalStars(): Int {
        return playerRewardDao.getTotalStars() ?: 0
    }

    /**
     * Add stars to the player's total
     * @param amount Number of stars to add
     */
    suspend fun addStars(amount: Int) {
        playerRewardDao.initializeIfNeeded()
        playerRewardDao.addStars(amount)
        Timber.d("Added $amount stars. New total: ${getTotalStars()}")
    }

    /**
     * Award stars based on game performance
     * @param score The player's score (0-100 percentage)
     * @param isPerfect Whether the player achieved a perfect score
     * @return Number of stars awarded
     */
    suspend fun awardStarsForGame(score: Int, isPerfect: Boolean = false): Int {
        val baseStars = when {
            score >= 90 -> 3
            score >= 70 -> 2
            score >= 50 -> 1
            else -> 0
        }

        val bonusStars = if (isPerfect) 2 else 0
        val totalAwarded = baseStars + bonusStars

        if (totalAwarded > 0) {
            addStars(totalAwarded)
        }

        return totalAwarded
    }

    /**
     * Award stars for completing a game
     * Simpler version for games without percentage scores
     * @param difficulty Game difficulty (affects stars: easy=1, medium=2, hard=3)
     * @param completed Whether the game was completed
     * @return Number of stars awarded
     */
    suspend fun awardStarsForCompletion(difficulty: GameDifficulty, completed: Boolean): Int {
        if (!completed) return 0

        val stars = when (difficulty) {
            GameDifficulty.EASY -> 1
            GameDifficulty.MEDIUM -> 2
            GameDifficulty.HARD -> 3
        }

        addStars(stars)
        return stars
    }
}

/**
 * Game difficulty levels for star rewards
 */
enum class GameDifficulty {
    EASY, MEDIUM, HARD
}
