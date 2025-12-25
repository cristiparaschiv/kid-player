package com.kidplayer.app.presentation.games.match3

import androidx.compose.ui.graphics.Color
import kotlin.math.pow

/**
 * Types of tiles in the Match-3 game
 * Each tile has a distinct color for easy recognition by kids
 */
enum class TileType(val color: Color, val emoji: String) {
    RED(Color(0xFFE53935), "🍎"),      // Apple
    ORANGE(Color(0xFFFF9800), "🍊"),   // Orange
    YELLOW(Color(0xFFFFEB3B), "⭐"),   // Star
    GREEN(Color(0xFF4CAF50), "🍀"),    // Clover
    BLUE(Color(0xFF2196F3), "💎"),     // Diamond
    PURPLE(Color(0xFF9C27B0), "🍇");   // Grape

    companion object {
        fun random(): TileType = entries.random()
    }
}

/**
 * Represents a single tile on the game board
 */
data class Tile(
    val id: Int,
    val type: TileType,
    val row: Int,
    val col: Int,
    val isMatched: Boolean = false,
    val isSelected: Boolean = false,
    val isAnimating: Boolean = false
)

/**
 * Represents a position on the board
 */
data class Position(val row: Int, val col: Int) {
    fun isAdjacentTo(other: Position): Boolean {
        val rowDiff = kotlin.math.abs(row - other.row)
        val colDiff = kotlin.math.abs(col - other.col)
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)
    }
}

/**
 * Represents a match found on the board
 */
data class Match(
    val positions: Set<Position>,
    val type: TileType
) {
    val size: Int get() = positions.size
}

/**
 * Game board configuration
 */
object Match3Config {
    const val BOARD_ROWS = 8
    const val BOARD_COLS = 8
    const val MIN_MATCH_SIZE = 3

    // Scoring
    const val POINTS_PER_TILE = 10
    const val COMBO_MULTIPLIER = 1.5f
    const val MOVES_PER_GAME = 30
}

/**
 * Core game logic for Match-3
 */
object Match3Logic {

    /**
     * Create a new game board with no initial matches
     */
    fun createBoard(): List<List<Tile>> {
        var tileId = 0
        val board = MutableList(Match3Config.BOARD_ROWS) { row ->
            MutableList(Match3Config.BOARD_COLS) { col ->
                Tile(
                    id = tileId++,
                    type = TileType.random(),
                    row = row,
                    col = col
                )
            }
        }

        // Remove any initial matches by replacing tiles
        var hasMatches = true
        while (hasMatches) {
            val matches = findAllMatches(board)
            if (matches.isEmpty()) {
                hasMatches = false
            } else {
                for (match in matches) {
                    for (pos in match.positions) {
                        val currentType = board[pos.row][pos.col].type
                        val newType = TileType.entries
                            .filter { it != currentType }
                            .random()
                        board[pos.row][pos.col] = board[pos.row][pos.col].copy(
                            id = tileId++,
                            type = newType
                        )
                    }
                }
            }
        }

        return board
    }

    /**
     * Swap two tiles and return the new board
     */
    fun swapTiles(
        board: List<List<Tile>>,
        pos1: Position,
        pos2: Position
    ): List<List<Tile>> {
        val newBoard = board.map { row -> row.toMutableList() }.toMutableList()

        val tile1 = newBoard[pos1.row][pos1.col]
        val tile2 = newBoard[pos2.row][pos2.col]

        newBoard[pos1.row][pos1.col] = tile2.copy(row = pos1.row, col = pos1.col)
        newBoard[pos2.row][pos2.col] = tile1.copy(row = pos2.row, col = pos2.col)

        return newBoard
    }

