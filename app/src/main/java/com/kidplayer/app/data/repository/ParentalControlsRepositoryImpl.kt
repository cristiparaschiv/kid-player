package com.kidplayer.app.data.repository

import com.kidplayer.app.data.local.PinManager
import com.kidplayer.app.data.local.ScreenTimeManager
import com.kidplayer.app.data.local.SecurePreferences
import com.kidplayer.app.domain.model.AccessRestriction
import com.kidplayer.app.domain.model.AccessSchedule
import com.kidplayer.app.domain.model.ContentFiltering
import com.kidplayer.app.domain.model.DayOfWeek
import com.kidplayer.app.domain.model.ParentalControls
import com.kidplayer.app.domain.model.PinVerificationResult
import com.kidplayer.app.domain.model.ScreenTimeConfig
import com.kidplayer.app.domain.repository.ParentalControlsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ParentalControlsRepository
 * Manages parental controls, PIN verification, and screen time limits
 */
@Singleton
class ParentalControlsRepositoryImpl @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val pinManager: PinManager,
    private val screenTimeManager: ScreenTimeManager
) : ParentalControlsRepository {

    override fun getParentalControls(): Flow<ParentalControls> {
        val defaultScreenTimeConfig = ScreenTimeConfig(
            isEnabled = false,
            dailyLimitMinutes = 60,
            usedTodayMinutes = 0,
            lastResetDate = java.time.LocalDate.now().toString()
        )

        return screenTimeManager.screenTimeState
            .onStart { emit(null) } // Emit immediately so we don't wait for initialization
            .map { screenTimeConfig ->
                val isPinSet = isPinSet()
                val accessSchedule = getAccessSchedule()
                val contentFiltering = ContentFiltering(isEnabled = false) // Future enhancement

                ParentalControls(
                    isPinSet = isPinSet,
                    screenTimeConfig = screenTimeConfig ?: defaultScreenTimeConfig,
                    accessSchedule = accessSchedule,
                    contentFiltering = contentFiltering
                )
            }
    }

    override suspend fun isPinSet(): Boolean {
        val pin = securePreferences.getParentPin()
        return !pin.isNullOrBlank()
    }

    override suspend fun setPin(pin: String): Result<Unit> {
        return try {
            if (!pinManager.isValidPinFormat(pin)) {
                return Result.failure(IllegalArgumentException("PIN must be exactly 4 digits"))
            }

            val hashedPin = pinManager.hashPin(pin)
            securePreferences.saveParentPin(hashedPin)
            Timber.d("Parent PIN set successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to set parent PIN")
            Result.failure(e)
        }
    }

    override suspend fun verifyPin(pin: String): PinVerificationResult {
        return try {
            val storedHash = securePreferences.getParentPin()

            if (storedHash.isNullOrBlank()) {
                return PinVerificationResult.NotSet
            }

            val isValid = pinManager.verifyPin(pin, storedHash)
            if (isValid) {
                Timber.d("PIN verification successful")
                PinVerificationResult.Success
            } else {
                Timber.d("PIN verification failed")
                PinVerificationResult.Failure
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during PIN verification")
            PinVerificationResult.Failure
        }
    }

    override suspend fun getScreenTimeConfig(): ScreenTimeConfig {
        return screenTimeManager.getScreenTimeConfig()
    }

    override suspend fun updateScreenTimeConfig(config: ScreenTimeConfig): Result<Unit> {
        return try {
            screenTimeManager.updateScreenTimeConfig(config)
            Timber.d("Screen time config updated: $config")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update screen time config")
            Result.failure(e)
        }
    }

    override suspend fun addScreenTimeUsage(minutes: Int): Result<Unit> {
        return try {
            screenTimeManager.addScreenTime(minutes)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to add screen time usage")
            Result.failure(e)
        }
    }

    override suspend fun resetDailyScreenTime(): Result<Unit> {
        return try {
            screenTimeManager.resetDailyCounter()
            Timber.d("Daily screen time reset")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to reset daily screen time")
            Result.failure(e)
        }
    }

    override suspend fun getAccessSchedule(): AccessSchedule? {
        val isEnabled = securePreferences.getAccessScheduleEnabled() ?: return null
        val startTimeStr = securePreferences.getAccessScheduleStartTime()
        val endTimeStr = securePreferences.getAccessScheduleEndTime()

        return if (startTimeStr != null && endTimeStr != null) {
            try {
                AccessSchedule(
                    isEnabled = isEnabled,
                    startTime = LocalTime.parse(startTimeStr),
                    endTime = LocalTime.parse(endTimeStr),
                    allowedDays = DayOfWeek.entries.toSet() // All days by default
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse access schedule times")
                null
            }
        } else {
            null
        }
    }

    override suspend fun updateAccessSchedule(schedule: AccessSchedule?): Result<Unit> {
        return try {
            if (schedule == null) {
                securePreferences.saveAccessScheduleEnabled(false)
            } else {
                securePreferences.saveAccessScheduleEnabled(schedule.isEnabled)
                securePreferences.saveAccessScheduleStartTime(schedule.startTime.toString())
                securePreferences.saveAccessScheduleEndTime(schedule.endTime.toString())
            }
            Timber.d("Access schedule updated: $schedule")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update access schedule")
            Result.failure(e)
        }
    }

    override suspend fun checkAccessRestriction(): AccessRestriction {
        val accessSchedule = getAccessSchedule()
        return screenTimeManager.checkAccessRestriction(accessSchedule)
    }

    override suspend fun clearAllControls(): Result<Unit> {
        return try {
            // Clear PIN
            securePreferences.saveParentPin("")

            // Reset screen time
            val defaultConfig = ScreenTimeConfig(
                isEnabled = false,
                dailyLimitMinutes = 60,
                usedTodayMinutes = 0,
                lastResetDate = java.time.LocalDate.now().toString()
            )
            screenTimeManager.updateScreenTimeConfig(defaultConfig)

            // Clear access schedule
            updateAccessSchedule(null)

            Timber.d("All parental controls cleared")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear parental controls")
            Result.failure(e)
        }
    }
}
