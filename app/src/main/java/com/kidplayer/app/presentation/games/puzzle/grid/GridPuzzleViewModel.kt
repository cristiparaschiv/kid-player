package com.kidplayer.app.presentation.games.puzzle.grid

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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GridPuzzleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GridPuzzleUiState())
    val uiState: StateFlow<GridPuzzleUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

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

        // Shuffle positions
        val shuffledPositions = pieces.indices.shuffled()
        val shuffledPieces = pieces.mapIndexed { index, piece ->
            piece.copy(currentPosition = shuffledPositions[index])
        }.sortedBy { it.currentPosition }

        gameStartTime = System.currentTimeMillis()

        _uiState.update {
            it.copy(
                pieces = shuffledPieces,
                gridSize = gridSize,
                gameState = GameState.Playing(),
                config = it.config.copy(difficulty = difficulty),
                moves = 0,
                selectedPiecePosition = null,
                previewImage = previewImage
            )
        }
    }

    /**
     * Handle piece selection - tap to select, tap another to swap
     */
    fun onPieceTap(position: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val selectedPosition = currentState.selectedPiecePosition

        if (selectedPosition == null) {
            // Select this piece
            _uiState.update { it.copy(selectedPiecePosition = position) }
        } else if (selectedPosition == position) {
            // Deselect
            _uiState.update { it.copy(selectedPiecePosition = null) }
        } else {
            // Swap pieces
            swapPieces(selectedPosition, position)
        }
    }

    private fun swapPieces(pos1: Int, pos2: Int) {
        val currentState = _uiState.value
        val pieces = currentState.pieces.toMutableList()

        val piece1Index = pieces.indexOfFirst { it.currentPosition == pos1 }
        val piece2Index = pieces.indexOfFirst { it.currentPosition == pos2 }

        if (piece1Index == -1 || piece2Index == -1) return

        val piece1 = pieces[piece1Index]
        val piece2 = pieces[piece2Index]

        // Swap positions
        pieces[piece1Index] = piece1.copy(currentPosition = pos2)
        pieces[piece2Index] = piece2.copy(currentPosition = pos1)

        val newMoves = currentState.moves + 1

        _uiState.update {
            it.copy(
                pieces = pieces.sortedBy { p -> p.currentPosition },
                moves = newMoves,
                selectedPiecePosition = null,
                gameState = GameState.Playing(moves = newMoves)
            )
        }

        // Check for completion
        if (pieces.isSolved()) {
            handleGameComplete()
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime
        val gridSize = currentState.gridSize
        val totalPieces = gridSize * gridSize
        val optimalMoves = totalPieces // Best case: each piece moved once

        val stars = when {
            currentState.moves <= optimalMoves -> 3
            currentState.moves <= optimalMoves * 2 -> 2
            else -> 1
        }

        val score = maxOf(0, 1000 - currentState.moves * 5) + (stars * 100)

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

data class GridPuzzleUiState(
    val selectedCategory: PuzzleCategory = PuzzleCategory.ANIMALS,
    val selectedImage: PuzzleImage = PuzzleImage.ANIMALS_1,
    val availableImages: List<PuzzleImage> = PuzzleImage.forCategory(PuzzleCategory.ANIMALS),
    val pieces: List<PuzzlePiece> = emptyList(),
    val gridSize: Int = 3,
    val gameState: GameState = GameState.Ready,
    val config: GameConfig = GameConfig(),
    val moves: Int = 0,
    val selectedPiecePosition: Int? = null,
    val previewImage: ImageBitmap? = null
)
