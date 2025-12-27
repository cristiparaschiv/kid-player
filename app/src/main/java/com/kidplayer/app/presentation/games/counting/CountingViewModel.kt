package com.kidplayer.app.presentation.games.counting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.reward.GameDifficulty
import com.kidplayer.app.domain.reward.RewardManager
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
class CountingViewModel @Inject constructor(
    private val rewardManager: RewardManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountingUiState())
    val uiState: StateFlow<CountingUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        viewModelScope.launch {
            rewardManager.initialize()
        }
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        _uiState.update {
            CountingUiState(
                round = 1,
                score = 0,
                level = 1,
                currentChallenge = CountingGenerator.generateChallenge(1),
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun selectAnswer(answer: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val challenge = currentState.currentChallenge ?: return
        val isCorrect = answer == challenge.correctAnswer

        val newScore = if (isCorrect) {
            currentState.score + CountingConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + CountingConfig.POINTS_WRONG)
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

        // Move to next challenge after delay
        viewModelScope.launch {
            delay(1500)
            nextChallenge()
        }
    }

    private fun nextChallenge() {
        val currentState = _uiState.value
        val nextRound = currentState.round + 1

        if (nextRound > CountingConfig.TOTAL_ROUNDS) {
            handleGameComplete()
        } else {
            // Increase level every 3 rounds
            val newLevel = (nextRound - 1) / 3 + 1

            _uiState.update {
                it.copy(
                    round = nextRound,
                    level = minOf(newLevel, 3),
                    currentChallenge = CountingGenerator.generateChallenge(minOf(newLevel, 3)),
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

        val percentage = currentState.correctCount.toFloat() / CountingConfig.TOTAL_ROUNDS
        val stars = when {
            percentage >= 0.9f -> 3
            percentage >= 0.7f -> 2
            else -> 1
        }

        // Award stars based on difficulty
        viewModelScope.launch {
            val difficulty = when (currentState.level) {
                1 -> GameDifficulty.EASY
                2 -> GameDifficulty.MEDIUM
                else -> GameDifficulty.HARD
            }
            rewardManager.awardStarsForCompletion(difficulty, true)
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

data class CountingUiState(
    val round: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val correctCount: Int = 0,
    val currentChallenge: CountingChallenge? = null,
    val selectedAnswer: Int? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val gameState: GameState = GameState.Loading
)
