package com.kidplayer.app.presentation.games.spotdiff

import kotlin.random.Random

/**
 * A cell in the picture grid
 */
data class PictureCell(
    val row: Int,
    val col: Int,
    val emoji: String,
    val isDifferent: Boolean = false,
    val isFound: Boolean = false
)

/**
 * Game configuration
 */
object SpotDiffConfig {
    // Grid size by level
    fun getGridSize(level: Int): Int = when (level) {
        1 -> 3
        2 -> 4
        else -> 5
    }

    // Number of differences by level
    fun getDifferenceCount(level: Int): Int = when (level) {
        1 -> 2
        2 -> 3
        else -> 4
    }

    const val TOTAL_ROUNDS = 5
    const val POINTS_PER_DIFFERENCE = 50
    const val BONUS_ALL_FOUND = 100
}

/**
 * Emoji themes for the pictures
 */
object SpotDiffThemes {
    val themes = listOf(
        // Animals
        listOf("🐶", "🐱", "🐰", "🐻", "🐸", "🦊", "🐼", "🐨", "🦁", "🐯"),
        // Food
        listOf("🍎", "🍌", "🍇", "🍓", "🍊", "🍕", "🍔", "🍪", "🍩", "🎂"),
        // Nature
        listOf("🌸", "🌻", "🌲", "🌈", "☀️", "🌙", "⭐", "🌊", "🍀", "🌺"),
        // Transport
        listOf("🚗", "🚌", "✈️", "🚂", "🚀", "⛵", "🚁", "🚲", "🛵", "🚕"),
        // Objects
        listOf("⚽", "🎈", "🎁", "📚", "🎨", "🎸", "🎺", "🎯", "🎲", "🧸")
    )

    fun getRandomTheme(): List<String> = themes.random()
}

/**
 * Generator for spot the difference puzzles
 */
object SpotDiffGenerator {

    fun generatePuzzle(level: Int): Pair<List<List<PictureCell>>, List<List<PictureCell>>> {
        val gridSize = SpotDiffConfig.getGridSize(level)
        val differenceCount = SpotDiffConfig.getDifferenceCount(level)
        val theme = SpotDiffThemes.getRandomTheme()

        // Generate base grid
        val baseGrid = Array(gridSize) { row ->
            Array(gridSize) { col ->
                PictureCell(row, col, theme.random())
            }
        }

        // Create copy for difference grid
        val diffGrid = Array(gridSize) { row ->
            Array(gridSize) { col ->
                baseGrid[row][col].copy()
            }
        }

        // Select random positions for differences
        val allPositions = (0 until gridSize * gridSize).toMutableList().shuffled()
        val differencePositions = allPositions.take(differenceCount)

        // Apply differences
        for (pos in differencePositions) {
            val row = pos / gridSize
            val col = pos % gridSize
            val currentEmoji = baseGrid[row][col].emoji
            val newEmoji = theme.filter { it != currentEmoji }.random()
            diffGrid[row][col] = diffGrid[row][col].copy(emoji = newEmoji, isDifferent = true)
        }

        // Convert to lists
        val baseList = baseGrid.map { it.toList() }
        val diffList = diffGrid.map { it.toList() }

        return Pair(baseList, diffList)
    }
}
