package com.kidplayer.app.presentation.games.maze

import kotlin.random.Random

/**
 * Represents a cell in the maze
 */
data class MazeCell(
    val row: Int,
    val col: Int,
    var topWall: Boolean = true,
    var rightWall: Boolean = true,
    var bottomWall: Boolean = true,
    var leftWall: Boolean = true,
    var visited: Boolean = false
)

/**
 * Represents the player position
 */
data class Position(val row: Int, val col: Int)

/**
 * Direction for movement
 */
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * Characters for the maze
 */
object MazeCharacters {
    val players = listOf("🐰", "🐱", "🐶", "🐸", "🐻")
    val goals = listOf("🥕", "🐟", "🦴", "🪰", "🍯")

    fun getPlayerForLevel(level: Int): String = players[(level - 1) % players.size]
    fun getGoalForLevel(level: Int): String = goals[(level - 1) % goals.size]
}

/**
 * Game configuration
 */
object MazeConfig {
    fun getSizeForLevel(level: Int): Int {
        return when {
            level <= 2 -> 5
            level <= 4 -> 6
            level <= 6 -> 7
            else -> 8
        }
    }

    const val MAX_LEVELS = 8
    const val BASE_POINTS = 100
    const val TIME_BONUS_THRESHOLD_SECONDS = 30
    const val TIME_BONUS_POINTS = 50
}

/**
 * The maze grid
 */
class Maze(val size: Int) {
    val cells: Array<Array<MazeCell>> = Array(size) { row ->
        Array(size) { col ->
            MazeCell(row, col)
        }
    }

    val start: Position = Position(0, 0)
    val goal: Position = Position(size - 1, size - 1)

    init {
        generate()
    }

    /**
     * Generate maze using recursive backtracking algorithm
     */
    private fun generate() {
        val stack = mutableListOf<MazeCell>()
        val startCell = cells[0][0]
        startCell.visited = true
        stack.add(startCell)

        while (stack.isNotEmpty()) {
            val current = stack.last()
            val unvisitedNeighbors = getUnvisitedNeighbors(current)

            if (unvisitedNeighbors.isEmpty()) {
                stack.removeLast()
            } else {
                val next = unvisitedNeighbors.random()
                removeWallBetween(current, next)
                next.visited = true
                stack.add(next)
            }
        }

        // Reset visited flags for gameplay
        cells.forEach { row -> row.forEach { it.visited = false } }
    }

    private fun getUnvisitedNeighbors(cell: MazeCell): List<MazeCell> {
        val neighbors = mutableListOf<MazeCell>()
        val (row, col) = cell.row to cell.col

        // Top
        if (row > 0 && !cells[row - 1][col].visited) {
            neighbors.add(cells[row - 1][col])
        }
        // Right
        if (col < size - 1 && !cells[row][col + 1].visited) {
            neighbors.add(cells[row][col + 1])
        }
        // Bottom
        if (row < size - 1 && !cells[row + 1][col].visited) {
            neighbors.add(cells[row + 1][col])
        }
        // Left
        if (col > 0 && !cells[row][col - 1].visited) {
            neighbors.add(cells[row][col - 1])
        }

        return neighbors
    }

    private fun removeWallBetween(a: MazeCell, b: MazeCell) {
        val rowDiff = b.row - a.row
        val colDiff = b.col - a.col

        when {
            rowDiff == -1 -> { // b is above a
                a.topWall = false
                b.bottomWall = false
            }
            rowDiff == 1 -> { // b is below a
                a.bottomWall = false
                b.topWall = false
            }
            colDiff == -1 -> { // b is left of a
                a.leftWall = false
                b.rightWall = false
            }
            colDiff == 1 -> { // b is right of a
                a.rightWall = false
                b.leftWall = false
            }
        }
    }

    /**
     * Check if movement in a direction is valid
     */
    fun canMove(from: Position, direction: Direction): Boolean {
        val cell = cells[from.row][from.col]
        return when (direction) {
            Direction.UP -> !cell.topWall
            Direction.DOWN -> !cell.bottomWall
            Direction.LEFT -> !cell.leftWall
            Direction.RIGHT -> !cell.rightWall
        }
    }

    /**
     * Get new position after moving in a direction
     */
    fun move(from: Position, direction: Direction): Position {
        if (!canMove(from, direction)) return from

        return when (direction) {
            Direction.UP -> Position(from.row - 1, from.col)
            Direction.DOWN -> Position(from.row + 1, from.col)
            Direction.LEFT -> Position(from.row, from.col - 1)
            Direction.RIGHT -> Position(from.row, from.col + 1)
        }
    }

    /**
     * Check if position is the goal
     */
    fun isGoal(position: Position): Boolean {
        return position == goal
    }
}
