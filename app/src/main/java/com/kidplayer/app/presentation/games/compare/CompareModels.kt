package com.kidplayer.app.presentation.games.compare

import kotlin.random.Random

/**
 * Emojis for visual comparison
 */
object CompareEmojis {
    val items = listOf(
        "🍎", "🍊", "🍋", "🍇", "🍓", "🍌",
        "⭐", "❤️", "💎", "🌸", "🎈", "🍪",
        "🐶", "🐱", "🐰", "🐸", "🦋", "🐝"
    )

    fun random(): String = items.random()
}

/**
 * Comparison types
 */
enum class ComparisonResult {
    GREATER,    // Left > Right
    LESS,       // Left < Right
    EQUAL       // Left = Right
}

/**
 * Game configuration
 */
object CompareConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25

    fun getMaxNumber(level: Int): Int = when (level) {
        1 -> 5
        2 -> 7
        else -> 10
    }
}

/**
 * A comparison problem
 */
data class CompareProblem(
    val leftCount: Int,
    val rightCount: Int,
    val leftEmoji: String,
    val rightEmoji: String
) {
    val correctAnswer: ComparisonResult = when {
        leftCount > rightCount -> ComparisonResult.GREATER
        leftCount < rightCount -> ComparisonResult.LESS
        else -> ComparisonResult.EQUAL
    }

    val correctSymbol: String = when (correctAnswer) {
        ComparisonResult.GREATER -> ">"
        ComparisonResult.LESS -> "<"
        ComparisonResult.EQUAL -> "="
    }
}

/**
 * Problem generator
 */
object CompareGenerator {

    fun generateProblem(level: Int): CompareProblem {
        val max = CompareConfig.getMaxNumber(level)

        // Generate two different counts (occasionally equal)
        val leftCount = Random.nextInt(1, max + 1)
        val rightCount = if (Random.nextFloat() < 0.15f) {
            leftCount // 15% chance of equal
        } else {
            var count = Random.nextInt(1, max + 1)
            while (count == leftCount && max > 1) {
                count = Random.nextInt(1, max + 1)
            }
            count
        }

        // Use same emoji for both sides (easier to compare)
        val emoji = CompareEmojis.random()

        return CompareProblem(
            leftCount = leftCount,
            rightCount = rightCount,
            leftEmoji = emoji,
            rightEmoji = emoji
        )
    }
}
