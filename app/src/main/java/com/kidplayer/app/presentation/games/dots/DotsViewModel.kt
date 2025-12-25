package com.kidplayer.app.presentation.games.dots

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
class DotsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DotsUiState())
    val uiState: StateFlow<DotsUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        loadPuzzle(0)
    }

    private fun loadPuzzle(index: Int) {
        val puzzle = DotPuzzles.getByIndex(index)
        val dots = puzzle.dots.map { it.copy(isConnected = false) }

        _uiState.update {
            DotsUiState(
                currentPuzzle = puzzle.copy(dots = dots),
                puzzleIndex = index,
                currentDotNumber = 1,
                connectedDots = emptyList(),
                score = it.score,
                gameState = GameState.Playing(score = it.score),
                puzzleComplete = false,
                showingShape = false
            )
        }
    }

    fun onDotTap(dotNumber: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.puzzleComplete) return

        // Check if this is the next dot in sequence
        if (dotNumber == currentState.currentDotNumber) {
            connectDot(dotNumber)
        }
    }

    private fun connectDot(dotNumber: Int) {
        val currentState = _uiState.value
        val puzzle = currentState.currentPuzzle ?: return

        val updatedDots = puzzle.dots.map { dot ->
            if (dot.number == dotNumber) dot.copy(isConnected = true) else dot
        }

        val newConnectedDots = currentState.connectedDots + dotNumber
        val newScore = currentState.score + DotsConfig.POINTS_PER_DOT
        val isLastDot = dotNumber == puzzle.dots.size

        _uiState.update {
            it.copy(
                currentPuzzle = puzzle.copy(dots = updatedDots),
                currentDotNumber = dotNumber + 1,
                connectedDots = newConnectedDots,
                score = newScore,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Check if puzzle is complete
        if (isLastDot) {
            handlePuzzleComplete()
        }
    }

    private fun handlePuzzleComplete() {
        val currentState = _uiState.value
        val bonusScore = currentState.score + DotsConfig.COMPLETION_BONUS

        _uiState.update {
            it.copy(
                puzzleComplete = true,
                showingShape = true,
                score = bonusScore,
                gameState = GameState.Playing(score = bonusScore)
            )
        }

        viewModelScope.launch {
            delay(2000)

            val nextIndex = currentState.puzzleIndex + 1
            if (nextIndex >= DotsConfig.TOTAL_PUZZLES) {
                handleGameComplete()
            } else {
                loadPuzzle(nextIndex)
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val maxScore = DotsConfig.TOTAL_PUZZLES * (
            DotPuzzles.all.take(DotsConfig.TOTAL_PUZZLES).sumOf { it.dots.size } * DotsConfig.POINTS_PER_DOT +
            DotsConfig.TOTAL_PUZZLES * DotsConfig.COMPLETION_BONUS
        )
        val percentage = currentState.score.toFloat() / maxScore

        val stars = when {
            percentage >= 0.95f -> 3
            percentage >= 0.8f -> 2
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

data class DotsUiState(
    val currentPuzzle: DotPuzzle? = null,
    val puzzleIndex: Int = 0,
    val currentDotNumber: Int = 1,
    val connectedDots: List<Int> = emptyList(),
    val score: Int = 0,
    val gameState: GameState = GameState.Loading,
    val puzzleComplete: Boolean = false,
    val showingShape: Boolean = false
)
