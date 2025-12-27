package com.kidplayer.app.presentation.games.wordsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.reward.GameDifficulty
import com.kidplayer.app.domain.reward.RewardManager
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordSearchViewModel @Inject constructor(
    private val rewardManager: RewardManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordSearchUiState())
    val uiState: StateFlow<WordSearchUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        viewModelScope.launch {
            rewardManager.initialize()
        }
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        val (grid, words) = WordSearchGenerator.generatePuzzle(1)

        _uiState.update {
            WordSearchUiState(
                level = 1,
                score = 0,
                grid = grid,
                hiddenWords = words,
                selectedCells = emptyList(),
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return

        val cell = currentState.grid.getOrNull(row)?.getOrNull(col) ?: return

        // Toggle cell selection
        val newSelectedCells = if (currentState.selectedCells.any { it.row == row && it.col == col }) {
            currentState.selectedCells.filter { !(it.row == row && it.col == col) }
        } else {
            currentState.selectedCells + cell
        }

        // Update selection in grid
        val updatedGrid = currentState.grid.map { gridRow ->
            gridRow.map { gridCell ->
                gridCell.copy(
                    isSelected = newSelectedCells.any { it.row == gridCell.row && it.col == gridCell.col }
                )
            }
        }

        _uiState.update {
            it.copy(
                grid = updatedGrid,
                selectedCells = newSelectedCells
            )
        }

        // Check if selection forms a word
        checkForWord(newSelectedCells)
    }

    fun clearSelection() {
        val currentState = _uiState.value

        val updatedGrid = currentState.grid.map { row ->
            row.map { cell -> cell.copy(isSelected = false) }
        }

        _uiState.update {
            it.copy(
                grid = updatedGrid,
                selectedCells = emptyList()
            )
        }
    }

    private fun checkForWord(selectedCells: List<GridCell>) {
        if (selectedCells.size < 2) return

        val currentState = _uiState.value

        // Build word from selected cells (sorted by position)
        val sortedCells = selectedCells.sortedWith(compareBy({ it.row }, { it.col }))
        val selectedWord = sortedCells.map { it.letter }.joinToString("")
        val reversedWord = selectedWord.reversed()

        // Check if this matches any hidden word
        val matchedWord = currentState.hiddenWords.find { hiddenWord ->
            !hiddenWord.found && (hiddenWord.word == selectedWord || hiddenWord.word == reversedWord)
        }

        if (matchedWord != null) {
            // Word found!
            val newScore = currentState.score + WordSearchConfig.POINTS_PER_WORD
            val newWordsFound = currentState.wordsFound + 1

            // Update grid to mark cells as found
            val updatedGrid = currentState.grid.map { row ->
                row.map { cell ->
                    if (selectedCells.any { it.row == cell.row && it.col == cell.col }) {
                        cell.copy(isFound = true, isSelected = false)
                    } else {
                        cell.copy(isSelected = false)
                    }
                }
            }

            // Update hidden words
            val updatedWords = currentState.hiddenWords.map { word ->
                if (word.word == matchedWord.word) word.copy(found = true)
                else word
            }

            _uiState.update {
                it.copy(
                    grid = updatedGrid,
                    hiddenWords = updatedWords,
                    selectedCells = emptyList(),
                    score = newScore,
                    wordsFound = newWordsFound,
                    gameState = GameState.Playing(score = newScore)
                )
            }

            // Check if all words found
            if (updatedWords.all { it.found }) {
                handleLevelComplete()
            }
        }
    }

    private fun handleLevelComplete() {
        val currentState = _uiState.value

        if (currentState.level < 3) {
            // Move to next level
            viewModelScope.launch {
                kotlinx.coroutines.delay(1000)
                val nextLevel = currentState.level + 1
                val (grid, words) = WordSearchGenerator.generatePuzzle(nextLevel)

                _uiState.update {
                    it.copy(
                        level = nextLevel,
                        grid = grid,
                        hiddenWords = words,
                        selectedCells = emptyList()
                    )
                }
            }
        } else {
            // Game complete
            handleGameComplete()
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val totalWords = currentState.level * 4 // Approximate
        val percentage = currentState.wordsFound.toFloat() / totalWords
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

data class WordSearchUiState(
    val level: Int = 1,
    val score: Int = 0,
    val wordsFound: Int = 0,
    val grid: List<List<GridCell>> = emptyList(),
    val hiddenWords: List<HiddenWord> = emptyList(),
    val selectedCells: List<GridCell> = emptyList(),
    val gameState: GameState = GameState.Loading
)
