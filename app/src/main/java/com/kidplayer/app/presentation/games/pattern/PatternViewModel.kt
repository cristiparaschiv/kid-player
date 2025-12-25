package com.kidplayer.app.presentation.games.pattern

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
class PatternViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PatternUiState())
    val uiState: StateFlow<PatternUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        val puzzle = PatternGenerator.generatePattern(
            length = PatternConfig.INITIAL_PATTERN_LENGTH,
            level = 1
        )

        _uiState.update {
            PatternUiState(
                currentPuzzle = puzzle,
                level = 1,
                round = 1,
                score = 0,
                patternLength = PatternConfig.INITIAL_PATTERN_LENGTH,
                gameState = GameState.Playing(),
                selectedOption = null,
                showResult = false,
                isCorrect = false
            )
        }
    }

    fun selectOption(option: PatternElement) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val isCorrect = option == currentState.currentPuzzle?.correctAnswer
        val newScore = if (isCorrect) {
            currentState.score + PatternConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + PatternConfig.POINTS_WRONG)
        }

        _uiState.update {
            it.copy(
                selectedOption = option,
                showResult = true,
                isCorrect = isCorrect,
                score = newScore,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Move to next puzzle after delay
        viewModelScope.launch {
            delay(1500)
            nextPuzzle(isCorrect)
        }
    }

    private fun nextPuzzle(wasCorrect: Boolean) {
        val currentState = _uiState.value
        var newRound = currentState.round + 1
        var newLevel = currentState.level
        var newLength = currentState.patternLength

        // Check if advancing to next level
        if (newRound > PatternConfig.ROUNDS_PER_LEVEL) {
            newRound = 1
            newLevel++

            // Increase pattern length every 2 levels
            if (newLevel % 2 == 0 && newLength < PatternConfig.MAX_PATTERN_LENGTH) {
                newLength++
            }
        }

        // Check for game completion (e.g., after level 5)
        if (newLevel > 5) {
            handleGameComplete()
            return
        }

        val newPuzzle = PatternGenerator.generatePattern(
            length = newLength,
            level = newLevel
        )

        _uiState.update {
            it.copy(
                currentPuzzle = newPuzzle,
                level = newLevel,
                round = newRound,
                patternLength = newLength,
                selectedOption = null,
                showResult = false,
                isCorrect = false
            )
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        // Calculate stars based on score
        val maxPossibleScore = 5 * PatternConfig.ROUNDS_PER_LEVEL * PatternConfig.POINTS_CORRECT
        val percentage = currentState.score.toFloat() / maxPossibleScore

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

data class PatternUiState(
    val currentPuzzle: PatternPuzzle? = null,
    val level: Int = 1,
    val round: Int = 1,
    val score: Int = 0,
    val patternLength: Int = PatternConfig.INITIAL_PATTERN_LENGTH,
    val gameState: GameState = GameState.Loading,
    val selectedOption: PatternElement? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false
)
