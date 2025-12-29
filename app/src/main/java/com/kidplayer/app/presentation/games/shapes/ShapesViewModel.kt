package com.kidplayer.app.presentation.games.shapes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.local.LanguageManager
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
class ShapesViewModel @Inject constructor(
    private val rewardManager: RewardManager,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShapesUiState())
    val uiState: StateFlow<ShapesUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        viewModelScope.launch {
            rewardManager.initialize()
        }
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        val isRomanian = languageManager.isRomanian()
        _uiState.update {
            ShapesUiState(
                round = 1,
                score = 0,
                level = 1,
                isRomanian = isRomanian,
                currentChallenge = ShapesGenerator.generateChallenge(1),
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun selectAnswer(answer: String) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val challenge = currentState.currentChallenge ?: return
        val isCorrect = answer == challenge.getCorrectAnswer(currentState.isRomanian)

        val newScore = if (isCorrect) {
            currentState.score + ShapesConfig.POINTS_CORRECT
        } else {
            maxOf(0, currentState.score + ShapesConfig.POINTS_WRONG)
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

    fun selectShape(displayShape: DisplayShape) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return
        if (currentState.currentChallenge?.type != ChallengeType.FIND_SHAPE) return

        selectAnswer(displayShape.shape.getDisplayName(currentState.isRomanian))
    }

    private fun nextChallenge() {
        val currentState = _uiState.value
        val nextRound = currentState.round + 1

        if (nextRound > ShapesConfig.TOTAL_ROUNDS) {
            handleGameComplete()
        } else {
            // Increase level every 3 rounds
            val newLevel = (nextRound - 1) / 3 + 1

            _uiState.update {
                it.copy(
                    round = nextRound,
                    level = minOf(newLevel, 3),
                    currentChallenge = ShapesGenerator.generateChallenge(minOf(newLevel, 3)),
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

        val percentage = currentState.correctCount.toFloat() / ShapesConfig.TOTAL_ROUNDS
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

data class ShapesUiState(
    val round: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val correctCount: Int = 0,
    val isRomanian: Boolean = false,
    val currentChallenge: ShapeChallenge? = null,
    val selectedAnswer: String? = null,
    val showResult: Boolean = false,
    val isCorrect: Boolean = false,
    val gameState: GameState = GameState.Loading
)
