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
 * Romanian words are complete words appropriate for children
 */
object SpellingWords {
    // Easy words (3-4 letters) - Level 1
    val easyWords = listOf(
        SpellingWord("CAT", "PISICĂ", "🐱", "A furry pet that says meow", "Face miau"),
        SpellingWord("DOG", "CÂINE", "🐶", "A pet that barks", "Latră"),
        SpellingWord("SUN", "SOARE", "☀️", "Shines in the sky", "Strălucește pe cer"),
        SpellingWord("BEE", "ALBINĂ", "🐝", "Makes honey", "Face miere"),
        SpellingWord("HAT", "CĂCIULĂ", "🎩", "You wear it on your head", "O porți pe cap"),
        SpellingWord("BUS", "AUTOBUZ", "🚌", "A big vehicle for many people", "Un vehicul mare"),
        SpellingWord("CAR", "MAȘINĂ", "🚗", "You drive it", "O conduci"),
        SpellingWord("CUP", "CANĂ", "🍵", "You drink from it", "Bei din ea"),
        SpellingWord("EGG", "OU", "🥚", "Comes from a chicken", "Vine de la găină"),
        SpellingWord("PIG", "PORC", "🐷", "Says oink", "Face groh"),
        SpellingWord("BED", "PAT", "🛏️", "You sleep on it", "Dormi în el"),
        SpellingWord("BOX", "CUTIE", "📦", "You put things inside", "Pui lucruri în ea"),
        SpellingWord("COW", "VACĂ", "🐄", "Gives us milk", "Ne dă lapte"),
        SpellingWord("KEY", "CHEIE", "🔑", "Opens doors", "Deschide uși"),
        SpellingWord("FOX", "VULPE", "🦊", "Orange and clever", "Portocalie și isteață")
    )

    // Medium words (4-5 letters) - Level 2
    val mediumWords = listOf(
        SpellingWord("FISH", "PEȘTE", "🐟", "Lives in water", "Trăiește în apă"),
        SpellingWord("BIRD", "PASĂRE", "🐦", "Has wings and flies", "Are aripi și zboară"),
        SpellingWord("FROG", "BROASCĂ", "🐸", "Says ribbit", "Face oac"),
        SpellingWord("STAR", "STEA", "⭐", "Twinkles at night", "Sclipește noaptea"),
        SpellingWord("MOON", "LUNĂ", "🌙", "Shines at night", "Strălucește noaptea"),
        SpellingWord("TREE", "COPAC", "🌳", "Has leaves and branches", "Are frunze și ramuri"),
        SpellingWord("CAKE", "TORT", "🎂", "A birthday treat", "Un tort de ziua ta"),
        SpellingWord("DUCK", "RAȚĂ", "🦆", "Says quack", "Face mac"),
        SpellingWord("BEAR", "URS", "🐻", "A big furry animal", "Un animal mare și pufos"),
        SpellingWord("LION", "LEU", "🦁", "King of the jungle", "Regele junglei"),
        SpellingWord("BOOK", "CARTE", "📚", "You read it", "O citești"),
        SpellingWord("BALL", "MINGE", "⚽", "You can kick or throw it", "O poți lovi sau arunca"),
        SpellingWord("RAIN", "PLOAIE", "🌧️", "Falls from clouds", "Cade din nori"),
        SpellingWord("BOAT", "BARCĂ", "⛵", "Floats on water", "Plutește pe apă"),
        SpellingWord("DOOR", "UȘĂ", "🚪", "You open and close it", "O deschizi și închizi")
    )

    // Hard words (5-6 letters) - Level 3
    val hardWords = listOf(
        SpellingWord("APPLE", "MĂR", "🍎", "A red fruit", "Un fruct roșu"),
        SpellingWord("HORSE", "CAL", "🐴", "You can ride it", "Poți să-l călărești"),
        SpellingWord("HOUSE", "CASĂ", "🏠", "Where you live", "Unde locuiești"),
        SpellingWord("HAPPY", "FERICIT", "😊", "A good feeling", "O senzație bună"),
        SpellingWord("WATER", "APĂ", "💧", "You drink it", "O bei"),
        SpellingWord("CLOUD", "NOR", "☁️", "Floats in the sky", "Plutește pe cer"),
        SpellingWord("MOUSE", "ȘOARECE", "🐭", "A small animal", "Un animal mic"),
        SpellingWord("SNAKE", "ȘARPE", "🐍", "Has no legs", "Nu are picioare"),
        SpellingWord("PIZZA", "PIZZA", "🍕", "A yummy food", "O mâncare gustoasă"),
        SpellingWord("TIGER", "TIGRU", "🐯", "Has stripes", "Are dungi"),
        SpellingWord("CANDY", "BOMBOANĂ", "🍬", "Sweet treat", "Dulce"),
        SpellingWord("PLANE", "AVION", "✈️", "Flies in the sky", "Zboară pe cer"),
        SpellingWord("TRAIN", "TREN", "🚂", "Goes on tracks", "Merge pe șine"),
        SpellingWord("QUEEN", "REGINĂ", "👑", "Wears a crown", "Poartă o coroană"),
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
