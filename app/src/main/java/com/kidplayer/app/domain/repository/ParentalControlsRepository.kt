package com.kidplayer.app.domain.repository

import com.kidplayer.app.domain.model.AccessRestriction
import com.kidplayer.app.domain.model.AccessSchedule
import com.kidplayer.app.domain.model.ParentalControls
import com.kidplayer.app.domain.model.PinVerificationResult
import com.kidplayer.app.domain.model.ScreenTimeConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for parental controls and screen time management
 */
interface ParentalControlsRepository {
    /**
     * Get parental controls configuration
     */
    fun getParentalControls(): Flow<ParentalControls>

    /**
     * Check if PIN is set
     */
    suspend fun isPinSet(): Boolean

    /**
     * Set or update parent PIN
     * @param pin 4-digit PIN code
     */
    suspend fun setPin(pin: String): Result<Unit>

    /**
     * Verify parent PIN
     * @param pin 4-digit PIN to verify
     */
    suspend fun verifyPin(pin: String): PinVerificationResult

    /**
     * Get screen time configuration
     */
    suspend fun getScreenTimeConfig(): ScreenTimeConfig

    /**
     * Update screen time configuration
     */
    suspend fun updateScreenTimeConfig(config: ScreenTimeConfig): Result<Unit>

    /**
     * Add screen time usage (in minutes)
     */
    suspend fun addScreenTimeUsage(minutes: Int): Result<Unit>

    /**
     * Reset daily screen time counter (called automatically at midnight)
     */
    suspend fun resetDailyScreenTime(): Result<Unit>

    /**
     * Get access schedule configuration
     */
    suspend fun getAccessSchedule(): AccessSchedule?

    /**
     * Update access schedule configuration
     */
    suspend fun updateAccessSchedule(schedule: AccessSchedule?): Result<Unit>

    /**
     * Check if app access is currently allowed
     * Returns AccessRestriction.Allowed if access is permitted,
     * or a specific restriction reason if blocked
     */
    suspend fun checkAccessRestriction(): AccessRestriction

    /**
     * Clear all parental controls (requires PIN verification)
     */
    suspend fun clearAllControls(): Result<Unit>
}
