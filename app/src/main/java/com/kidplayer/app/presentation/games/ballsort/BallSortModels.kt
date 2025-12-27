package com.kidplayer.app.presentation.games.ballsort

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Ball colors for the sorting game
 */
object BallColors {
    val colors = listOf(
        Color(0xFFE53935) to "Red",      // Red
        Color(0xFF2196F3) to "Blue",     // Blue
        Color(0xFF4CAF50) to "Green",    // Green
        Color(0xFFFFEB3B) to "Yellow",   // Yellow
        Color(0xFF9C27B0) to "Purple",   // Purple
        Color(0xFFFF9800) to "Orange",   // Orange
        Color(0xFFE91E63) to "Pink",     // Pink
        Color(0xFF00BCD4) to "Cyan"      // Cyan
    )

    fun getColors(count: Int): List<Pair<Color, String>> = colors.take(count)
}

/**
 * A tube that can hold balls
 * Uses immutable List for proper Compose state updates
 */
data class Tube(
    val id: Int,
    val balls: List<Color> = emptyList(),
    val capacity: Int = 4
) {
    val isEmpty: Boolean get() = balls.isEmpty()
    val isFull: Boolean get() = balls.size >= capacity
    val topBall: Color? get() = balls.lastOrNull()
    val availableSpace: Int get() = capacity - balls.size

    /**
     * Count how many consecutive same-colored balls are on top
     */
    fun topBallCount(): Int {
        if (isEmpty) return 0
        val topColor = topBall ?: return 0
        var count = 0
        for (i in balls.indices.reversed()) {
            if (balls[i] == topColor) count++ else break
        }
        return count
    }

    /**
     * Check if this tube can accept a certain number of balls of a given color
     */
    fun canAcceptBalls(ball: Color, count: Int): Boolean {
        if (availableSpace < count) return false
        if (isEmpty) return true
        return topBall == ball
    }

    fun isSorted(): Boolean {
        if (isEmpty) return true
        if (balls.size != capacity) return false
        return balls.all { it == balls.first() }
    }

    /**
     * Create a new tube with balls added
     */
    fun withBallsAdded(newBalls: List<Color>): Tube {
        return copy(balls = balls + newBalls)
    }

    /**
     * Create a new tube with top balls removed
     */
    fun withTopBallsRemoved(count: Int): Tube {
        return copy(balls = balls.dropLast(count))
    }
}

/**
 * Game configuration
 */
object BallSortConfig {
    const val BALLS_PER_COLOR = 4
    const val TOTAL_LEVELS = 8

    // Level configurations: (number of colors, extra empty tubes)
    // Always need at least 2 empty tubes to ensure puzzle is solvable
    fun getLevelConfig(level: Int): Pair<Int, Int> = when (level) {
        1 -> 2 to 2   // 2 colors, 2 empty tubes (4 tubes total)
        2 -> 3 to 2   // 3 colors, 2 empty tubes (5 tubes total)
        3 -> 3 to 2   // 3 colors, 2 empty tubes (5 tubes total)
        4 -> 4 to 2   // 4 colors, 2 empty tubes (6 tubes total)
        5 -> 4 to 2   // 4 colors, 2 empty tubes (6 tubes total)
        6 -> 5 to 2   // 5 colors, 2 empty tubes (7 tubes total)
        7 -> 5 to 2   // 5 colors, 2 empty tubes (7 tubes total)
        else -> 6 to 2 // 6 colors, 2 empty tubes (8 tubes total)
    }

    const val POINTS_PER_LEVEL = 100
    const val BONUS_FEW_MOVES = 50
}

/**
 * A ball sort puzzle
 */
data class BallSortPuzzle(
    val tubes: List<Tube>,
    val colorCount: Int
) {
    fun isSolved(): Boolean {
        return tubes.all { it.isEmpty || it.isSorted() }
    }

    /**
     * Check if we can move a ball from one tube to another
     */
    fun canMove(fromIndex: Int, toIndex: Int): Boolean {
        if (fromIndex == toIndex) return false
        val fromTube = tubes.getOrNull(fromIndex) ?: return false
        val toTube = tubes.getOrNull(toIndex) ?: return false

        if (fromTube.isEmpty) return false
        val topColor = fromTube.topBall ?: return false

        // Check if destination can accept this ball
        if (toTube.isFull) return false
        if (!toTube.isEmpty && toTube.topBall != topColor) return false

        return true
    }

    /**
     * Move ONE ball from one tube to another
     */
    fun move(fromIndex: Int, toIndex: Int): BallSortPuzzle {
        if (!canMove(fromIndex, toIndex)) return this

        // Get the ball being moved
        val topColor = tubes[fromIndex].topBall!!

        // Create new tubes list with the move applied
        val newTubes = tubes.mapIndexed { index, tube ->
            when (index) {
                fromIndex -> tube.withTopBallsRemoved(1)
                toIndex -> tube.withBallsAdded(listOf(topColor))
                else -> tube
            }
        }

        return copy(tubes = newTubes)
    }
}

/**
 * Puzzle generator
 */
object BallSortGenerator {

    fun generatePuzzle(level: Int): BallSortPuzzle {
        val (colorCount, emptyTubes) = BallSortConfig.getLevelConfig(level)
        val colors = BallColors.getColors(colorCount).map { it.first }
        val totalTubes = colorCount + emptyTubes

        // Generate a valid, shuffled puzzle
        var puzzle: BallSortPuzzle
        var attempts = 0

        do {
            // Create all balls (4 of each color) and shuffle them
            val allBalls = colors.flatMap { color ->
                List(BallSortConfig.BALLS_PER_COLOR) { color }
            }.shuffled()

            // Create tubes with shuffled balls
            val tubes = mutableListOf<Tube>()
            var ballIndex = 0

            // Fill tubes with balls (immutable lists)
            for (i in 0 until colorCount) {
                val tubeBalls = allBalls.subList(
                    ballIndex,
                    ballIndex + BallSortConfig.BALLS_PER_COLOR
                ).toList()
                tubes.add(Tube(id = i, balls = tubeBalls))
                ballIndex += BallSortConfig.BALLS_PER_COLOR
            }

            // Add empty tubes
            for (i in colorCount until totalTubes) {
                tubes.add(Tube(id = i, balls = emptyList()))
            }

            puzzle = BallSortPuzzle(tubes.toList(), colorCount)
            attempts++
        } while (puzzle.isSolved() && attempts < 100)

        return puzzle
    }
}
