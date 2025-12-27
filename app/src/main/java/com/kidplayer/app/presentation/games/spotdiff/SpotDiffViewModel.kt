package com.kidplayer.app.presentation.games.spotdiff

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
class SpotDiffViewModel @Inject constructor(
    private val rewardManager: RewardManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpotDiffUiState())
    val uiState: StateFlow<SpotDiffUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        viewModelScope.launch {
            rewardManager.initialize()
        }
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        generateNewPuzzle(1, 1)
    }

    private fun generateNewPuzzle(round: Int, level: Int) {
        val (baseGrid, diffGrid) = SpotDiffGenerator.generatePuzzle(level)
        val totalDifferences = diffGrid.flatten().count { it.isDifferent }

        _uiState.update {
            SpotDiffUiState(
                round = round,
                level = level,
                score = it.score,
                baseGrid = baseGrid,
                diffGrid = diffGrid,
                totalDifferences = totalDifferences,
                differencesFound = 0,
                gameState = GameState.Playing(score = it.score)
            )
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val cell = currentState.diffGrid.getOrNull(row)?.getOrNull(col) ?: return

        // Check if this is a difference that hasn't been found yet
        if (cell.isDifferent && !cell.isFound) {
            // Found a difference!
            val newScore = currentState.score + SpotDiffConfig.POINTS_PER_DIFFERENCE
            val newDifferencesFound = currentState.differencesFound + 1

            // Update the grid to mark this difference as found
            val updatedDiffGrid = currentState.diffGrid.map { gridRow ->
                gridRow.map { gridCell ->
                    if (gridCell.row == row && gridCell.col == col) {
                        gridCell.copy(isFound = true)
                    } else {
                        gridCell
                    }
                }
            }

            _uiState.update {
                it.copy(
                    diffGrid = updatedDiffGrid,
                    score = newScore,
                    differencesFound = newDifferencesFound,
                    gameState = GameState.Playing(score = newScore)
                )
            }

            // Check if all differences found
            if (newDifferencesFound >= currentState.totalDifferences) {
                handleRoundComplete()
            }
        }
    }

    private fun handleRoundComplete() {
        val currentState = _uiState.value

        // Award bonus for finding all differences
        val newScore = currentState.score + SpotDiffConfig.BONUS_ALL_FOUND

        _uiState.update {
            it.copy(
                score = newScore,
                roundComplete = true,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(1500)

            val nextRound = currentState.round + 1
            if (nextRound > SpotDiffConfig.TOTAL_ROUNDS) {
                handleGameComplete()
            } else {
                // Increase level every 2 rounds
                val newLevel = (nextRound - 1) / 2 + 1
                generateNewPuzzle(nextRound, minOf(newLevel, 3))
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val maxScore = SpotDiffConfig.TOTAL_ROUNDS *
                (SpotDiffConfig.POINTS_PER_DIFFERENCE * 4 + SpotDiffConfig.BONUS_ALL_FOUND)
        val percentage = currentState.score.toFloat() / maxScore
        val stars = when {
            percentage >= 0.9f -> 3
            percentage >= 0.7f -> 2
            else -> 1
        }

        // Award stars based on final level
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

data class SpotDiffUiState(
    val round: Int = 1,
    val level: Int = 1,
    val score: Int = 0,
    val baseGrid: List<List<PictureCell>> = emptyList(),
    val diffGrid: List<List<PictureCell>> = emptyList(),
    val totalDifferences: Int = 0,
    val differencesFound: Int = 0,
    val roundComplete: Boolean = false,
    val gameState: GameState = GameState.Loading
)
