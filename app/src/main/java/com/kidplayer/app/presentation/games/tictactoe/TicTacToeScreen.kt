package com.kidplayer.app.presentation.games.tictactoe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.games.tictactoe.components.BoardCell

/**
 * Tic-Tac-Toe Game Screen
 * Player (X) vs AI (O)
 */
@Composable
fun TicTacToeScreen(
    onNavigateBack: () -> Unit,
    viewModel: TicTacToeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GameScaffold(
        gameName = stringResource(R.string.game_tictactoe_name),
        gameId = "tictactoe",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Difficulty selector
            if (uiState.gameState is GameState.Playing || uiState.gameState == GameState.Ready) {
                DifficultySelector(
                    currentDifficulty = uiState.config.difficulty,
                    onDifficultyChange = { viewModel.setDifficulty(it) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Turn indicator
            TurnIndicator(
                currentPlayer = uiState.currentPlayer,
                gameState = uiState.gameState,
                winner = uiState.winner,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Game board - centered and takes available space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GameBoard(
                    board = uiState.board,
                    winningLine = uiState.winningLine,
                    onCellClick = { index -> viewModel.onCellClick(index) },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize(0.9f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == currentDifficulty
            FilterChip(
                selected = isSelected,
                onClick = { onDifficultyChange(difficulty) },
                label = {
                    Text(
                        text = when (difficulty) {
                            Difficulty.EASY -> "EASY"
                            Difficulty.MEDIUM -> "MEDIUM"
                            Difficulty.HARD -> "HARD"
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun TurnIndicator(
    currentPlayer: Player,
    gameState: GameState,
    winner: Player?,
    modifier: Modifier = Modifier
) {
    val text = when {
        gameState is GameState.Completed && winner != null -> {
            if (winner == Player.X) "YOU WIN!" else "AI WINS!"
        }
        gameState is GameState.Completed -> "IT'S A DRAW!"
        currentPlayer == Player.X -> "YOUR TURN (X)"
        else -> "AI THINKING..."
    }

    val color = when {
        gameState is GameState.Completed && winner == Player.X -> Color(0xFF4CAF50)
        gameState is GameState.Completed && winner == Player.O -> Color(0xFFE91E63)
        gameState is GameState.Completed -> Color(0xFFFF9800)
        currentPlayer == Player.X -> Color(0xFF2196F3)
        else -> Color(0xFFE91E63)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

@Composable
private fun GameBoard(
    board: List<CellState>,
    winningLine: List<Int>?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = 8.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    BoardCell(
                        state = board[index],
                        isWinningCell = winningLine?.contains(index) == true,
                        onClick = { onCellClick(index) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}
