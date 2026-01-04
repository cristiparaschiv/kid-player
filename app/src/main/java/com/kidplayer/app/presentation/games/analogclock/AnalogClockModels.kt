package com.kidplayer.app.presentation.games.analogclock

import kotlin.random.Random

/**
 * Game configuration for Analog Clock
 */
object AnalogClockConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
    const val OPTIONS_COUNT = 4
}

/**
 * Represents a time on a clock
 */
data class ClockTime(
    val hour: Int,    // 1-12
    val minute: Int   // 0, 15, 30, 45 for easy; 0-55 by 5s for harder
) {
    /**
     * Format time as readable string (e.g., "3:30" or "10:15")
     */
    fun format(): String {
        val displayHour = if (hour == 0) 12 else hour
        return "$displayHour:${minute.toString().padStart(2, '0')}"
    }

    /**
     * Calculate hour hand angle (in degrees from 12 o'clock)
     * Hour hand moves 30 degrees per hour + 0.5 degrees per minute
     */
    fun hourHandAngle(): Float {
        val hourAngle = (hour % 12) * 30f
        val minuteContribution = minute * 0.5f
        return hourAngle + minuteContribution
    }

    /**
     * Calculate minute hand angle (in degrees from 12 o'clock)
     * Minute hand moves 6 degrees per minute
     */
    fun minuteHandAngle(): Float {
        return minute * 6f
    }
}

/**
 * A single clock reading problem
 */
data class ClockProblem(
    val correctTime: ClockTime,
    val options: List<ClockTime>
) {
    init {
        require(options.size == AnalogClockConfig.OPTIONS_COUNT) {
            "Must have exactly ${AnalogClockConfig.OPTIONS_COUNT} options"
        }
        require(correctTime in options) {
            "Correct time must be in options"
        }
    }
}

/**
 * Generator for clock problems based on difficulty
 */
object ClockProblemGenerator {

    /**
     * Generate a clock problem for a given level (1-3)
     * Level 1: Hours only (minute = 0)
     * Level 2: Half and quarter hours (0, 15, 30, 45)
     * Level 3: Five-minute increments (0, 5, 10, 15, ..., 55)
     */
    fun generateProblem(level: Int): ClockProblem {
        val correctTime = generateRandomTime(level)
        val options = generateOptions(correctTime, level)
        return ClockProblem(correctTime, options)
    }

    private fun generateRandomTime(level: Int): ClockTime {
        val hour = Random.nextInt(1, 13) // 1-12
        val minute = when (level) {
            1 -> 0 // Hours only
            2 -> listOf(0, 15, 30, 45).random() // Quarter hours
            else -> (0..11).random() * 5 // Five-minute increments
        }
        return ClockTime(hour, minute)
    }

    private fun generateOptions(correct: ClockTime, level: Int): List<ClockTime> {
        val options = mutableSetOf(correct)

        while (options.size < AnalogClockConfig.OPTIONS_COUNT) {
            val wrongTime = generateWrongOption(correct, level)
            if (wrongTime != correct) {
                options.add(wrongTime)
            }
        }

        return options.shuffled()
    }

    private fun generateWrongOption(correct: ClockTime, level: Int): ClockTime {
        return when (Random.nextInt(4)) {
            0 -> {
                // Wrong hour, same minute
                var newHour = Random.nextInt(1, 13)
                while (newHour == correct.hour) {
                    newHour = Random.nextInt(1, 13)
                }
                ClockTime(newHour, correct.minute)
            }
            1 -> {
                // Same hour, wrong minute
                val possibleMinutes = when (level) {
                    1 -> listOf(0)
                    2 -> listOf(0, 15, 30, 45)
                    else -> (0..11).map { it * 5 }
                }
                var newMinute = possibleMinutes.random()
                while (newMinute == correct.minute && possibleMinutes.size > 1) {
                    newMinute = possibleMinutes.random()
                }
                ClockTime(correct.hour, newMinute)
            }
            2 -> {
                // Hour off by 1 (common mistake)
                val newHour = if (correct.hour == 12) 1 else if (correct.hour == 1) 12 else correct.hour + listOf(-1, 1).random()
                ClockTime(newHour.coerceIn(1, 12), correct.minute)
            }
            else -> {
                // Completely random time
                generateRandomTime(level)
            }
        }
    }
}
