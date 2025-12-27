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
 * Word lists by category
 */
object WordSearchWords {
    val animals = listOf("CAT", "DOG", "PIG", "COW", "FOX", "BEE", "ANT", "OWL", "BAT", "HEN")
    val colors = listOf("RED", "BLUE", "PINK", "GOLD", "GRAY", "TAN", "LIME")
    val food = listOf("PIE", "HAM", "JAM", "EGG", "NUT", "PEA", "FIG")
    val nature = listOf("SUN", "SKY", "SEA", "DEW", "FOG", "MUD", "ICE")

    val mediumAnimals = listOf("BEAR", "DEER", "FROG", "DUCK", "FISH", "BIRD", "LION", "WOLF")
    val mediumFood = listOf("CAKE", "RICE", "BEAN", "CORN", "PLUM", "PEAR", "LIME")
    val mediumNature = listOf("TREE", "LEAF", "RAIN", "WIND", "SNOW", "MOON", "STAR")

    val hardAnimals = listOf("HORSE", "SNAKE", "TIGER", "MOUSE", "SHEEP", "ZEBRA")
    val hardFood = listOf("APPLE", "GRAPE", "LEMON", "BREAD", "PIZZA", "MELON")
    val hardNature = listOf("CLOUD", "RIVER", "GRASS", "STORM", "BEACH", "STONE")

    fun getWordsForLevel(level: Int): List<String> = when (level) {
        1 -> (animals + colors + food + nature).shuffled()
        2 -> (mediumAnimals + mediumFood + mediumNature).shuffled()
        else -> (hardAnimals + hardFood + hardNature).shuffled()
    }
}

/**
 * Puzzle generator
 */
object WordSearchGenerator {

    fun generatePuzzle(level: Int): Pair<List<List<GridCell>>, List<HiddenWord>> {
        val gridSize = WordSearchConfig.getGridSize(level)
        val wordCount = WordSearchConfig.getWordCount(level)
        val allowedDirections = WordSearchConfig.getAllowedDirections(level)
        val availableWords = WordSearchWords.getWordsForLevel(level)
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
        fillEmptyCells(grid, gridSize)

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

    private fun fillEmptyCells(grid: Array<Array<GridCell>>, gridSize: Int) {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
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
