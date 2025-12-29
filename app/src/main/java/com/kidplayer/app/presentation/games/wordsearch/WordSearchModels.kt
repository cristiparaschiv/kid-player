package com.kidplayer.app.presentation.games.wordsearch

import kotlin.random.Random

/**
 * Direction for word placement
 */
enum class WordDirection {
    HORIZONTAL,      // Left to right
    VERTICAL,        // Top to bottom
    DIAGONAL_DOWN,   // Top-left to bottom-right
    DIAGONAL_UP      // Bottom-left to top-right
}

/**
 * A hidden word in the puzzle
 */
data class HiddenWord(
    val word: String,
    val startRow: Int,
    val startCol: Int,
    val direction: WordDirection,
    val found: Boolean = false
)

/**
 * A cell in the grid
 */
data class GridCell(
    val row: Int,
    val col: Int,
    val letter: Char,
    val isPartOfWord: Boolean = false,
    val isSelected: Boolean = false,
    val isFound: Boolean = false
)

/**
 * Game configuration
 */
object WordSearchConfig {
    // Grid sizes by level
    fun getGridSize(level: Int): Int = when (level) {
        1 -> 6
        2 -> 8
        else -> 10
    }

    // Number of words by level
    fun getWordCount(level: Int): Int = when (level) {
        1 -> 3
        2 -> 4
        else -> 5
    }

    // Allowed directions by level
    fun getAllowedDirections(level: Int): List<WordDirection> = when (level) {
        1 -> listOf(WordDirection.HORIZONTAL, WordDirection.VERTICAL)
        2 -> listOf(WordDirection.HORIZONTAL, WordDirection.VERTICAL, WordDirection.DIAGONAL_DOWN)
        else -> WordDirection.entries.toList()
    }

    const val POINTS_PER_WORD = 100
}

/**
 * Word lists by category - Bilingual support
 */
object WordSearchWords {
    // English word lists
    val animalsEn = listOf("CAT", "DOG", "PIG", "COW", "FOX", "BEE", "ANT", "OWL", "BAT", "HEN")
    val colorsEn = listOf("RED", "BLUE", "PINK", "GOLD", "GRAY", "TAN", "LIME")
    val foodEn = listOf("PIE", "HAM", "JAM", "EGG", "NUT", "PEA", "FIG")
    val natureEn = listOf("SUN", "SKY", "SEA", "DEW", "FOG", "MUD", "ICE")

    val mediumAnimalsEn = listOf("BEAR", "DEER", "FROG", "DUCK", "FISH", "BIRD", "LION", "WOLF")
    val mediumFoodEn = listOf("CAKE", "RICE", "BEAN", "CORN", "PLUM", "PEAR", "LIME")
    val mediumNatureEn = listOf("TREE", "LEAF", "RAIN", "WIND", "SNOW", "MOON", "STAR")

    val hardAnimalsEn = listOf("HORSE", "SNAKE", "TIGER", "MOUSE", "SHEEP", "ZEBRA")
    val hardFoodEn = listOf("APPLE", "GRAPE", "LEMON", "BREAD", "PIZZA", "MELON")
    val hardNatureEn = listOf("CLOUD", "RIVER", "GRASS", "STORM", "BEACH", "STONE")

    // Romanian word lists (words with simple letters for the grid)
    val animalsRo = listOf("URS", "LEU", "LUP", "RAC", "PUI", "CAL", "OAI", "VUL", "SOC", "COT")
    val colorsRo = listOf("ALB", "GRI", "ROZ", "MOV", "VIS")
    val foodRo = listOf("MĂR", "NUC", "PIE", "OUA", "PÂI", "GEM", "SUC")
    val natureRo = listOf("CER", "LAC", "NOR", "SOL", "IAZ", "VÂN", "ZĂP")

    val mediumAnimalsRo = listOf("URSUL", "LUPUL", "CAII", "OILE", "VULPE", "RATÂ", "PEȘTE")
    val mediumFoodRo = listOf("TORT", "OREZ", "PARĂ", "PRUNĂ", "LAPTE", "CARNE")
    val mediumNatureRo = listOf("COPAC", "PLOAI", "ZĂPAD", "STEA", "LUNĂ", "FRUN")

    val hardAnimalsRo = listOf("CANGUR", "ELEFAN", "ȘARPE", "TIGRU", "ZEBRĂ", "MAIMUȚ")
    val hardFoodRo = listOf("BANANĂ", "PORTOC", "CIREAS", "PEPENE", "MORCOV")
    val hardNatureRo = listOf("CURCUB", "PĂMÂNT", "FLOARE", "FRUNZĂ", "JUNGLĂ")

    // Legacy English-only lists (for backwards compatibility)
    val animals = animalsEn
    val colors = colorsEn
    val food = foodEn
    val nature = natureEn
    val mediumAnimals = mediumAnimalsEn
    val mediumFood = mediumFoodEn
    val mediumNature = mediumNatureEn
    val hardAnimals = hardAnimalsEn
    val hardFood = hardFoodEn
    val hardNature = hardNatureEn

