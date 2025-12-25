package com.kidplayer.app.presentation.games.sudoku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SudokuUiState())
    val uiState: StateFlow<SudokuUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        _uiState.update {
            SudokuUiState(
                puzzleNumber = 1,
                score = 0,
                level = 1,
                currentPuzzle = SudokuGenerator.generatePuzzle(1),
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun selectCell(row: Int, col: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val puzzle = currentState.currentPuzzle ?: return
        val cell = puzzle.getCell(row, col)

        // Can only select empty (non-given) cells
        if (cell.isGiven) return

        _uiState.update {
            it.copy(selectedCell = row to col)
        }
    }

    fun placeEmoji(emojiIndex: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val (row, col) = currentState.selectedCell ?: return
        val puzzle = currentState.currentPuzzle ?: return
        val cell = puzzle.getCell(row, col)

        if (cell.isGiven) return

        // Place the emoji
        val newPuzzle = puzzle.withUserValue(row, col, emojiIndex)

        // Check if correct
        val isCorrect = newPuzzle.getCell(row, col).isCorrect

        val newScore = if (isCorrect) {
            currentState.score + SudokuConfig.POINTS_PER_CELL
        } else {
            currentState.score
        }

        _uiState.update {
            it.copy(
                currentPuzzle = newPuzzle,
                selectedCell = null,
                score = newScore,
                lastPlacementCorrect = isCorrect,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Check if puzzle is complete
        if (newPuzzle.isComplete() && newPuzzle.isSolved()) {
            viewModelScope.launch {
                delay(500)
                handlePuzzleComplete()
            }
        }
    }

    fun clearCell() {
        val currentState = _uiState.value
        val (row, col) = currentState.selectedCell ?: return
        val puzzle = currentState.currentPuzzle ?: return

        val newPuzzle = puzzle.withUserValue(row, col, null)

        _uiState.update {
            it.copy(
                currentPuzzle = newPuzzle,
                selectedCell = null
            )
        }
    }

    private fun handlePuzzleComplete() {
        val currentState = _uiState.value
        val nextPuzzle = currentState.puzzleNumber + 1

        if (nextPuzzle > SudokuConfig.TOTAL_PUZZLES) {
            handleGameComplete()
        } else {
            // Next puzzle, increase level every 2 puzzles
            val newLevel = (nextPuzzle - 1) / 2 + 1

            _uiState.update {
                it.copy(
                    puzzleNumber = nextPuzzle,
                    level = minOf(newLevel, 3),
                    currentPuzzle = SudokuGenerator.generatePuzzle(minOf(newLevel, 3)),
                    selectedCell = null,
                    showPuzzleComplete = true
                )
            }

            viewModelScope.launch {
                delay(1500)
                _uiState.update { it.copy(showPuzzleComplete = false) }
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        // Calculate stars based on score
        val maxScore = SudokuConfig.TOTAL_PUZZLES * SudokuConfig.GRID_SIZE * SudokuConfig.GRID_SIZE * SudokuConfig.POINTS_PER_CELL
        val percentage = currentState.score.toFloat() / maxScore

        val stars = when {
            percentage >= 0.9f -> 3
            percentage >= 0.7f -> 2
            else -> 1
        }

        _uiState.update {
            it.copy(
                gameState = GameState.Completed(
                    won = true,
                    score = currentState.score,
                    stars = stars,
                    timeElapsedMs = timeElapsed
                )
            )
        }
    }

    fun pauseGame() {
        _uiState.update { it.copy(gameState = GameState.Paused) }
    }

    fun resumeGame() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(gameState = GameState.Playing(score = currentState.score))
        }
    }
}

data class SudokuUiState(
    val puzzleNumber: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val currentPuzzle: SudokuPuzzle? = null,
    val selectedCell: Pair<Int, Int>? = null,
    val lastPlacementCorrect: Boolean? = null,
    val showPuzzleComplete: Boolean = false,
    val gameState: GameState = GameState.Loading
)
