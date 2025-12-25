package com.kidplayer.app.presentation.games.colormix

import androidx.compose.ui.graphics.Color

/**
 * Primary colors that can be mixed
 */
enum class PrimaryColor(val color: Color, val displayName: String, val emoji: String) {
    RED(Color(0xFFE53935), "Red", "🔴"),
    YELLOW(Color(0xFFFFEB3B), "Yellow", "🟡"),
    BLUE(Color(0xFF2196F3), "Blue", "🔵");

    companion object {
        fun fromColor(color: Color): PrimaryColor? = entries.find { it.color == color }
    }
}

/**
 * Secondary colors created by mixing
 */
enum class SecondaryColor(val color: Color, val displayName: String, val emoji: String) {
    ORANGE(Color(0xFFFF9800), "Orange", "🟠"),
    GREEN(Color(0xFF4CAF50), "Green", "🟢"),
    PURPLE(Color(0xFF9C27B0), "Purple", "🟣");
}

/**
 * Color mixing rules
 */
object ColorMixer {
    private val mixingRules = mapOf(
        setOf(PrimaryColor.RED, PrimaryColor.YELLOW) to SecondaryColor.ORANGE,
        setOf(PrimaryColor.BLUE, PrimaryColor.YELLOW) to SecondaryColor.GREEN,
        setOf(PrimaryColor.RED, PrimaryColor.BLUE) to SecondaryColor.PURPLE
    )

    fun mix(color1: PrimaryColor, color2: PrimaryColor): SecondaryColor? {
        if (color1 == color2) return null
        return mixingRules[setOf(color1, color2)]
    }

    fun getAllMixtures(): List<ColorMixture> {
        return mixingRules.map { (primaries, result) ->
            val list = primaries.toList()
            ColorMixture(list[0], list[1], result)
        }
    }
}

/**
 * Represents a color mixing combination
 */
data class ColorMixture(
    val color1: PrimaryColor,
    val color2: PrimaryColor,
    val result: SecondaryColor
)

/**
 * A puzzle asking what two colors make
 */
data class ColorMixPuzzle(
    val color1: PrimaryColor,
    val color2: PrimaryColor,
    val correctAnswer: SecondaryColor,
    val options: List<SecondaryColor>
)

/**
 * Game configuration
 */
object ColorMixConfig {
    const val TOTAL_ROUNDS = 9 // Each mixture shown 3 times
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -20
}

/**
 * Puzzle generator
 */
object ColorMixPuzzleGenerator {

    fun generatePuzzle(): ColorMixPuzzle {
        val mixtures = ColorMixer.getAllMixtures()
        val mixture = mixtures.random()

        // Shuffle options
        val options = SecondaryColor.entries.shuffled()

        return ColorMixPuzzle(
            color1 = mixture.color1,
            color2 = mixture.color2,
            correctAnswer = mixture.result,
            options = options
        )
    }
}
