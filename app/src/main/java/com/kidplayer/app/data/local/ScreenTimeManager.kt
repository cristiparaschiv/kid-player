package com.kidplayer.app.data.local

import com.kidplayer.app.domain.model.AccessRestriction
import com.kidplayer.app.domain.model.AccessSchedule
import com.kidplayer.app.domain.model.DayOfWeek
import com.kidplayer.app.domain.model.ScreenTimeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for tracking and enforcing screen time limits
 * Handles real-time screen time tracking and daily reset logic
 */
@Singleton
class ScreenTimeManager @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    private val _screenTimeState = MutableStateFlow<ScreenTimeConfig?>(null)
    val screenTimeState: Flow<ScreenTimeConfig?> = _screenTimeState.asStateFlow()

    private var trackingStartTime: Long? = null

    /**
     * Initialize screen time tracking
     * Should be called when app starts
     */
    suspend fun initialize() {
        val config = loadScreenTimeConfig()
        checkAndResetDailyCounter(config)
        _screenTimeState.value = config
    }

    /**
     * Start tracking screen time
     * Call this when video playback starts
     */
    fun startTracking() {
        trackingStartTime = System.currentTimeMillis()
        Timber.d("Screen time tracking started")
    }

    /**
     * Stop tracking and save accumulated time
     * Call this when video playback stops
     */
    suspend fun stopTracking() {
        val startTime = trackingStartTime ?: return
        trackingStartTime = null

        val elapsedMillis = System.currentTimeMillis() - startTime
        val elapsedMinutes = (elapsedMillis / 60_000).toInt()

        if (elapsedMinutes > 0) {
            addScreenTime(elapsedMinutes)
            Timber.d("Screen time tracking stopped. Added $elapsedMinutes minutes")
        }
    }

    /**
     * Get current screen time configuration
     * Includes active session time if tracking is in progress
     */
    suspend fun getScreenTimeConfig(): ScreenTimeConfig {
        var config = _screenTimeState.value
        if (config == null) {
            config = loadScreenTimeConfig()
            checkAndResetDailyCounter(config)
            _screenTimeState.value = config
        }

        // Include active session time if tracking is in progress
        val activeSessionMinutes = getCurrentActiveSessionMinutes()
        return if (activeSessionMinutes > 0) {
            config.copy(usedTodayMinutes = config.usedTodayMinutes + activeSessionMinutes)
        } else {
            config
        }
    }

    /**
     * Get the elapsed minutes from the current active tracking session
     * Returns 0 if not currently tracking
     */
    private fun getCurrentActiveSessionMinutes(): Int {
        val startTime = trackingStartTime ?: return 0
        val elapsedMillis = System.currentTimeMillis() - startTime
        return (elapsedMillis / 60_000).toInt()
    }

    /**
     * Update screen time configuration
     */
    suspend fun updateScreenTimeConfig(config: ScreenTimeConfig) {
        saveScreenTimeConfig(config)
        _screenTimeState.value = config
    }

    /**
     * Add screen time usage
     */
    suspend fun addScreenTime(minutes: Int) {
        val config = getScreenTimeConfig()
        val newUsedTime = config.usedTodayMinutes + minutes
        val updatedConfig = config.copy(usedTodayMinutes = newUsedTime)

        saveScreenTimeConfig(updatedConfig)
        _screenTimeState.value = updatedConfig

        Timber.d("Screen time updated: $newUsedTime/${config.dailyLimitMinutes} minutes")
    }

    /**
     * Check if daily counter needs to be reset
     */
    private suspend fun checkAndResetDailyCounter(config: ScreenTimeConfig): ScreenTimeConfig {
        val today = LocalDate.now().toString()

        return if (config.lastResetDate != today) {
            // New day - reset counter
            val resetConfig = config.copy(
                usedTodayMinutes = 0,
                lastResetDate = today
            )
            saveScreenTimeConfig(resetConfig)
            Timber.d("Daily screen time counter reset for $today")
            resetConfig
        } else {
            config
        }
    }

    /**
     * Reset daily screen time counter manually
     */
    suspend fun resetDailyCounter() {
        val config = getScreenTimeConfig()
        val today = LocalDate.now().toString()
        val resetConfig = config.copy(
            usedTodayMinutes = 0,
            lastResetDate = today
        )
        saveScreenTimeConfig(resetConfig)
        _screenTimeState.value = resetConfig
        Timber.d("Daily screen time counter manually reset")
    }

    /**
     * Check current access restrictions
     */
    suspend fun checkAccessRestriction(accessSchedule: AccessSchedule?): AccessRestriction {
        val config = getScreenTimeConfig()

        // Check screen time limit
        if (config.isEnabled && config.isLimitReached) {
            return AccessRestriction.TimeLimitReached(config.dailyLimitMinutes)
        }

        // Check access schedule
        if (accessSchedule != null && accessSchedule.isEnabled) {
            val currentTime = LocalTime.now()
            val currentDay = getCurrentDayOfWeek()

            if (!accessSchedule.isCurrentTimeAllowed(currentTime, currentDay)) {
                return AccessRestriction.OutsideSchedule(
                    accessSchedule.startTime,
                    accessSchedule.endTime
                )
            }
        }

        return AccessRestriction.Allowed
    }

    /**
     * Get remaining minutes for today
     */
    suspend fun getRemainingMinutes(): Int {
        val config = getScreenTimeConfig()
        return if (config.isEnabled) {
            config.remainingMinutes
        } else {
            Int.MAX_VALUE
        }
    }

    /**
     * Load screen time config from secure preferences
     */
    private suspend fun loadScreenTimeConfig(): ScreenTimeConfig {
        return ScreenTimeConfig(
            isEnabled = securePreferences.getScreenTimeLimitEnabled() ?: false,
            dailyLimitMinutes = securePreferences.getScreenTimeDailyLimit() ?: 60,
            usedTodayMinutes = securePreferences.getScreenTimeUsedToday() ?: 0,
            lastResetDate = securePreferences.getScreenTimeLastReset() ?: LocalDate.now().toString()
        )
    }

    /**
     * Save screen time config to secure preferences
     */
    private suspend fun saveScreenTimeConfig(config: ScreenTimeConfig) {
        securePreferences.saveScreenTimeLimitEnabled(config.isEnabled)
        securePreferences.saveScreenTimeDailyLimit(config.dailyLimitMinutes)
        securePreferences.saveScreenTimeUsedToday(config.usedTodayMinutes)
        securePreferences.saveScreenTimeLastReset(config.lastResetDate)
    }

    /**
     * Extend screen time by adding extra minutes to the daily limit temporarily
     * This is used when a parent grants additional time after limit is reached
     *
     * @param additionalMinutes Number of minutes to add (e.g., 15, 30, 60)
     */
    suspend fun extendScreenTime(additionalMinutes: Int) {
        val config = getScreenTimeConfig()
        val newLimit = config.dailyLimitMinutes + additionalMinutes
        val updatedConfig = config.copy(dailyLimitMinutes = newLimit)

        saveScreenTimeConfig(updatedConfig)
        _screenTimeState.value = updatedConfig

        Timber.d("Screen time extended by $additionalMinutes minutes. New limit: $newLimit minutes")
    }

    /**
     * Reduce used time by a specific amount
     * Alternative approach to extending time - reduces the counter instead of increasing limit
     *
     * @param minutesToSubtract Number of minutes to subtract from used time
     */
    suspend fun reduceUsedTime(minutesToSubtract: Int) {
        val config = getScreenTimeConfig()
        val newUsedTime = (config.usedTodayMinutes - minutesToSubtract).coerceAtLeast(0)
        val updatedConfig = config.copy(usedTodayMinutes = newUsedTime)

        saveScreenTimeConfig(updatedConfig)
        _screenTimeState.value = updatedConfig

        Timber.d("Screen time reduced by $minutesToSubtract minutes. New used time: $newUsedTime minutes")
    }

    /**
     * Get current day of week
     */
    private fun getCurrentDayOfWeek(): DayOfWeek {
        return when (java.time.LocalDate.now().dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
            java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
            java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
            java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
            java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
            java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        }
    }
}
