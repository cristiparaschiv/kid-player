package com.kidplayer.app.presentation.games.lettermatch

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
class LetterMatchViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LetterMatchUiState())
    val uiState: StateFlow<LetterMatchUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        val puzzle = LetterMatchPuzzleGenerator.generatePuzzle(1)

        _uiState.update {
            LetterMatchUiState(
                currentPuzzle = puzzle,
                round = 1,
                score = 0,
                gameState = GameState.Playing(),
                selectedIndex = null,
                showResult = false,
                isCorrect = false
            )
        }
    }

    fun selectAnswer(index: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val puzzle = currentState.currentPuzzle ?: return
        val isCorrect = index == puzzle.correctAnswerIndex

        val newScore = if (isCorrect) {
            currentState.score + LetterMatchConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + LetterMatchConfig.POINTS_WRONG)
        }

        _uiState.update {
            it.copy(
                selectedIndex = index,
                showResult = true,
                isCorrect = isCorrect,
                score = newScore,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(1500)
            nextPuzzle()
        }
    }

    private fun nextPuzzle() {
        val currentState = _uiState.value
        val newRound = currentState.round + 1

        if (newRound > LetterMatchConfig.TOTAL_ROUNDS) {
            handleGameComplete()
            return
        }

        val puzzle = LetterMatchPuzzleGenerator.generatePuzzle(newRound)

        _uiState.update {
            it.copy(
                currentPuzzle = puzzle,
                round = newRound,
                selectedIndex = null,
                showResult = false,
                isCorrect = false
            )
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val maxScore = LetterMatchConfig.TOTAL_ROUNDS * LetterMatchConfig.POINTS_CORRECT
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

data class LetterMatchUiState(
    val currentPuzzle: LetterMatchPuzzle? = null,
    val round: Int = 1,
    val score: Int = 0,
    val gameState: GameState = GameState.Loading,
    val selectedIndex: Int? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false
)
