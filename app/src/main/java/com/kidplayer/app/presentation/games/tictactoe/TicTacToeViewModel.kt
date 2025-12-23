package com.kidplayer.app.presentation.games.tictactoe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameConfig
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * ViewModel for Tic-Tac-Toe game
 * Player is X, AI is O
 */
@HiltViewModel
class TicTacToeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TicTacToeUiState())
    val uiState: StateFlow<TicTacToeUiState> = _uiState.asStateFlow()

    init {
        startNewGame()
    }

    /**
     * Start a new game
     */
    fun startNewGame(difficulty: Difficulty = _uiState.value.config.difficulty) {
        _uiState.update {
            TicTacToeUiState(
                board = List(9) { CellState.EMPTY },
                gameState = GameState.Playing(),
                config = it.config.copy(difficulty = difficulty),
                currentPlayer = Player.X,
                winner = null,
                winningLine = null,
                moves = 0
            )
        }
    }

    /**
     * Handle cell tap
     */
    fun onCellClick(index: Int) {
        val currentState = _uiState.value

        // Ignore if game not playing, cell already taken, or not player's turn
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.board[index] != CellState.EMPTY) return
        if (currentState.currentPlayer != Player.X) return

        // Make player move
        makeMove(index, Player.X)

        // Check for game end
        val stateAfterPlayer = _uiState.value
        if (stateAfterPlayer.gameState is GameState.Completed) return

        // AI's turn
        viewModelScope.launch {
            delay(500) // Brief delay for better UX
            makeAiMove()
        }
    }

    private fun makeMove(index: Int, player: Player) {
        val currentState = _uiState.value
        val newBoard = currentState.board.toMutableList()
        newBoard[index] = if (player == Player.X) CellState.X else CellState.O

        val newMoves = currentState.moves + 1

        _uiState.update {
            it.copy(
                board = newBoard,
                moves = newMoves,
                currentPlayer = if (player == Player.X) Player.O else Player.X,
                gameState = GameState.Playing(moves = newMoves)
            )
        }

        // Check for winner
        checkGameEnd()
    }

    private fun makeAiMove() {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.currentPlayer != Player.O) return

        val move = when (currentState.config.difficulty) {
            Difficulty.EASY -> getEasyMove(currentState.board)
            Difficulty.MEDIUM -> getMediumMove(currentState.board)
            Difficulty.HARD -> getHardMove(currentState.board)
        }

        if (move != null) {
            makeMove(move, Player.O)
        }
    }

    private fun getEasyMove(board: List<CellState>): Int? {
        // Random available cell
        val available = board.indices.filter { board[it] == CellState.EMPTY }
        return available.randomOrNull()
    }

    private fun getMediumMove(board: List<CellState>): Int? {
        // 50% chance to play optimally, 50% random
        return if (Random.nextBoolean()) {
            getHardMove(board)
        } else {
            getEasyMove(board)
        }
    }

    private fun getHardMove(board: List<CellState>): Int? {
        // Try to win
        findWinningMove(board, CellState.O)?.let { return it }

        // Block player from winning
        findWinningMove(board, CellState.X)?.let { return it }

        // Take center if available
        if (board[4] == CellState.EMPTY) return 4

        // Take a corner
        val corners = listOf(0, 2, 6, 8)
        corners.filter { board[it] == CellState.EMPTY }.randomOrNull()?.let { return it }

        // Take any available
        return getEasyMove(board)
    }

    private fun findWinningMove(board: List<CellState>, player: CellState): Int? {
        for (i in board.indices) {
            if (board[i] == CellState.EMPTY) {
                val testBoard = board.toMutableList()
                testBoard[i] = player
                if (checkWinner(testBoard) == player) {
                    return i
                }
            }
        }
        return null
    }

    private fun checkGameEnd() {
        val currentState = _uiState.value
        val winner = checkWinner(currentState.board)
        val winningLine = getWinningLine(currentState.board)

        when {
            winner != null -> {
                val playerWon = winner == CellState.X
                _uiState.update {
                    it.copy(
                        gameState = GameState.Completed(
                            won = playerWon,
                            score = if (playerWon) 100 else 0,
                            stars = if (playerWon) 3 else 1,
                            moves = it.moves
                        ),
                        winner = if (winner == CellState.X) Player.X else Player.O,
                        winningLine = winningLine
                    )
                }
            }
            currentState.board.none { it == CellState.EMPTY } -> {
                // Draw
                _uiState.update {
                    it.copy(
                        gameState = GameState.Completed(
                            won = false,
                            score = 50,
                            stars = 2,
                            moves = it.moves
                        ),
                        winner = null
                    )
                }
            }
        }
    }

    private fun checkWinner(board: List<CellState>): CellState? {
        val lines = listOf(
            // Rows
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            // Columns
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            // Diagonals
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (line in lines) {
            val (a, b, c) = line
            if (board[a] != CellState.EMPTY &&
                board[a] == board[b] &&
                board[b] == board[c]
            ) {
                return board[a]
            }
        }
        return null
    }

    private fun getWinningLine(board: List<CellState>): List<Int>? {
        val lines = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (line in lines) {
            val (a, b, c) = line
            if (board[a] != CellState.EMPTY &&
                board[a] == board[b] &&
                board[b] == board[c]
            ) {
                return line
            }
        }
        return null
    }

    /**
     * Pause the game
     */
    fun pauseGame() {
        _uiState.update {
            it.copy(gameState = GameState.Paused)
        }
    }

    /**
     * Resume the game
     */
    fun resumeGame() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                gameState = GameState.Playing(moves = currentState.moves)
            )
        }
    }

    /**
     * Change difficulty
     */
    fun setDifficulty(difficulty: Difficulty) {
        startNewGame(difficulty)
    }
}

/**
 * UI state for Tic-Tac-Toe
 */
data class TicTacToeUiState(
    val board: List<CellState> = List(9) { CellState.EMPTY },
    val gameState: GameState = GameState.Loading,
    val config: GameConfig = GameConfig(),
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val winningLine: List<Int>? = null,
    val moves: Int = 0
)

/**
 * Cell state
 */
enum class CellState {
    EMPTY, X, O
}

/**
 * Player
 */
enum class Player {
    X, O
}
