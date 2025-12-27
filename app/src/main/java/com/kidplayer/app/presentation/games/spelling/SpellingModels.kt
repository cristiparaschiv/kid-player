package com.kidplayer.app.presentation.games.spelling

import kotlin.random.Random

/**
 * A word for spelling with its image representation
 */
data class SpellingWord(
    val word: String,
    val emoji: String,
    val hint: String
)

/**
 * Word lists by difficulty
 */
object SpellingWords {
    // 3-letter words (Level 1)
    val easyWords = listOf(
        SpellingWord("CAT", "🐱", "A furry pet that says meow"),
        SpellingWord("DOG", "🐶", "A pet that barks"),
        SpellingWord("SUN", "☀️", "Shines in the sky"),
        SpellingWord("BEE", "🐝", "Makes honey"),
        SpellingWord("HAT", "🎩", "You wear it on your head"),
        SpellingWord("BUS", "🚌", "A big vehicle for many people"),
        SpellingWord("CAR", "🚗", "You drive it"),
        SpellingWord("CUP", "🍵", "You drink from it"),
        SpellingWord("EGG", "🥚", "Comes from a chicken"),
        SpellingWord("PIG", "🐷", "Says oink"),
        SpellingWord("BED", "🛏️", "You sleep on it"),
        SpellingWord("BOX", "📦", "You put things inside"),
        SpellingWord("COW", "🐄", "Gives us milk"),
        SpellingWord("FAN", "🌀", "Keeps you cool"),
        SpellingWord("FOX", "🦊", "Orange and clever")
    )

    // 4-letter words (Level 2)
    val mediumWords = listOf(
        SpellingWord("FISH", "🐟", "Lives in water"),
        SpellingWord("BIRD", "🐦", "Has wings and flies"),
        SpellingWord("FROG", "🐸", "Says ribbit"),
        SpellingWord("STAR", "⭐", "Twinkles at night"),
        SpellingWord("MOON", "🌙", "Shines at night"),
        SpellingWord("TREE", "🌳", "Has leaves and branches"),
        SpellingWord("CAKE", "🎂", "A birthday treat"),
        SpellingWord("DUCK", "🦆", "Says quack"),
        SpellingWord("BEAR", "🐻", "A big furry animal"),
        SpellingWord("LION", "🦁", "King of the jungle"),
        SpellingWord("BOOK", "📚", "You read it"),
        SpellingWord("BALL", "⚽", "You can kick or throw it"),
        SpellingWord("RAIN", "🌧️", "Falls from clouds"),
        SpellingWord("BOAT", "⛵", "Floats on water"),
        SpellingWord("DOOR", "🚪", "You open and close it")
    )

    // 5-6 letter words (Level 3)
    val hardWords = listOf(
        SpellingWord("APPLE", "🍎", "A red fruit"),
        SpellingWord("HORSE", "🐴", "You can ride it"),
        SpellingWord("HOUSE", "🏠", "Where you live"),
        SpellingWord("HAPPY", "😊", "A good feeling"),
        SpellingWord("WATER", "💧", "You drink it"),
        SpellingWord("CLOUD", "☁️", "Floats in the sky"),
        SpellingWord("MOUSE", "🐭", "A small animal"),
        SpellingWord("SNAKE", "🐍", "Has no legs"),
        SpellingWord("PIZZA", "🍕", "A yummy food"),
        SpellingWord("TIGER", "🐯", "Has stripes"),
        SpellingWord("CANDY", "🍬", "Sweet treat"),
        SpellingWord("PLANE", "✈️", "Flies in the sky"),
        SpellingWord("TRAIN", "🚂", "Goes on tracks"),
        SpellingWord("QUEEN", "👑", "Wears a crown"),
        SpellingWord("ROBOT", "🤖", "A machine friend")
    )

    fun getWordsForLevel(level: Int): List<SpellingWord> = when (level) {
        1 -> easyWords
        2 -> mediumWords
        else -> hardWords
    }

    fun getRandomWord(level: Int): SpellingWord {
        return getWordsForLevel(level).random()
    }
}

/**
 * Game configuration
 */
object SpellingConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT_LETTER = 20
    const val POINTS_WRONG_LETTER = -10
    const val BONUS_COMPLETE_WORD = 50
}

/**
 * A letter tile that can be dragged
 */
data class LetterTile(
    val id: Int,
    val letter: Char,
    val isPlaced: Boolean = false,
    val placedIndex: Int = -1
)

/**
 * Generator for scrambled letters
 */
object SpellingGenerator {

    fun scrambleLetters(word: String): List<LetterTile> {
        return word.mapIndexed { index, char ->
            LetterTile(id = index, letter = char)
        }.shuffled()
    }

    fun addDistractorLetters(word: String, count: Int = 2): List<LetterTile> {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val wordChars = word.toSet()
        val distractors = alphabet.filter { it !in wordChars }.toList().shuffled().take(count)

        val allLetters: List<Char> = word.toList() + distractors
        return allLetters.mapIndexed { index, char ->
            LetterTile(id = index, letter = char)
        }.shuffled()
    }
}
