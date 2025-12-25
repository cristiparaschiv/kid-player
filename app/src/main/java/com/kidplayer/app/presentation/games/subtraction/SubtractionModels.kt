package com.kidplayer.app.presentation.games.subtraction

import kotlin.random.Random

/**
 * Safari animals for visual subtraction
 */
object SubtractionEmojis {
    val animals = listOf(
        "🦁" to "Lions",
        "🐘" to "Elephants",
        "🦒" to "Giraffes",
        "🦓" to "Zebras",
        "🐒" to "Monkeys",
        "🦜" to "Parrots",
        "🐊" to "Crocodiles",
        "🦛" to "Hippos",
        "🐆" to "Leopards",
        "🦏" to "Rhinos"
    )

    fun random(): Pair<String, String> = animals.random()
}

/**
 * Game configuration
 */
object SubtractionConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
    const val OPTIONS_COUNT = 4

    fun getMaxNumber(level: Int): Int = when (level) {
        1 -> 5      // Start with up to 5
        2 -> 7      // Up to 7
        else -> 10  // Up to 10
    }
}

/**
 * A subtraction problem
 */
data class SubtractionProblem(
    val startCount: Int,
    val takeAway: Int,
    val emoji: String,
    val emojiName: String,
    val options: List<Int>
) {
    val correctAnswer: Int = startCount - takeAway
}

/**
 * Problem generator
 */
object SubtractionGenerator {

    fun generateProblem(level: Int): SubtractionProblem {
        val maxNum = SubtractionConfig.getMaxNumber(level)
        val (emoji, emojiName) = SubtractionEmojis.random()

        // Generate starting number and amount to take away
        val startCount = Random.nextInt(2, maxNum + 1)
        val takeAway = Random.nextInt(1, startCount)
        val correctAnswer = startCount - takeAway

        // Generate wrong options
        val options = mutableSetOf(correctAnswer)
        while (options.size < SubtractionConfig.OPTIONS_COUNT) {
            val wrong = when {
                Random.nextBoolean() -> correctAnswer + Random.nextInt(1, 4)
                correctAnswer > 0 -> maxOf(0, correctAnswer - Random.nextInt(1, 3))
                else -> Random.nextInt(1, 5)
            }
            if (wrong >= 0 && wrong != correctAnswer) {
                options.add(wrong)
            }
        }

        return SubtractionProblem(
            startCount = startCount,
            takeAway = takeAway,
            emoji = emoji,
            emojiName = emojiName,
            options = options.shuffled()
        )
    }
}
