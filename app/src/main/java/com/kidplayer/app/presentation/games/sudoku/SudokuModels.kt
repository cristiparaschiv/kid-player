package com.kidplayer.app.presentation.games.sudoku

import kotlin.random.Random

/**
 * Emoji sets for sudoku
 */
object SudokuEmojis {
    val sets = listOf(
        listOf("🍎", "🍊", "🍋", "🍇"),  // Fruits
        listOf("🐶", "🐱", "🐰", "🐸"),  // Animals
        listOf("⭐", "❤️", "💎", "🌸"),  // Shapes
        listOf("🚗", "✈️", "🚀", "🚲"),  // Vehicles
        listOf("🌞", "🌙", "☁️", "🌈"),  // Weather
        listOf("🎈", "🎁", "🎂", "🎉")   // Party
    )

    fun randomSet(): List<String> = sets.random()
}

/**
 * Game configuration
 */
object SudokuConfig {
    const val GRID_SIZE = 4
    const val BOX_SIZE = 2  // 2x2 boxes
    const val POINTS_PER_CELL = 50
    const val TOTAL_PUZZLES = 5

    // How many cells to hide by level
    fun getCellsToHide(level: Int): Int = when (level) {
        1 -> 4   // Easy - hide 4 cells
        2 -> 6   // Medium - hide 6 cells
        else -> 8 // Hard - hide 8 cells
    }
}

/**
 * A cell in the sudoku grid
 */
data class SudokuCell(
    val row: Int,
    val col: Int,
    val correctValue: Int,  // 0-3 index into emoji set
    val isGiven: Boolean,   // true if pre-filled
    var userValue: Int? = null  // user's answer (null if empty)
) {
    val isEmpty: Boolean get() = !isGiven && userValue == null
    val isCorrect: Boolean get() = isGiven || userValue == correctValue
}

/**
 * A sudoku puzzle
 */
data class SudokuPuzzle(
    val grid: List<List<SudokuCell>>,
    val emojis: List<String>
) {
    fun getCell(row: Int, col: Int): SudokuCell = grid[row][col]

    fun isComplete(): Boolean = grid.flatten().all { !it.isEmpty }

    fun isSolved(): Boolean = grid.flatten().all { it.isCorrect }

    fun getEmptyCellCount(): Int = grid.flatten().count { it.isEmpty }

    fun withUserValue(row: Int, col: Int, value: Int?): SudokuPuzzle {
        val newGrid = grid.map { rowCells ->
            rowCells.map { cell ->
                if (cell.row == row && cell.col == col && !cell.isGiven) {
                    cell.copy(userValue = value)
                } else {
                    cell
                }
            }
        }
        return copy(grid = newGrid)
    }
}

/**
 * Sudoku puzzle generator
 */
object SudokuGenerator {

    fun generatePuzzle(level: Int): SudokuPuzzle {
        val emojis = SudokuEmojis.randomSet()
        val solution = generateSolution()
        val cellsToHide = SudokuConfig.getCellsToHide(level)

        // Create cells
        val grid = (0 until SudokuConfig.GRID_SIZE).map { row ->
            (0 until SudokuConfig.GRID_SIZE).map { col ->
                SudokuCell(
                    row = row,
                    col = col,
                    correctValue = solution[row][col],
                    isGiven = true
                )
            }
        }

        // Hide some cells
        val allPositions = (0 until SudokuConfig.GRID_SIZE).flatMap { row ->
            (0 until SudokuConfig.GRID_SIZE).map { col -> row to col }
        }.shuffled()

        val hiddenPositions = allPositions.take(cellsToHide).toSet()

        val puzzleGrid = grid.map { rowCells ->
            rowCells.map { cell ->
                if (hiddenPositions.contains(cell.row to cell.col)) {
                    cell.copy(isGiven = false)
                } else {
                    cell
                }
            }
        }

        return SudokuPuzzle(grid = puzzleGrid, emojis = emojis)
    }

    private fun generateSolution(): Array<IntArray> {
        val grid = Array(SudokuConfig.GRID_SIZE) { IntArray(SudokuConfig.GRID_SIZE) { -1 } }

        // Fill with a valid sudoku solution using backtracking
        fillGrid(grid, 0, 0)

        return grid
    }

    private fun fillGrid(grid: Array<IntArray>, row: Int, col: Int): Boolean {
        if (row == SudokuConfig.GRID_SIZE) return true

        val nextRow = if (col == SudokuConfig.GRID_SIZE - 1) row + 1 else row
        val nextCol = if (col == SudokuConfig.GRID_SIZE - 1) 0 else col + 1

        val numbers = (0 until SudokuConfig.GRID_SIZE).shuffled()

        for (num in numbers) {
            if (isValidPlacement(grid, row, col, num)) {
                grid[row][col] = num
                if (fillGrid(grid, nextRow, nextCol)) {
                    return true
                }
                grid[row][col] = -1
            }
        }

        return false
    }

    private fun isValidPlacement(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        if (grid[row].contains(num)) return false

        // Check column
        if ((0 until SudokuConfig.GRID_SIZE).any { grid[it][col] == num }) return false

        // Check 2x2 box
        val boxRow = (row / SudokuConfig.BOX_SIZE) * SudokuConfig.BOX_SIZE
        val boxCol = (col / SudokuConfig.BOX_SIZE) * SudokuConfig.BOX_SIZE

        for (r in boxRow until boxRow + SudokuConfig.BOX_SIZE) {
            for (c in boxCol until boxCol + SudokuConfig.BOX_SIZE) {
                if (grid[r][c] == num) return false
            }
        }

        return true
    }
}
