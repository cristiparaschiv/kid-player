package com.kidplayer.app.presentation.games.lettermatch

import androidx.compose.ui.graphics.Color

/**
 * Letters with associated pictures (emojis) and words
 */
enum class LetterPicture(
    val letter: Char,
    val emoji: String,
    val word: String
) {
    A('A', "🍎", "Apple"),
    B('B', "🐻", "Bear"),
    C('C', "🐱", "Cat"),
    D('D', "🐕", "Dog"),
    E('E', "🐘", "Elephant"),
    F('F', "🐸", "Frog"),
    G('G', "🍇", "Grapes"),
    H('H', "🏠", "House"),
    I('I', "🍦", "Ice Cream"),
    J('J', "🎃", "Jack-o-lantern"),
    K('K', "🪁", "Kite"),
    L('L', "🦁", "Lion"),
    M('M', "🐵", "Monkey"),
    N('N', "👃", "Nose"),
    O('O', "🐙", "Octopus"),
    P('P', "🐷", "Pig"),
    Q('Q', "👸", "Queen"),
    R('R', "🌈", "Rainbow"),
    S('S', "⭐", "Star"),
    T('T', "🐢", "Turtle"),
    U('U', "☂️", "Umbrella"),
    V('V', "🎻", "Violin"),
    W('W', "🐋", "Whale"),
    X('X', "🎄", "Xmas Tree"),
    Y('Y', "🪀", "Yo-yo"),
    Z('Z', "🦓", "Zebra");

    companion object {
        fun random(): LetterPicture = entries.random()
        fun forLetter(letter: Char): LetterPicture? = entries.find { it.letter == letter }
    }
}

/**
 * Game mode types
 */
enum class LetterMatchMode(val displayName: String) {
    LETTER_TO_PICTURE("Match Letter to Picture"),
    PICTURE_TO_LETTER("Match Picture to Letter"),
    UPPER_TO_LOWER("Match Upper to Lower");
}

/**
 * A puzzle in the letter match game
 */
data class LetterMatchPuzzle(
    val mode: LetterMatchMode,
    val targetLetter: LetterPicture,
    val options: List<Any>, // Can be LetterPicture or Char depending on mode
    val correctAnswerIndex: Int
)

/**
 * Game configuration
 */
object LetterMatchConfig {
    const val TOTAL_ROUNDS = 12
    const val OPTIONS_COUNT = 4
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -15
}

/**
 * Puzzle generator
 */
object LetterMatchPuzzleGenerator {

    fun generatePuzzle(round: Int): LetterMatchPuzzle {
        // Cycle through modes for variety
        val mode = when (round % 3) {
            0 -> LetterMatchMode.LETTER_TO_PICTURE
            1 -> LetterMatchMode.PICTURE_TO_LETTER
            else -> LetterMatchMode.UPPER_TO_LOWER
        }

        val target = LetterPicture.random()

        return when (mode) {
            LetterMatchMode.LETTER_TO_PICTURE -> {
                // Show letter, pick correct picture
                val otherPictures = LetterPicture.entries
                    .filter { it != target }
                    .shuffled()
                    .take(LetterMatchConfig.OPTIONS_COUNT - 1)

                val options = (otherPictures + target).shuffled()
                val correctIndex = options.indexOf(target)

                LetterMatchPuzzle(
                    mode = mode,
                    targetLetter = target,
                    options = options,
                    correctAnswerIndex = correctIndex
                )
            }
            LetterMatchMode.PICTURE_TO_LETTER -> {
                // Show picture, pick correct letter
                val otherLetters = LetterPicture.entries
                    .filter { it != target }
                    .shuffled()
                    .take(LetterMatchConfig.OPTIONS_COUNT - 1)
                    .map { it.letter }

                val options = (otherLetters + target.letter).shuffled()
                val correctIndex = options.indexOf(target.letter)

                LetterMatchPuzzle(
                    mode = mode,
                    targetLetter = target,
                    options = options,
                    correctAnswerIndex = correctIndex
                )
            }
            LetterMatchMode.UPPER_TO_LOWER -> {
                // Show uppercase, pick correct lowercase
                val correctLower = target.letter.lowercaseChar()
                val otherLowers = LetterPicture.entries
                    .filter { it != target }
                    .shuffled()
                    .take(LetterMatchConfig.OPTIONS_COUNT - 1)
                    .map { it.letter.lowercaseChar() }

                val options = (otherLowers + correctLower).shuffled()
                val correctIndex = options.indexOf(correctLower)

                LetterMatchPuzzle(
                    mode = mode,
                    targetLetter = target,
                    options = options,
                    correctAnswerIndex = correctIndex
                )
            }
        }
    }
}

/**
 * Colors for letter cards
 */
object LetterColors {
    val cardColors = listOf(
        Color(0xFFE53935), // Red
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF00BCD4), // Cyan
    )

    fun forLetter(letter: Char): Color {
        val index = (letter.uppercaseChar() - 'A') % cardColors.size
        return cardColors[index]
    }
}
