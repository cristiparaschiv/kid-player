package com.kidplayer.app.presentation.games.crossword

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
class CrosswordViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CrosswordUiState())
    val uiState: StateFlow<CrosswordUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        startPuzzle(1, 0)  // Start with puzzle 1
    }

    private fun startPuzzle(puzzleNumber: Int, currentScore: Int) {
        // Use random puzzle selection based on puzzle number (progressive difficulty)
        val puzzle = CrosswordPuzzles.getRandomPuzzle(puzzleNumber)

        _uiState.update {
            CrosswordUiState(
                puzzleNumber = puzzleNumber,
                score = currentScore,
                puzzle = puzzle,
                selectedCell = null,
                showKeyboard = false,
                puzzleComplete = false,
                gameState = GameState.Playing(score = currentScore)
            )
        }
    }

    fun selectCell(row: Int, col: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.puzzleComplete) return

        val puzzle = currentState.puzzle ?: return
        val cell = puzzle.getCell(row, col) ?: return

        // Can only select cells that are part of words
        if (cell.correctLetter == null) return

        _uiState.update {
            it.copy(
                selectedCell = row to col,
                showKeyboard = true
            )
        }
    }

    fun inputLetter(letter: Char) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val (row, col) = currentState.selectedCell ?: return
        val puzzle = currentState.puzzle ?: return
        val cell = puzzle.getCell(row, col) ?: return

        if (cell.correctLetter == null) return

        val upperLetter = letter.uppercaseChar()
        val newPuzzle = puzzle.withUserLetter(row, col, upperLetter)

        // Check if this letter is correct and award points
        val isCorrect = cell.correctLetter == upperLetter
        val scoreChange = if (isCorrect && cell.userLetter != upperLetter) {
            CrosswordConfig.POINTS_PER_LETTER
        } else {
            0
        }

        val newScore = currentState.score + scoreChange

        _uiState.update {
            it.copy(
                puzzle = newPuzzle,
                score = newScore,
                showKeyboard = false,
                selectedCell = null,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Check if puzzle is complete
        if (newPuzzle.isSolved()) {
            handlePuzzleComplete()
        }
    }

    fun clearCell() {
        val currentState = _uiState.value
        val (row, col) = currentState.selectedCell ?: return
        val puzzle = currentState.puzzle ?: return

        val newPuzzle = puzzle.withUserLetter(row, col, null)

        _uiState.update {
            it.copy(
                puzzle = newPuzzle,
                showKeyboard = false,
                selectedCell = null
            )
        }
    }

    fun hideKeyboard() {
        _uiState.update {
            it.copy(
                showKeyboard = false,
                selectedCell = null
            )
        }
    }

    private fun handlePuzzleComplete() {
        val currentState = _uiState.value

        val newScore = currentState.score + CrosswordConfig.POINTS_PUZZLE_COMPLETE

        _uiState.update {
            it.copy(
                score = newScore,
                puzzleComplete = true,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(2000)

            val nextPuzzleNumber = currentState.puzzleNumber + 1
            if (nextPuzzleNumber > CrosswordConfig.TOTAL_PUZZLES) {
                handleGameComplete()
            } else {
                startPuzzle(nextPuzzleNumber, newScore)
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val maxScore = CrosswordConfig.TOTAL_PUZZLES * CrosswordConfig.POINTS_PUZZLE_COMPLETE +
                CrosswordPuzzles.puzzles.sumOf { it.words.sumOf { w -> w.word.length } } * CrosswordConfig.POINTS_PER_LETTER

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

data class CrosswordUiState(
    val puzzleNumber: Int = 1,
    val score: Int = 0,
    val puzzle: CrosswordPuzzle? = null,
    val selectedCell: Pair<Int, Int>? = null,
    val showKeyboard: Boolean = false,
    val puzzleComplete: Boolean = false,
    val gameState: GameState = GameState.Loading
)
