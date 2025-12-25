package com.kidplayer.app.presentation.games.numberbonds

import kotlin.random.Random

/**
 * Game configuration
 */
object NumberBondsConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
    const val OPTIONS_COUNT = 4

    // Target numbers by level
    fun getTargetNumber(level: Int): Int = when (level) {
        1 -> 5   // Bonds to 5
        2 -> 10  // Bonds to 10
        else -> listOf(10, 15, 20).random() // Bonds to 10, 15, or 20
    }
}

/**
 * A number bonds problem
 */
data class NumberBondsProblem(
    val targetNumber: Int,
    val givenNumber: Int,
    val options: List<Int>
) {
    val correctAnswer: Int = targetNumber - givenNumber
}

/**
 * Problem generator
 */
object NumberBondsGenerator {

    fun generateProblem(level: Int): NumberBondsProblem {
        val target = NumberBondsConfig.getTargetNumber(level)

        // Generate a number less than target
        val given = Random.nextInt(1, target)
        val correct = target - given

        // Generate wrong options
        val options = mutableSetOf(correct)
        while (options.size < NumberBondsConfig.OPTIONS_COUNT) {
            val wrong = when {
                Random.nextBoolean() -> correct + Random.nextInt(1, 4)
                correct > 1 -> maxOf(0, correct - Random.nextInt(1, 3))
                else -> Random.nextInt(1, target)
            }
            if (wrong >= 0 && wrong <= target && wrong != correct) {
                options.add(wrong)
            }
        }

        return NumberBondsProblem(
            targetNumber = target,
            givenNumber = given,
            options = options.shuffled()
        )
    }
}

/**
 * Visual elements for number bonds
 */
object NumberBondsVisuals {
    val backgrounds = listOf(
        0xFFE3F2FD to 0xFF2196F3, // Blue
        0xFFFCE4EC to 0xFFE91E63, // Pink
        0xFFE8F5E9 to 0xFF4CAF50, // Green
        0xFFFFF3E0 to 0xFFFF9800, // Orange
        0xFFF3E5F5 to 0xFF9C27B0  // Purple
    )

    fun getColorsForTarget(target: Int): Pair<Long, Long> {
        return backgrounds[target % backgrounds.size]
    }
}
