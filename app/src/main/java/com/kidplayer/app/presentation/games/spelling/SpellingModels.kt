package com.kidplayer.app.presentation.games.spelling

import kotlin.random.Random

/**
 * A word for spelling with its image representation
 * Bilingual support for English and Romanian
 */
data class SpellingWord(
    val wordEn: String,
    val wordRo: String,
    val emoji: String,
    val hintEn: String,
    val hintRo: String
) {
    fun getWord(isRomanian: Boolean): String = if (isRomanian) wordRo else wordEn
    fun getHint(isRomanian: Boolean): String = if (isRomanian) hintRo else hintEn
}

/**
 * Word lists by difficulty - Bilingual
 */
object SpellingWords {
    // 3-letter words (Level 1) - Easy
    val easyWords = listOf(
        SpellingWord("CAT", "PIS", "🐱", "A furry pet that says meow", "O pisică zice miau"),
        SpellingWord("DOG", "CÂN", "🐶", "A pet that barks", "Un câine latră"),
        SpellingWord("SUN", "SOA", "☀️", "Shines in the sky", "Strălucește pe cer"),
        SpellingWord("BEE", "ALB", "🐝", "Makes honey", "Face miere"),
        SpellingWord("HAT", "PĂL", "🎩", "You wear it on your head", "O porți pe cap"),
        SpellingWord("BUS", "BUS", "🚌", "A big vehicle for many people", "Un vehicul mare"),
        SpellingWord("CAR", "CAR", "🚗", "You drive it", "Îl conduci"),
        SpellingWord("CUP", "CAN", "🍵", "You drink from it", "Bei din ea"),
        SpellingWord("EGG", "OUĂ", "🥚", "Comes from a chicken", "Vine de la găină"),
        SpellingWord("PIG", "POR", "🐷", "Says oink", "Face groh"),
        SpellingWord("BED", "PAT", "🛏️", "You sleep on it", "Dormi în el"),
        SpellingWord("BOX", "CUT", "📦", "You put things inside", "Pui lucruri în ea"),
        SpellingWord("COW", "VAC", "🐄", "Gives us milk", "Ne dă lapte"),
        SpellingWord("FAN", "VÂN", "🌀", "Keeps you cool", "Te răcorește"),
        SpellingWord("FOX", "VUL", "🦊", "Orange and clever", "Portocalie și isteață")
    )

    // 4-letter words (Level 2) - Medium
    val mediumWords = listOf(
        SpellingWord("FISH", "PEȘTE", "🐟", "Lives in water", "Trăiește în apă"),
        SpellingWord("BIRD", "PASĂR", "🐦", "Has wings and flies", "Are aripi și zboară"),
        SpellingWord("FROG", "BROSC", "🐸", "Says ribbit", "Face oac"),
        SpellingWord("STAR", "STEA", "⭐", "Twinkles at night", "Sclipește noaptea"),
        SpellingWord("MOON", "LUNĂ", "🌙", "Shines at night", "Strălucește noaptea"),
        SpellingWord("TREE", "COPA", "🌳", "Has leaves and branches", "Are frunze și ramuri"),
        SpellingWord("CAKE", "TORT", "🎂", "A birthday treat", "Un tort de ziua ta"),
        SpellingWord("DUCK", "RAȚĂ", "🦆", "Says quack", "Face mac"),
        SpellingWord("BEAR", "URS", "🐻", "A big furry animal", "Un animal mare și pufos"),
        SpellingWord("LION", "LEU", "🦁", "King of the jungle", "Regele junglei"),
        SpellingWord("BOOK", "CART", "📚", "You read it", "O citești"),
        SpellingWord("BALL", "MING", "⚽", "You can kick or throw it", "O poți lovi sau arunca"),
        SpellingWord("RAIN", "PLOA", "🌧️", "Falls from clouds", "Cade din nori"),
        SpellingWord("BOAT", "BARC", "⛵", "Floats on water", "Plutește pe apă"),
        SpellingWord("DOOR", "UȘĂ", "🚪", "You open and close it", "O deschizi și închizi")
    )

    // 5-6 letter words (Level 3) - Hard
    val hardWords = listOf(
        SpellingWord("APPLE", "MĂR", "🍎", "A red fruit", "Un fruct roșu"),
        SpellingWord("HORSE", "CAL", "🐴", "You can ride it", "Poți să-l călărești"),
        SpellingWord("HOUSE", "CASĂ", "🏠", "Where you live", "Unde locuiești"),
        SpellingWord("HAPPY", "FERIC", "😊", "A good feeling", "O senzație bună"),
        SpellingWord("WATER", "APĂ", "💧", "You drink it", "O bei"),
        SpellingWord("CLOUD", "NOR", "☁️", "Floats in the sky", "Plutește pe cer"),
        SpellingWord("MOUSE", "ȘOARE", "🐭", "A small animal", "Un animal mic"),
        SpellingWord("SNAKE", "ȘARP", "🐍", "Has no legs", "Nu are picioare"),
        SpellingWord("PIZZA", "PIZZA", "🍕", "A yummy food", "O mâncare gustoasă"),
        SpellingWord("TIGER", "TIGRU", "🐯", "Has stripes", "Are dungi"),
        SpellingWord("CANDY", "BOMBO", "🍬", "Sweet treat", "Dulce"),
        SpellingWord("PLANE", "AVION", "✈️", "Flies in the sky", "Zboară pe cer"),
        SpellingWord("TRAIN", "TREN", "🚂", "Goes on tracks", "Merge pe șine"),
        SpellingWord("QUEEN", "REGIN", "👑", "Wears a crown", "Poartă o coroană"),
        SpellingWord("ROBOT", "ROBOT", "🤖", "A machine friend", "Un prieten mașină")
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

    fun addDistractorLetters(word: String, count: Int = 2, isRomanian: Boolean = false): List<LetterTile> {
        // Romanian alphabet includes: Ă, Â, Î, Ș, Ț
        val alphabet = if (isRomanian) {
            "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ"
        } else {
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        }
        val wordChars = word.toSet()
        val distractors = alphabet.filter { it !in wordChars }.toList().shuffled().take(count)

        val allLetters: List<Char> = word.toList() + distractors
        return allLetters.mapIndexed { index, char ->
            LetterTile(id = index, letter = char)
        }.shuffled()
    }
}