    fun getWordsForLevel(level: Int, isRomanian: Boolean = false): List<String> {
        return if (isRomanian) {
            when (level) {
                1 -> (animalsRo + colorsRo + foodRo + natureRo).shuffled()
                2 -> (mediumAnimalsRo + mediumFoodRo + mediumNatureRo).shuffled()
                else -> (hardAnimalsRo + hardFoodRo + hardNatureRo).shuffled()
            }
        } else {
            when (level) {
                1 -> (animalsEn + colorsEn + foodEn + natureEn).shuffled()
                2 -> (mediumAnimalsEn + mediumFoodEn + mediumNatureEn).shuffled()
                else -> (hardAnimalsEn + hardFoodEn + hardNatureEn).shuffled()
            }
        }
    }
}

/**
 * Puzzle generator
 */
object WordSearchGenerator {

    fun generatePuzzle(level: Int, isRomanian: Boolean = false): Pair<List<List<GridCell>>, List<HiddenWord>> {
        val gridSize = WordSearchConfig.getGridSize(level)
        val wordCount = WordSearchConfig.getWordCount(level)
        val allowedDirections = WordSearchConfig.getAllowedDirections(level)
        val availableWords = WordSearchWords.getWordsForLevel(level, isRomanian)
            .filter { it.length <= gridSize }
            .shuffled()
            .take(wordCount * 2) // Take extra in case some don't fit

        // Initialize grid with empty cells
        val grid = Array(gridSize) { row ->
            Array(gridSize) { col ->
                GridCell(row, col, ' ')
            }
        }

        val placedWords = mutableListOf<HiddenWord>()

        // Try to place words
        for (word in availableWords) {
            if (placedWords.size >= wordCount) break

            val placement = tryPlaceWord(grid, word, allowedDirections, gridSize)
            if (placement != null) {
                // Mark cells as part of word
                placeWordInGrid(grid, word, placement.first, placement.second, placement.third)
                placedWords.add(
                    HiddenWord(
                        word = word,
                        startRow = placement.first,
                        startCol = placement.second,
                        direction = placement.third
                    )
                )
            }
        }

        // Fill remaining cells with random letters
        fillEmptyCells(grid, gridSize, isRomanian)

        // Convert to list of lists
        val gridList = grid.map { row -> row.toList() }

        return Pair(gridList, placedWords)
    }

    private fun tryPlaceWord(
        grid: Array<Array<GridCell>>,
        word: String,
        directions: List<WordDirection>,
        gridSize: Int
    ): Triple<Int, Int, WordDirection>? {
        val shuffledDirections = directions.shuffled()

        for (direction in shuffledDirections) {
            // Try random positions
            repeat(20) {
                val startRow = Random.nextInt(gridSize)
                val startCol = Random.nextInt(gridSize)

                if (canPlaceWord(grid, word, startRow, startCol, direction, gridSize)) {
                    return Triple(startRow, startCol, direction)
                }
            }
        }
        return null
    }

    private fun canPlaceWord(
        grid: Array<Array<GridCell>>,
        word: String,
        startRow: Int,
        startCol: Int,
        direction: WordDirection,
        gridSize: Int
    ): Boolean {
        val (dRow, dCol) = getDirectionDeltas(direction)

        for (i in word.indices) {
            val row = startRow + i * dRow
            val col = startCol + i * dCol

            if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) return false

            val existingLetter = grid[row][col].letter
            if (existingLetter != ' ' && existingLetter != word[i]) return false
        }
        return true
    }

    private fun placeWordInGrid(
        grid: Array<Array<GridCell>>,
        word: String,
        startRow: Int,
        startCol: Int,
        direction: WordDirection
    ) {
        val (dRow, dCol) = getDirectionDeltas(direction)

        for (i in word.indices) {
            val row = startRow + i * dRow
            val col = startCol + i * dCol
            grid[row][col] = grid[row][col].copy(letter = word[i], isPartOfWord = true)
        }
    }

    private fun fillEmptyCells(grid: Array<Array<GridCell>>, gridSize: Int, isRomanian: Boolean = false) {
        // Use appropriate alphabet based on language
        val letters = if (isRomanian) {
            "AĂÂBCDEFGHIÎJKLMNOPRSȘTȚUVWXYZ"
        } else {
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        }
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                if (grid[row][col].letter == ' ') {
                    grid[row][col] = grid[row][col].copy(letter = letters.random())
                }
            }
        }
    }

    private fun getDirectionDeltas(direction: WordDirection): Pair<Int, Int> = when (direction) {
        WordDirection.HORIZONTAL -> Pair(0, 1)
        WordDirection.VERTICAL -> Pair(1, 0)
        WordDirection.DIAGONAL_DOWN -> Pair(1, 1)
        WordDirection.DIAGONAL_UP -> Pair(-1, 1)
    }
}
