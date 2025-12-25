package com.kidplayer.app.presentation.games.addition

import kotlin.random.Random

/**
 * Emoji groups for visual addition
 */
object AdditionEmojis {
    val groups = listOf(
        "🍎" to "Apples",
        "🍌" to "Bananas",
        "🍊" to "Oranges",
        "🍇" to "Grapes",
        "🍓" to "Strawberries",
        "🐶" to "Dogs",
        "🐱" to "Cats",
        "🐰" to "Bunnies",
        "🐸" to "Frogs",
        "🦋" to "Butterflies",
        "⭐" to "Stars",
        "❤️" to "Hearts",
        "🌸" to "Flowers",
        "🎈" to "Balloons",
        "🍪" to "Cookies"
    )

    fun random(): Pair<String, String> = groups.random()
}

/**
 * Game configuration
 */
object AdditionConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
    const val OPTIONS_COUNT = 4

    // Difficulty ranges by level
    fun getMaxNumber(level: Int): Int = when (level) {
        1 -> 5      // Sums up to 5
        2 -> 7      // Sums up to 7
        else -> 10  // Sums up to 10
    }
}

/**
 * An addition problem
 */
data class AdditionProblem(
    val num1: Int,
    val num2: Int,
    val emoji: String,
    val emojiName: String,
    val options: List<Int>
) {
    val correctAnswer: Int = num1 + num2
}

/**
 * Problem generator
 */
object AdditionGenerator {

    fun generateProblem(level: Int): AdditionProblem {
        val maxSum = AdditionConfig.getMaxNumber(level)
        val (emoji, emojiName) = AdditionEmojis.random()

        // Generate two numbers that sum to at most maxSum
        val num1 = Random.nextInt(1, maxSum)
        val num2 = Random.nextInt(1, maxSum - num1 + 1)
        val correctAnswer = num1 + num2

        // Generate wrong options
        val options = mutableSetOf(correctAnswer)
        while (options.size < AdditionConfig.OPTIONS_COUNT) {
            val wrong = when {
                Random.nextBoolean() -> correctAnswer + Random.nextInt(1, 4)
                correctAnswer > 1 -> correctAnswer - Random.nextInt(1, minOf(3, correctAnswer))
                else -> correctAnswer + Random.nextInt(1, 4)
            }
            if (wrong > 0 && wrong != correctAnswer) {
                options.add(wrong)
            }
        }

        return AdditionProblem(
            num1 = num1,
            num2 = num2,
            emoji = emoji,
            emojiName = emojiName,
            options = options.shuffled()
        )
    }
}
