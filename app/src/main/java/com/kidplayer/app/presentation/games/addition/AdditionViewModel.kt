package com.kidplayer.app.presentation.games.addition

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
class AdditionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AdditionUiState())
    val uiState: StateFlow<AdditionUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        _uiState.update {
            AdditionUiState(
                round = 1,
                score = 0,
                level = 1,
                currentProblem = AdditionGenerator.generateProblem(1),
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun selectAnswer(answer: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val problem = currentState.currentProblem ?: return
        val isCorrect = answer == problem.correctAnswer

        val newScore = if (isCorrect) {
            currentState.score + AdditionConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + AdditionConfig.POINTS_WRONG)
        }

        val newCorrectCount = if (isCorrect) currentState.correctCount + 1 else currentState.correctCount

        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                showResult = true,
                isCorrect = isCorrect,
                score = newScore,
                correctCount = newCorrectCount,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Move to next problem after delay
        viewModelScope.launch {
            delay(1500)
            nextProblem()
        }
    }

    private fun nextProblem() {
        val currentState = _uiState.value
        val nextRound = currentState.round + 1

        if (nextRound > AdditionConfig.TOTAL_ROUNDS) {
            handleGameComplete()
        } else {
            // Increase level every 3 rounds
            val newLevel = (nextRound - 1) / 3 + 1

            _uiState.update {
                it.copy(
                    round = nextRound,
                    level = minOf(newLevel, 3),
                    currentProblem = AdditionGenerator.generateProblem(minOf(newLevel, 3)),
                    selectedAnswer = null,
                    showResult = false,
                    isCorrect = false
                )
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val percentage = currentState.correctCount.toFloat() / AdditionConfig.TOTAL_ROUNDS
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

data class AdditionUiState(
    val round: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val correctCount: Int = 0,
    val currentProblem: AdditionProblem? = null,
    val selectedAnswer: Int? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val gameState: GameState = GameState.Loading
)
