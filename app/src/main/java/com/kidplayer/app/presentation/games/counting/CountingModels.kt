package com.kidplayer.app.presentation.games.counting

import kotlin.random.Random

/**
 * A counting object with bilingual support
 */
data class CountingObject(
    val emoji: String,
    val nameEn: String,
    val nameRo: String
) {
    fun getName(isRomanian: Boolean): String = if (isRomanian) nameRo else nameEn
}

/**
 * Objects that can be counted in the game
 */
object CountingObjects {
    val items = listOf(
        CountingObject("🍎", "apples", "mere"),
        CountingObject("🍌", "bananas", "banane"),
        CountingObject("🍊", "oranges", "portocale"),
        CountingObject("🍓", "strawberries", "căpșuni"),
        CountingObject("🍇", "grapes", "struguri"),
        CountingObject("⭐", "stars", "stele"),
        CountingObject("❤️", "hearts", "inimi"),
        CountingObject("🎈", "balloons", "baloane"),
        CountingObject("🌸", "flowers", "flori"),
        CountingObject("🍪", "cookies", "biscuiți"),
        CountingObject("🐶", "puppies", "cățeluși"),
        CountingObject("🐱", "kittens", "pisicuțe"),
        CountingObject("🐰", "bunnies", "iepurași"),
        CountingObject("🦋", "butterflies", "fluturi"),
        CountingObject("🐸", "frogs", "broscuțe"),
        CountingObject("🐠", "fish", "pești"),
        CountingObject("🌟", "stars", "stele"),
        CountingObject("🍬", "candies", "bomboane"),
        CountingObject("🎁", "presents", "cadouri"),
        CountingObject("🚗", "cars", "mașini")
    )

    fun random(): CountingObject = items.random()
}

/**
 * Game configuration
 */
object CountingConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
    const val OPTIONS_COUNT = 4

    // Count ranges by difficulty level
    fun getCountRange(level: Int): IntRange = when (level) {
        1 -> 1..5      // Easy: count 1-5 objects
        2 -> 3..10     // Medium: count 3-10 objects
        else -> 5..15  // Hard: count 5-15 objects
    }
}

/**
 * A counting challenge
 */
data class CountingChallenge(
    val emoji: String,
    val objectNameEn: String,
    val objectNameRo: String,
    val count: Int,
    val options: List<Int>,
    val objectPositions: List<ObjectPosition>
) {
    val correctAnswer: Int = count

    fun getObjectName(isRomanian: Boolean): String = if (isRomanian) objectNameRo else objectNameEn
}

/**
 * Position for an object on screen
 */
data class ObjectPosition(
    val x: Float, // 0.0 to 1.0 relative position
    val y: Float,
    val rotation: Float = 0f,
    val scale: Float = 1f
)

/**
 * Challenge generator
 */
object CountingGenerator {

    fun generateChallenge(level: Int): CountingChallenge {
        val countingObject = CountingObjects.random()
        val range = CountingConfig.getCountRange(level)
        val count = Random.nextInt(range.first, range.last + 1)

        // Generate positions for objects with some randomness
        val positions = generatePositions(count)

        // Generate wrong options
        val options = generateOptions(count)

        return CountingChallenge(
            emoji = countingObject.emoji,
            objectNameEn = countingObject.nameEn,
            objectNameRo = countingObject.nameRo,
            count = count,
            options = options,
            objectPositions = positions
        )
    }

    private fun generatePositions(count: Int): List<ObjectPosition> {
        val positions = mutableListOf<ObjectPosition>()

        // Create a grid-like distribution with some randomness
        val cols = when {
            count <= 3 -> 3
            count <= 6 -> 3
            count <= 9 -> 3
            else -> 4
        }
        val rows = (count + cols - 1) / cols

        for (i in 0 until count) {
            val col = i % cols
            val row = i / cols

            // Base position in grid
            val baseX = (col + 0.5f) / cols
            val baseY = (row + 0.5f) / rows.coerceAtLeast(1)

            // Add some randomness
            val offsetX = Random.nextFloat() * 0.1f - 0.05f
            val offsetY = Random.nextFloat() * 0.1f - 0.05f
            val rotation = Random.nextFloat() * 30f - 15f
            val scale = 0.9f + Random.nextFloat() * 0.2f

            positions.add(
                ObjectPosition(
                    x = (baseX + offsetX).coerceIn(0.1f, 0.9f),
                    y = (baseY + offsetY).coerceIn(0.1f, 0.9f),
                    rotation = rotation,
                    scale = scale
                )
            )
        }

        return positions.shuffled() // Shuffle to make counting order random
    }

    private fun generateOptions(correctAnswer: Int): List<Int> {
        val options = mutableSetOf(correctAnswer)

        while (options.size < CountingConfig.OPTIONS_COUNT) {
            val wrong = when {
                Random.nextBoolean() && correctAnswer < 20 -> correctAnswer + Random.nextInt(1, 4)
                correctAnswer > 2 -> correctAnswer - Random.nextInt(1, minOf(3, correctAnswer))
                else -> correctAnswer + Random.nextInt(1, 4)
            }
            if (wrong > 0) {
                options.add(wrong)
            }
        }

        return options.toList().shuffled()
    }
}
