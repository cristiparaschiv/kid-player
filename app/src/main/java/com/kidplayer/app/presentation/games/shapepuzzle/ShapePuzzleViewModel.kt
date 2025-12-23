package com.kidplayer.app.presentation.games.shapepuzzle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameConfig
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel for Shape Puzzle game
 * Kids drag shapes to match their silhouettes
 */
@HiltViewModel
class ShapePuzzleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ShapePuzzleUiState())
    val uiState: StateFlow<ShapePuzzleUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    /**
     * Start a new game with current difficulty
     */
    fun startNewGame(difficulty: Difficulty = _uiState.value.config.difficulty) {
        val shapeCount = when (difficulty) {
            Difficulty.EASY -> 3
            Difficulty.MEDIUM -> 5
            Difficulty.HARD -> 7
        }

        // Select random shapes for this puzzle
        val selectedTypes = PuzzleShapeType.entries.shuffled().take(shapeCount)

        // Create shapes with shuffled positions for dragging
        val shapes = selectedTypes.mapIndexed { index, type ->
            PuzzleShape(
                id = index,
                type = type,
                color = type.color,
                isPlaced = false
            )
        }

        // Shuffle the order for the draggable shapes area
        val shuffledShapes = shapes.shuffled()

        gameStartTime = System.currentTimeMillis()

        _uiState.update {
            ShapePuzzleUiState(
                shapes = shuffledShapes,
                targetOrder = shapes.map { it.id }, // Original order for targets
                gameState = GameState.Playing(),
                config = it.config.copy(difficulty = difficulty),
                placedCount = 0,
                moves = 0
            )
        }
    }

    /**
     * Called when a shape is dropped on a target
     */
    fun onShapePlaced(shapeId: Int, targetId: Int): Boolean {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return false

        val shape = currentState.shapes.find { it.id == shapeId } ?: return false

        // Check if shape matches the target (same id means same shape type position)
        val isCorrect = shapeId == targetId

        if (isCorrect && !shape.isPlaced) {
            val updatedShapes = currentState.shapes.map {
                if (it.id == shapeId) it.copy(isPlaced = true) else it
            }

            val newPlacedCount = currentState.placedCount + 1
            val newMoves = currentState.moves + 1

            _uiState.update {
                it.copy(
                    shapes = updatedShapes,
                    placedCount = newPlacedCount,
                    moves = newMoves,
                    gameState = GameState.Playing(
                        score = newPlacedCount * 100,
                        moves = newMoves
                    )
                )
            }

            // Check for completion
            if (newPlacedCount == currentState.shapes.size) {
                handleGameComplete()
            }

            return true
        } else {
            // Wrong placement - count as a move
            _uiState.update {
                it.copy(moves = it.moves + 1)
            }
            return false
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime
        val totalShapes = currentState.shapes.size

        // Calculate stars based on accuracy (moves vs shapes)
        val stars = when {
            currentState.moves <= totalShapes -> 3      // Perfect
            currentState.moves <= totalShapes * 1.5 -> 2
            else -> 1
        }

        // Score calculation
        val baseScore = totalShapes * 100
        val efficiencyBonus = ((totalShapes.toFloat() / currentState.moves) * 200).toInt()
        val timeBonus = maxOf(0, 300 - (timeElapsed / 1000).toInt())
        val totalScore = baseScore + efficiencyBonus + timeBonus

        _uiState.update {
            it.copy(
                gameState = GameState.Completed(
                    won = true,
                    score = totalScore,
                    stars = stars,
                    moves = currentState.moves,
                    timeElapsedMs = timeElapsed
                )
            )
        }
    }

    /**
     * Pause the game
     */
    fun pauseGame() {
        _uiState.update {
            it.copy(gameState = GameState.Paused)
        }
    }

    /**
     * Resume the game
     */
    fun resumeGame() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                gameState = GameState.Playing(
                    score = currentState.placedCount * 100,
                    moves = currentState.moves
                )
            )
        }
    }

    /**
     * Change difficulty
     */
    fun setDifficulty(difficulty: Difficulty) {
        startNewGame(difficulty)
    }
}

/**
 * UI state for Shape Puzzle
 */
data class ShapePuzzleUiState(
    val shapes: List<PuzzleShape> = emptyList(),
    val targetOrder: List<Int> = emptyList(),
    val gameState: GameState = GameState.Loading,
    val config: GameConfig = GameConfig(),
    val placedCount: Int = 0,
    val moves: Int = 0
)

/**
 * A puzzle shape
 */
data class PuzzleShape(
    val id: Int,
    val type: PuzzleShapeType,
    val color: Color,
    val isPlaced: Boolean = false
)

/**
 * Types of shapes with their colors
 */
enum class PuzzleShapeType(val color: Color, val displayName: String) {
    CIRCLE(Color(0xFF2196F3), "Circle"),
    SQUARE(Color(0xFF4CAF50), "Square"),
    TRIANGLE(Color(0xFFFF9800), "Triangle"),
    STAR(Color(0xFFFFD700), "Star"),
    HEART(Color(0xFFE91E63), "Heart"),
    DIAMOND(Color(0xFF9C27B0), "Diamond"),
    HEXAGON(Color(0xFF00BCD4), "Hexagon"),
    PENTAGON(Color(0xFF8BC34A), "Pentagon"),
    OVAL(Color(0xFFFF5722), "Oval"),
    CRESCENT(Color(0xFF3F51B5), "Moon")
}
