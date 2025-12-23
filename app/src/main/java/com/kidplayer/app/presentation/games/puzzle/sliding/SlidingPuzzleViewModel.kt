package com.kidplayer.app.presentation.games.puzzle.sliding

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameConfig
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.games.puzzle.PuzzleCategory
import com.kidplayer.app.presentation.games.puzzle.PuzzleImage
import com.kidplayer.app.presentation.games.puzzle.PuzzleImageLoader
import com.kidplayer.app.presentation.games.puzzle.PuzzlePiece
import com.kidplayer.app.presentation.games.puzzle.isSolved
import com.kidplayer.app.presentation.games.puzzle.shuffleForSlidingPuzzle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SlidingPuzzleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SlidingPuzzleUiState())
    val uiState: StateFlow<SlidingPuzzleUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    // Get puzzle image from navigation argument if provided
    private val initialImageId: String? = savedStateHandle["imageId"]

    init {
        val initialImage = initialImageId?.let { PuzzleImage.fromId(it) }
        val initialCategory = initialImage?.category ?: PuzzleCategory.ANIMALS
        val selectedImage = initialImage ?: PuzzleImage.defaultForCategory(initialCategory)

        _uiState.update {
            it.copy(
                selectedCategory = initialCategory,
                selectedImage = selectedImage,
                availableImages = PuzzleImage.forCategory(initialCategory)
            )
        }
    }

    /**
     * Select a puzzle category
     */
    fun selectCategory(category: PuzzleCategory) {
        val imagesInCategory = PuzzleImage.forCategory(category)
        val defaultImage = imagesInCategory.first()

        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedImage = defaultImage,
                availableImages = imagesInCategory,
                gameState = GameState.Ready,
                pieces = emptyList()
            )
        }
    }

    /**
     * Select a puzzle image within current category
     */
    fun selectImage(image: PuzzleImage) {
        _uiState.update {
            it.copy(
                selectedImage = image,
                gameState = GameState.Ready,
                pieces = emptyList()
            )
        }
    }

    /**
     * Start a new game with current settings
     */
    fun startNewGame(difficulty: Difficulty = _uiState.value.config.difficulty) {
        val gridSize = when (difficulty) {
            Difficulty.EASY -> 3
            Difficulty.MEDIUM -> 4
            Difficulty.HARD -> 5
        }

        val currentImage = _uiState.value.selectedImage

        // Load the full preview image
        val previewImage = PuzzleImageLoader.loadFullImage(context, currentImage)

        // Load and slice the image
        val pieces = PuzzleImageLoader.loadAndSlice(context, currentImage, gridSize)

        // Shuffle for sliding puzzle (last piece becomes empty)
        val shuffledPieces = pieces.shuffleForSlidingPuzzle(gridSize)

        gameStartTime = System.currentTimeMillis()

        _uiState.update {
            it.copy(
                pieces = shuffledPieces,
                gridSize = gridSize,
                gameState = GameState.Playing(),
                config = it.config.copy(difficulty = difficulty),
                moves = 0,
                previewImage = previewImage
            )
        }
    }

    /**
     * Handle tile tap - slide if adjacent to empty
     */
    fun onTileTap(position: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val pieces = currentState.pieces.toMutableList()
        val gridSize = currentState.gridSize

        // Find the empty piece position
        val emptyPiece = pieces.find { it.isEmpty } ?: return
        val emptyPosition = emptyPiece.currentPosition

        // Check if tapped tile is adjacent to empty
        if (!isAdjacent(position, emptyPosition, gridSize)) return

        // Find the piece at the tapped position
        val tappedPieceIndex = pieces.indexOfFirst { it.currentPosition == position }
        if (tappedPieceIndex == -1) return

        val tappedPiece = pieces[tappedPieceIndex]
        val emptyPieceIndex = pieces.indexOfFirst { it.isEmpty }

        // Swap positions
        pieces[tappedPieceIndex] = tappedPiece.copy(currentPosition = emptyPosition)
        pieces[emptyPieceIndex] = emptyPiece.copy(currentPosition = position)

        val newMoves = currentState.moves + 1

        _uiState.update {
            it.copy(
                pieces = pieces.sortedBy { p -> p.currentPosition },
                moves = newMoves,
                gameState = GameState.Playing(moves = newMoves)
            )
        }

        // Check for completion (ignore empty piece position)
        val nonEmptyPieces = pieces.filter { !it.isEmpty }
        if (nonEmptyPieces.all { it.id == it.currentPosition }) {
            handleGameComplete()
        }
    }

    private fun isAdjacent(pos1: Int, pos2: Int, gridSize: Int): Boolean {
        val row1 = pos1 / gridSize
        val col1 = pos1 % gridSize
        val row2 = pos2 / gridSize
        val col2 = pos2 % gridSize

        return (row1 == row2 && kotlin.math.abs(col1 - col2) == 1) ||
                (col1 == col2 && kotlin.math.abs(row1 - row2) == 1)
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime
        val gridSize = currentState.gridSize
        val optimalMoves = gridSize * gridSize * 2 // Rough estimate

        // Calculate stars based on moves
        val stars = when {
            currentState.moves <= optimalMoves -> 3
            currentState.moves <= optimalMoves * 1.5 -> 2
            else -> 1
        }

        val score = maxOf(0, 1000 - currentState.moves * 10) + (stars * 100)

        _uiState.update {
            it.copy(
                gameState = GameState.Completed(
                    won = true,
                    score = score,
                    stars = stars,
                    moves = currentState.moves,
                    timeElapsedMs = timeElapsed
                )
            )
        }
    }

    fun pauseGame() {
        _uiState.update { it.copy(gameState = GameState.Paused) }
    }

    fun resumeGame() {
        val moves = _uiState.value.moves
        _uiState.update { it.copy(gameState = GameState.Playing(moves = moves)) }
    }

    fun setDifficulty(difficulty: Difficulty) {
        startNewGame(difficulty)
    }
}

data class SlidingPuzzleUiState(
    val selectedCategory: PuzzleCategory = PuzzleCategory.ANIMALS,
    val selectedImage: PuzzleImage = PuzzleImage.ANIMALS_1,
    val availableImages: List<PuzzleImage> = PuzzleImage.forCategory(PuzzleCategory.ANIMALS),
    val pieces: List<PuzzlePiece> = emptyList(),
    val gridSize: Int = 3,
    val gameState: GameState = GameState.Ready,
    val config: GameConfig = GameConfig(),
    val moves: Int = 0,
    val previewImage: ImageBitmap? = null
)
