package com.kidplayer.app.presentation.games.maze

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
class MazeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MazeUiState())
    val uiState: StateFlow<MazeUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0
    private var levelStartTime: Long = 0

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        startLevel(1)
    }

    private fun startLevel(level: Int) {
        levelStartTime = System.currentTimeMillis()
        val size = MazeConfig.getSizeForLevel(level)
        val maze = Maze(size)

        _uiState.update {
            it.copy(
                maze = maze,
                playerPosition = maze.start,
                level = level,
                moves = 0,
                gameState = GameState.Playing(score = it.score),
                levelComplete = false,
                playerEmoji = MazeCharacters.getPlayerForLevel(level),
                goalEmoji = MazeCharacters.getGoalForLevel(level)
            )
        }
    }

    fun move(direction: Direction) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.levelComplete) return

        val maze = currentState.maze ?: return

        if (maze.canMove(currentState.playerPosition, direction)) {
            val newPosition = maze.move(currentState.playerPosition, direction)
            val newMoves = currentState.moves + 1

            _uiState.update {
                it.copy(
                    playerPosition = newPosition,
                    moves = newMoves
                )
            }

            // Check if reached goal
            if (maze.isGoal(newPosition)) {
                handleLevelComplete()
            }
        }
    }

    private fun handleLevelComplete() {
        val currentState = _uiState.value
        val levelTime = (System.currentTimeMillis() - levelStartTime) / 1000

        // Calculate score
        var levelScore = MazeConfig.BASE_POINTS
        if (levelTime < MazeConfig.TIME_BONUS_THRESHOLD_SECONDS) {
            levelScore += MazeConfig.TIME_BONUS_POINTS
        }

        val newScore = currentState.score + levelScore

        _uiState.update {
            it.copy(
                score = newScore,
                levelComplete = true,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Proceed to next level or end game
        viewModelScope.launch {
            delay(1500)

            val nextLevel = currentState.level + 1
            if (nextLevel > MazeConfig.MAX_LEVELS) {
                handleGameComplete()
            } else {
                startLevel(nextLevel)
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        // Stars based on total score
        val maxScore = MazeConfig.MAX_LEVELS * (MazeConfig.BASE_POINTS + MazeConfig.TIME_BONUS_POINTS)
        val percentage = currentState.score.toFloat() / maxScore

        val stars = when {
            percentage >= 0.85f -> 3
            percentage >= 0.65f -> 2
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

data class MazeUiState(
    val maze: Maze? = null,
    val playerPosition: Position = Position(0, 0),
    val level: Int = 1,
    val score: Int = 0,
    val moves: Int = 0,
    val gameState: GameState = GameState.Loading,
    val levelComplete: Boolean = false,
    val playerEmoji: String = "🐰",
    val goalEmoji: String = "🥕"
)
