package com.kidplayer.app.presentation.games.match3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Match3ViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(Match3UiState())
    val uiState: StateFlow<Match3UiState> = _uiState.asStateFlow()

    private var cascadeJob: Job? = null
    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    /**
     * Start a new game
     */
    fun startNewGame() {
        cascadeJob?.cancel()
        gameStartTime = System.currentTimeMillis()

        val board = Match3Logic.createBoard()

        _uiState.update {
            Match3UiState(
                board = board,
                gameState = GameState.Playing(),
                score = 0,
                movesRemaining = Match3Config.MOVES_PER_GAME,
                selectedPosition = null,
                comboLevel = 0
            )
        }
    }

    /**
     * Handle tile tap
     */
    fun onTileTap(row: Int, col: Int) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.isProcessing) return

        val tappedPosition = Position(row, col)

        val selectedPos = currentState.selectedPosition
        if (selectedPos == null) {
            // First selection
            selectTile(tappedPosition)
        } else if (selectedPos == tappedPosition) {
            // Deselect
            deselectTile()
        } else if (selectedPos.isAdjacentTo(tappedPosition)) {
            // Adjacent tile - attempt swap
            attemptSwap(selectedPos, tappedPosition)
        } else {
            // Non-adjacent - change selection
            selectTile(tappedPosition)
        }
    }

    private fun selectTile(position: Position) {
        val currentState = _uiState.value
        val newBoard = currentState.board.map { row ->
            row.map { tile ->
                tile.copy(isSelected = tile.row == position.row && tile.col == position.col)
            }
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                selectedPosition = position
            )
        }
    }

    private fun deselectTile() {
        val currentState = _uiState.value
        val newBoard = currentState.board.map { row ->
            row.map { tile -> tile.copy(isSelected = false) }
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                selectedPosition = null
            )
        }
    }

    private fun attemptSwap(pos1: Position, pos2: Position) {
        val currentState = _uiState.value

        // Mark as processing
        _uiState.update { it.copy(isProcessing = true) }

        // Perform swap
        val swappedBoard = Match3Logic.swapTiles(currentState.board, pos1, pos2)
        val matches = Match3Logic.findAllMatches(swappedBoard)

        if (matches.isEmpty()) {
            // Invalid swap - swap back with animation
            viewModelScope.launch {
                // Show swapped state briefly
                _uiState.update { it.copy(board = swappedBoard, selectedPosition = null) }
                delay(200)
                // Swap back
                _uiState.update {
                    it.copy(
                        board = currentState.board.map { row ->
                            row.map { tile -> tile.copy(isSelected = false) }
                        },
                        isProcessing = false
                    )
                }
            }
        } else {
            // Valid swap - process matches
            val newMovesRemaining = currentState.movesRemaining - 1
            _uiState.update {
                it.copy(
                    board = swappedBoard.map { row ->
                        row.map { tile -> tile.copy(isSelected = false) }
                    },
                    selectedPosition = null,
                    movesRemaining = newMovesRemaining
                )
            }

            // Start cascade processing
            cascadeJob = viewModelScope.launch {
                processCascade(1)
            }
        }
    }

    private suspend fun processCascade(comboLevel: Int) {
        val currentState = _uiState.value
        val matches = Match3Logic.findAllMatches(currentState.board)

        if (matches.isEmpty()) {
            // No more matches - end cascade
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    comboLevel = 0
                )
            }

            // Check for game over
            checkGameOver()
            return
        }

        // Calculate and add score
        val points = Match3Logic.calculateScore(matches, comboLevel)
        val newScore = currentState.score + points

        // Mark matched tiles
        val markedBoard = Match3Logic.markMatches(currentState.board, matches)

        _uiState.update {
            it.copy(
                board = markedBoard,
                score = newScore,
                comboLevel = comboLevel,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Wait for match animation
        delay(300)

        // Apply gravity
        val newBoard = Match3Logic.applyGravity(markedBoard)

        _uiState.update {
            it.copy(board = newBoard)
        }

        // Wait for fall animation
        delay(250)

        // Clear animation flags and continue cascade
        val cleanBoard = newBoard.map { row ->
            row.map { tile -> tile.copy(isAnimating = false) }
        }

        _uiState.update { it.copy(board = cleanBoard) }

        // Check for more matches (cascade)
        processCascade(comboLevel + 1)
    }

    private fun checkGameOver() {
        val currentState = _uiState.value

        // Check if no moves remaining
        if (currentState.movesRemaining <= 0) {
            handleGameComplete()
            return
        }

        // Check if no valid moves on board
        if (!Match3Logic.hasValidMoves(currentState.board)) {
            // Shuffle the board
            viewModelScope.launch {
                shuffleBoard()
            }
        }
    }

    private suspend fun shuffleBoard() {
        _uiState.update { it.copy(isProcessing = true) }
        delay(300)

        var newBoard = Match3Logic.createBoard()
        // Ensure valid moves exist
        while (!Match3Logic.hasValidMoves(newBoard)) {
            newBoard = Match3Logic.createBoard()
        }

        _uiState.update {
            it.copy(
                board = newBoard,
                isProcessing = false
            )
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        // Calculate stars based on score
        val stars = when {
            currentState.score >= 3000 -> 3
            currentState.score >= 1500 -> 2
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

    /**
     * Pause the game
     */
    fun pauseGame() {
        cascadeJob?.cancel()
        _uiState.update { it.copy(gameState = GameState.Paused) }
    }

    /**
     * Resume the game
     */
    fun resumeGame() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(gameState = GameState.Playing(score = currentState.score))
        }
    }

    override fun onCleared() {
        super.onCleared()
        cascadeJob?.cancel()
    }
}

/**
 * UI state for Match-3 game
 */
data class Match3UiState(
    val board: List<List<Tile>> = emptyList(),
    val gameState: GameState = GameState.Loading,
    val score: Int = 0,
    val movesRemaining: Int = Match3Config.MOVES_PER_GAME,
    val selectedPosition: Position? = null,
    val comboLevel: Int = 0,
    val isProcessing: Boolean = false
)
