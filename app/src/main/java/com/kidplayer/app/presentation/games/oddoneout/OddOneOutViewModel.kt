package com.kidplayer.app.presentation.games.oddoneout

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
class OddOneOutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OddOneOutUiState())
    val uiState: StateFlow<OddOneOutUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        _uiState.update {
            OddOneOutUiState(
                round = 1,
                score = 0,
                level = 1,
                currentPuzzle = OddOneOutGenerator.generatePuzzle(1),
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun selectItem(index: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val puzzle = currentState.currentPuzzle ?: return
        val isCorrect = index == puzzle.oddItemIndex

        val newScore = if (isCorrect) {
            currentState.score + OddOneOutConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + OddOneOutConfig.POINTS_WRONG)
        }

        val newCorrectCount = if (isCorrect) currentState.correctCount + 1 else currentState.correctCount

        _uiState.update {
            it.copy(
                selectedIndex = index,
                showResult = true,
                isCorrect = isCorrect,
                score = newScore,
                correctCount = newCorrectCount,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(1800)
            nextPuzzle()
        }
    }

    private fun nextPuzzle() {
        val currentState = _uiState.value
        val nextRound = currentState.round + 1

        if (nextRound > OddOneOutConfig.TOTAL_ROUNDS) {
            handleGameComplete()
        } else {
            val newLevel = (nextRound - 1) / 3 + 1

            _uiState.update {
                it.copy(
                    round = nextRound,
                    level = minOf(newLevel, 3),
                    currentPuzzle = OddOneOutGenerator.generatePuzzle(minOf(newLevel, 3)),
                    selectedIndex = null,
                    showResult = false,
                    isCorrect = false
                )
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val percentage = currentState.correctCount.toFloat() / OddOneOutConfig.TOTAL_ROUNDS
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

data class OddOneOutUiState(
    val round: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val correctCount: Int = 0,
    val currentPuzzle: OddOneOutPuzzle? = null,
    val selectedIndex: Int? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val gameState: GameState = GameState.Loading
)