    /**
     * Find all matches on the board
     */
    fun findAllMatches(board: List<List<Tile>>): List<Match> {
        val matches = mutableListOf<Match>()
        val matchedPositions = mutableSetOf<Position>()

        // Find horizontal matches
        for (row in 0 until Match3Config.BOARD_ROWS) {
            var col = 0
            while (col < Match3Config.BOARD_COLS) {
                val type = board[row][col].type
                val positions = mutableSetOf(Position(row, col))

                var nextCol = col + 1
                while (nextCol < Match3Config.BOARD_COLS && board[row][nextCol].type == type) {
                    positions.add(Position(row, nextCol))
                    nextCol++
                }

                if (positions.size >= Match3Config.MIN_MATCH_SIZE) {
                    matches.add(Match(positions, type))
                    matchedPositions.addAll(positions)
                }

                col = nextCol
            }
        }

        // Find vertical matches
        for (col in 0 until Match3Config.BOARD_COLS) {
            var row = 0
            while (row < Match3Config.BOARD_ROWS) {
                val type = board[row][col].type
                val positions = mutableSetOf(Position(row, col))

                var nextRow = row + 1
                while (nextRow < Match3Config.BOARD_ROWS && board[nextRow][col].type == type) {
                    positions.add(Position(nextRow, col))
                    nextRow++
                }

                if (positions.size >= Match3Config.MIN_MATCH_SIZE) {
                    matches.add(Match(positions, type))
                    matchedPositions.addAll(positions)
                }

                row = nextRow
            }
        }

        return matches
    }

    /**
     * Mark matched tiles on the board
     */
    fun markMatches(board: List<List<Tile>>, matches: List<Match>): List<List<Tile>> {
        val matchedPositions = matches.flatMap { it.positions }.toSet()
        return board.map { row ->
            row.map { tile ->
                if (Position(tile.row, tile.col) in matchedPositions) {
                    tile.copy(isMatched = true)
                } else {
                    tile
                }
            }
        }
    }

    /**
     * Apply gravity - tiles fall down to fill empty spaces
     * Returns new board with tiles dropped
     */
    fun applyGravity(board: List<List<Tile>>): List<List<Tile>> {
        var tileId = board.flatten().maxOf { it.id } + 1
        val newBoard = board.map { row -> row.toMutableList() }.toMutableList()

        for (col in 0 until Match3Config.BOARD_COLS) {
            // Collect non-matched tiles from bottom to top
            val remainingTiles = mutableListOf<TileType>()
            for (row in Match3Config.BOARD_ROWS - 1 downTo 0) {
                if (!newBoard[row][col].isMatched) {
                    remainingTiles.add(newBoard[row][col].type)
                }
            }

            // Fill column from bottom
            var tileIndex = 0
            for (row in Match3Config.BOARD_ROWS - 1 downTo 0) {
                if (tileIndex < remainingTiles.size) {
                    // Use existing tile type
                    newBoard[row][col] = Tile(
                        id = tileId++,
                        type = remainingTiles[tileIndex],
                        row = row,
                        col = col
                    )
                    tileIndex++
                } else {
                    // Generate new tile
                    newBoard[row][col] = Tile(
                        id = tileId++,
                        type = TileType.random(),
                        row = row,
                        col = col,
                        isAnimating = true
                    )
                }
            }
        }

        return newBoard
    }

    /**
     * Check if a valid swap exists on the board
     */
    fun hasValidMoves(board: List<List<Tile>>): Boolean {
        for (row in 0 until Match3Config.BOARD_ROWS) {
            for (col in 0 until Match3Config.BOARD_COLS) {
                // Check swap right
                if (col < Match3Config.BOARD_COLS - 1) {
                    val swapped = swapTiles(board, Position(row, col), Position(row, col + 1))
                    if (findAllMatches(swapped).isNotEmpty()) return true
                }
                // Check swap down
                if (row < Match3Config.BOARD_ROWS - 1) {
                    val swapped = swapTiles(board, Position(row, col), Position(row + 1, col))
                    if (findAllMatches(swapped).isNotEmpty()) return true
                }
            }
        }
        return false
    }

    /**
     * Calculate score for matches
     */
    fun calculateScore(matches: List<Match>, comboLevel: Int): Int {
        val baseScore = matches.sumOf { it.size * Match3Config.POINTS_PER_TILE }
        val multiplier = if (comboLevel > 1) {
            Match3Config.COMBO_MULTIPLIER.toDouble().pow(comboLevel - 1)
        } else {
            1.0
        }
        return (baseScore * multiplier).toInt()
    }
}
