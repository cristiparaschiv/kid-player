package com.kidplayer.app.presentation.games.colormix

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
class ColorMixViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ColorMixUiState())
    val uiState: StateFlow<ColorMixUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        val puzzle = ColorMixPuzzleGenerator.generatePuzzle()

        _uiState.update {
            ColorMixUiState(
                currentPuzzle = puzzle,
                round = 1,
                score = 0,
                gameState = GameState.Playing(),
                selectedAnswer = null,
                showResult = false,
                isCorrect = false,
                showMixAnimation = false
            )
        }
    }

    fun selectAnswer(answer: SecondaryColor) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val isCorrect = answer == currentState.currentPuzzle?.correctAnswer
        val newScore = if (isCorrect) {
            currentState.score + ColorMixConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + ColorMixConfig.POINTS_WRONG)
        }

        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                showResult = true,
                isCorrect = isCorrect,
                score = newScore,
                showMixAnimation = isCorrect,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(2000)
            nextPuzzle()
        }
    }

    private fun nextPuzzle() {
        val currentState = _uiState.value
        val newRound = currentState.round + 1

        if (newRound > ColorMixConfig.TOTAL_ROUNDS) {
            handleGameComplete()
            return
        }

        val puzzle = ColorMixPuzzleGenerator.generatePuzzle()

        _uiState.update {
            it.copy(
                currentPuzzle = puzzle,
                round = newRound,
                selectedAnswer = null,
                showResult = false,
                isCorrect = false,
                showMixAnimation = false
            )
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val maxScore = ColorMixConfig.TOTAL_ROUNDS * ColorMixConfig.POINTS_CORRECT
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

data class ColorMixUiState(
    val currentPuzzle: ColorMixPuzzle? = null,
    val round: Int = 1,
    val score: Int = 0,
    val gameState: GameState = GameState.Loading,
    val selectedAnswer: SecondaryColor? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val showMixAnimation: Boolean = false
)
