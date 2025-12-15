package com.kidplayer.app.domain.model

import java.time.LocalTime

/**
 * Domain model representing parental control settings
 */
data class ParentalControls(
    val isPinSet: Boolean,
    val screenTimeConfig: ScreenTimeConfig,
    val accessSchedule: AccessSchedule?,
    val contentFiltering: ContentFiltering
)

/**
 * Screen time configuration
 */
data class ScreenTimeConfig(
    val isEnabled: Boolean,
    val dailyLimitMinutes: Int, // Daily time limit in minutes (e.g., 60 = 1 hour)
    val usedTodayMinutes: Int, // Minutes used today
    val lastResetDate: String // Date in ISO format (yyyy-MM-dd) when the counter was last reset
) {
    val remainingMinutes: Int
        get() = (dailyLimitMinutes - usedTodayMinutes).coerceAtLeast(0)

    val isLimitReached: Boolean
        get() = isEnabled && usedTodayMinutes >= dailyLimitMinutes
}

/**
 * Access schedule configuration (optional)
 * Defines when the app can be used
 */
data class AccessSchedule(
    val isEnabled: Boolean,
    val startTime: LocalTime, // e.g., 09:00
    val endTime: LocalTime, // e.g., 19:00 (7 PM)
    val allowedDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )
) {
    /**
     * Check if current time is within allowed access hours
     */
    fun isCurrentTimeAllowed(currentTime: LocalTime, currentDay: DayOfWeek): Boolean {
        if (!isEnabled) return true
        if (!allowedDays.contains(currentDay)) return false

        return if (endTime.isAfter(startTime)) {
            // Normal case: e.g., 9am-7pm
            currentTime >= startTime && currentTime <= endTime
        } else {
            // Overnight case: e.g., 8pm-6am
            currentTime >= startTime || currentTime <= endTime
        }
    }
}

/**
 * Days of the week
 */
enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

/**
 * Content filtering options (placeholder for future enhancement)
 */
data class ContentFiltering(
    val isEnabled: Boolean,
    val ageRating: AgeRating = AgeRating.ALL
)

/**
 * Age rating for content filtering
 */
enum class AgeRating {
    ALL,
    KIDS_UNDER_5,
    KIDS_5_TO_7,
    KIDS_8_TO_12,
    TEENS_13_PLUS
}

/**
 * Result of PIN verification
 */
sealed class PinVerificationResult {
    object Success : PinVerificationResult()
    object Failure : PinVerificationResult()
    object NotSet : PinVerificationResult()
}

/**
 * Access restriction information
 */
sealed class AccessRestriction {
    object Allowed : AccessRestriction()
    data class TimeLimitReached(val limitMinutes: Int) : AccessRestriction()
    data class OutsideSchedule(val startTime: LocalTime, val endTime: LocalTime) : AccessRestriction()
}
