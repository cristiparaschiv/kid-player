package com.kidplayer.app.presentation.games.pattern

import androidx.compose.ui.graphics.Color

/**
 * Shape types for pattern game
 */
enum class PatternShape(val symbol: String) {
    CIRCLE("●"),
    SQUARE("■"),
    TRIANGLE("▲"),
    DIAMOND("◆"),
    STAR("★"),
    HEART("♥");

    companion object {
        fun random(): PatternShape = entries.random()
    }
}

/**
 * Colors used in patterns
 */
object PatternColors {
    val RED = Color(0xFFE53935)
    val BLUE = Color(0xFF2196F3)
    val GREEN = Color(0xFF4CAF50)
    val YELLOW = Color(0xFFFFEB3B)
    val PURPLE = Color(0xFF9C27B0)
    val ORANGE = Color(0xFFFF9800)

    val all = listOf(RED, BLUE, GREEN, YELLOW, PURPLE, ORANGE)

    fun random(): Color = all.random()
}

/**
 * A pattern element (shape + color combination)
 */
data class PatternElement(
    val shape: PatternShape,
    val color: Color
) {
    companion object {
        fun random(): PatternElement = PatternElement(
            shape = PatternShape.random(),
            color = PatternColors.random()
        )
    }
}

/**
 * Pattern types that can be generated
 */
enum class PatternType {
    SHAPE_REPEAT,      // Same shape, same color repeats: ●●●?
    COLOR_REPEAT,      // Same shape, color pattern: 🔴🔵🔴?
    SHAPE_SEQUENCE,    // Different shapes, same color: ●■▲?
    ALTERNATING,       // Two elements alternate: ●■●■?
    ABC_PATTERN        // Three element cycle: ●■▲●■?
}

/**
 * Game configuration
 */
object PatternConfig {
    const val INITIAL_PATTERN_LENGTH = 3
    const val MAX_PATTERN_LENGTH = 6
    const val OPTIONS_COUNT = 4
    const val ROUNDS_PER_LEVEL = 3
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
}

/**
 * Pattern generation logic
 */
object PatternGenerator {

    /**
     * Generate a pattern with a missing element
     * Returns: Pair(pattern with null for missing, correct answer)
     */
    fun generatePattern(length: Int, level: Int): PatternPuzzle {
        val patternType = selectPatternType(level)
        val pattern = createPattern(patternType, length)
        val missingIndex = length - 1 // Always missing the last one for simplicity
        val correctAnswer = pattern[missingIndex]

        val patternWithMissing: MutableList<PatternElement?> = pattern.toMutableList()
        patternWithMissing[missingIndex] = null

        val options = generateOptions(correctAnswer, patternType)

        return PatternPuzzle(
            pattern = patternWithMissing,
            correctAnswer = correctAnswer,
            options = options,
            patternType = patternType
        )
    }

    private fun selectPatternType(level: Int): PatternType {
        return when (level) {
            1 -> listOf(PatternType.SHAPE_REPEAT, PatternType.ALTERNATING).random()
            2 -> listOf(PatternType.COLOR_REPEAT, PatternType.ALTERNATING, PatternType.SHAPE_SEQUENCE).random()
            else -> PatternType.entries.random()
        }
    }

    private fun createPattern(type: PatternType, length: Int): List<PatternElement> {
        return when (type) {
            PatternType.SHAPE_REPEAT -> {
                val element = PatternElement.random()
                List(length) { element }
            }
            PatternType.COLOR_REPEAT -> {
                val shape = PatternShape.random()
                val colors = PatternColors.all.shuffled().take(2)
                List(length) { i -> PatternElement(shape, colors[i % colors.size]) }
            }
            PatternType.SHAPE_SEQUENCE -> {
                val color = PatternColors.random()
                val shapes = PatternShape.entries.shuffled().take(length)
                shapes.map { PatternElement(it, color) }
            }
            PatternType.ALTERNATING -> {
                val elem1 = PatternElement.random()
                var elem2 = PatternElement.random()
                while (elem2 == elem1) elem2 = PatternElement.random()
                List(length) { i -> if (i % 2 == 0) elem1 else elem2 }
            }
            PatternType.ABC_PATTERN -> {
                val elements = listOf(
                    PatternElement.random(),
                    PatternElement.random(),
                    PatternElement.random()
                ).distinctBy { it.shape to it.color }.take(3)
                if (elements.size < 3) {
                    // Fallback to alternating
                    val elem1 = PatternElement.random()
                    val elem2 = PatternElement.random()
                    List(length) { i -> if (i % 2 == 0) elem1 else elem2 }
                } else {
                    List(length) { i -> elements[i % 3] }
                }
            }
        }
    }

    private fun generateOptions(correct: PatternElement, patternType: PatternType): List<PatternElement> {
        val options = mutableSetOf(correct)

        // Generate distractors
        while (options.size < PatternConfig.OPTIONS_COUNT) {
            val distractor = when (patternType) {
                PatternType.SHAPE_REPEAT, PatternType.SHAPE_SEQUENCE -> {
                    // Change shape
                    PatternElement(PatternShape.random(), correct.color)
                }
                PatternType.COLOR_REPEAT -> {
                    // Change color
                    PatternElement(correct.shape, PatternColors.random())
                }
                else -> PatternElement.random()
            }
            if (distractor != correct) {
                options.add(distractor)
            }
        }

        return options.shuffled()
    }
}

/**
 * A pattern puzzle to solve
 */
data class PatternPuzzle(
    val pattern: List<PatternElement?>,
    val correctAnswer: PatternElement,
    val options: List<PatternElement>,
    val patternType: PatternType
)
