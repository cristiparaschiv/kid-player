package com.kidplayer.app.presentation.games.ballsort

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
class BallSortViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BallSortUiState())
    val uiState: StateFlow<BallSortUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        startLevel(1)
    }

    private fun startLevel(level: Int) {
        _uiState.update {
            BallSortUiState(
                level = level,
                score = it.score,
                puzzle = BallSortGenerator.generatePuzzle(level),
                moves = 0,
                selectedTubeIndex = null,
                gameState = GameState.Playing(score = it.score)
            )
        }
    }

    fun selectTube(index: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.levelComplete) return

        val puzzle = currentState.puzzle ?: return

        if (currentState.selectedTubeIndex == null) {
            // First selection - select source tube (must have balls)
            if (!puzzle.tubes[index].isEmpty) {
                _uiState.update { it.copy(selectedTubeIndex = index) }
            }
        } else {
            // Second selection - try to move
            if (currentState.selectedTubeIndex == index) {
                // Deselect if same tube clicked
                _uiState.update { it.copy(selectedTubeIndex = null) }
            } else {
                // Try to move ball
                if (puzzle.canMove(currentState.selectedTubeIndex, index)) {
                    val newPuzzle = puzzle.move(currentState.selectedTubeIndex, index)
                    val newMoves = currentState.moves + 1

                    _uiState.update {
                        it.copy(
                            puzzle = newPuzzle,
                            moves = newMoves,
                            selectedTubeIndex = null
                        )
                    }

                    // Check if solved
                    if (newPuzzle.isSolved()) {
                        handleLevelComplete()
                    }
                } else {
                    // Can't move - try selecting this tube as new source
                    if (!puzzle.tubes[index].isEmpty) {
                        _uiState.update { it.copy(selectedTubeIndex = index) }
                    } else {
                        _uiState.update { it.copy(selectedTubeIndex = null) }
                    }
                }
            }
        }
    }

    private fun handleLevelComplete() {
        val currentState = _uiState.value

        // Calculate score
        val (colorCount, _) = BallSortConfig.getLevelConfig(currentState.level)
        val minMoves = colorCount * BallSortConfig.BALLS_PER_COLOR // Rough estimate
        val hasBonus = currentState.moves <= minMoves * 2

        var levelScore = BallSortConfig.POINTS_PER_LEVEL
        if (hasBonus) {
            levelScore += BallSortConfig.BONUS_FEW_MOVES
        }

        val newScore = currentState.score + levelScore

        _uiState.update {
            it.copy(
                score = newScore,
                levelComplete = true,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(1500)

            val nextLevel = currentState.level + 1
            if (nextLevel > BallSortConfig.TOTAL_LEVELS) {
                handleGameComplete()
            } else {
                startLevel(nextLevel)
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val stars = when {
            currentState.score >= BallSortConfig.TOTAL_LEVELS * (BallSortConfig.POINTS_PER_LEVEL + BallSortConfig.BONUS_FEW_MOVES) * 0.8f -> 3
            currentState.score >= BallSortConfig.TOTAL_LEVELS * BallSortConfig.POINTS_PER_LEVEL * 0.7f -> 2
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

data class BallSortUiState(
    val level: Int = 1,
    val score: Int = 0,
    val moves: Int = 0,
    val puzzle: BallSortPuzzle? = null,
    val selectedTubeIndex: Int? = null,
    val levelComplete: Boolean = false,
    val gameState: GameState = GameState.Loading
)
