package com.kidplayer.app.presentation.games.puzzle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kidplayer.app.R

/**
 * Puzzle image categories
 */
enum class PuzzleCategory(val displayName: String) {
    ANIMALS("Animals"),
    CARS("Cars"),
    SPACE("Space");

    companion object {
        fun fromId(id: String): PuzzleCategory? = entries.find { it.name == id }
    }
}

/**
 * Available puzzle images grouped by category
 * Add new images here and they'll appear in the puzzle selection
 */
enum class PuzzleImage(
    @DrawableRes val resourceId: Int,
    val displayName: String,
    val category: PuzzleCategory
) {
    // Animals
    ANIMALS_1(R.drawable.puzzle_animals1, "Animals 1", PuzzleCategory.ANIMALS),
    ANIMALS_2(R.drawable.puzzle_animals2, "Animals 2", PuzzleCategory.ANIMALS),
    ANIMALS_3(R.drawable.puzzle_animals3, "Animals 3", PuzzleCategory.ANIMALS),

    // Cars
    CARS_1(R.drawable.puzzle_cars1, "Cars 1", PuzzleCategory.CARS),
    CARS_2(R.drawable.puzzle_cars2, "Cars 2", PuzzleCategory.CARS),

    // Space
    SPACE_1(R.drawable.puzzle_space1, "Space 1", PuzzleCategory.SPACE);

    companion object {
        fun fromId(id: String): PuzzleImage? = entries.find { it.name == id }

        /**
         * Get all images for a specific category
         */
        fun forCategory(category: PuzzleCategory): List<PuzzleImage> =
            entries.filter { it.category == category }

        /**
         * Get the first image of a category (default selection)
         */
        fun defaultForCategory(category: PuzzleCategory): PuzzleImage =
            forCategory(category).first()
    }
}

/**
 * A piece of the puzzle
 */
data class PuzzlePiece(
    val id: Int,                    // Unique identifier (correct position)
    val currentPosition: Int,       // Current position in grid
    val bitmap: ImageBitmap,        // The image slice for this piece
    val isEmpty: Boolean = false    // For sliding puzzle - the empty slot
)

/**
 * Utility class for loading and slicing puzzle images
 */
object PuzzleImageLoader {

    /**
     * Load a puzzle image and slice it into pieces
     * @param context Android context
     * @param puzzleImage The puzzle image to load
     * @param gridSize Number of rows/columns (3 for 3x3, 4 for 4x4, etc.)
     * @return List of puzzle pieces with their bitmaps
     */
    fun loadAndSlice(
        context: Context,
        puzzleImage: PuzzleImage,
        gridSize: Int
    ): List<PuzzlePiece> {
        // Load the full bitmap
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val fullBitmap = BitmapFactory.decodeResource(
            context.resources,
            puzzleImage.resourceId,
            options
        )

        // Calculate piece dimensions
        val pieceWidth = fullBitmap.width / gridSize
        val pieceHeight = fullBitmap.height / gridSize

        val pieces = mutableListOf<PuzzlePiece>()

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val id = row * gridSize + col
                val x = col * pieceWidth
                val y = row * pieceHeight

                // Extract the piece bitmap
                val pieceBitmap = Bitmap.createBitmap(
                    fullBitmap,
                    x, y,
                    pieceWidth, pieceHeight
                )

                pieces.add(
                    PuzzlePiece(
                        id = id,
                        currentPosition = id,
                        bitmap = pieceBitmap.asImageBitmap()
                    )
                )
            }
        }

        // Clean up the full bitmap
        fullBitmap.recycle()

        return pieces
    }

    /**
     * Load the full image as ImageBitmap (for preview)
     */
    fun loadFullImage(context: Context, puzzleImage: PuzzleImage): ImageBitmap {
        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            puzzleImage.resourceId
        )
        return bitmap.asImageBitmap()
    }
}

/**
 * Check if the puzzle is solved (all pieces in correct position)
 */
fun List<PuzzlePiece>.isSolved(): Boolean {
    return all { it.id == it.currentPosition }
}

/**
 * Shuffle pieces for a new game
 * For sliding puzzle, ensures the puzzle is solvable
 */
fun List<PuzzlePiece>.shuffleForSlidingPuzzle(gridSize: Int): List<PuzzlePiece> {
    // Create a mutable list of positions
    val positions = indices.toMutableList()

    // Shuffle using valid moves to ensure solvability
    var emptyPos = positions.size - 1
    repeat(gridSize * gridSize * 20) { // Make many random moves
        val validMoves = getValidMoves(emptyPos, gridSize)
        if (validMoves.isNotEmpty()) {
            val newEmptyPos = validMoves.random()
            positions[emptyPos] = positions[newEmptyPos].also {
                positions[newEmptyPos] = positions[emptyPos]
            }
            emptyPos = newEmptyPos
        }
    }

    // Create shuffled pieces
    return mapIndexed { index, piece ->
        val newPosition = positions.indexOf(piece.id)
        piece.copy(
            currentPosition = newPosition,
            isEmpty = piece.id == size - 1 // Last piece is empty
        )
    }.sortedBy { it.currentPosition }
}

/**
 * Get valid moves for sliding puzzle (adjacent to empty)
 */
private fun getValidMoves(emptyPos: Int, gridSize: Int): List<Int> {
    val moves = mutableListOf<Int>()
    val row = emptyPos / gridSize
    val col = emptyPos % gridSize

    if (row > 0) moves.add(emptyPos - gridSize)  // Above
    if (row < gridSize - 1) moves.add(emptyPos + gridSize)  // Below
    if (col > 0) moves.add(emptyPos - 1)  // Left
    if (col < gridSize - 1) moves.add(emptyPos + 1)  // Right

    return moves
}

/**
 * Shuffle pieces for drag & drop puzzle
 */
fun List<PuzzlePiece>.shuffleForDragDrop(): List<PuzzlePiece> {
    val shuffledPositions = indices.shuffled()
    return mapIndexed { index, piece ->
        piece.copy(currentPosition = shuffledPositions[index])
    }.sortedBy { it.currentPosition }
}
