package com.kidplayer.app.presentation.games.memory

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.key
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.games.memory.components.MemoryCard

/**
 * Memory Match Game Screen
 * Displays a grid of cards that can be flipped to find matching pairs
 */
@Composable
fun MemoryGameScreen(
    onNavigateBack: () -> Unit,
    viewModel: MemoryGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GameScaffold(
        gameName = "Memory Match",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Difficulty selector (only show when game is playing)
            if (uiState.gameState is GameState.Playing || uiState.gameState == GameState.Ready) {
                DifficultySelector(
                    currentDifficulty = uiState.config.difficulty,
                    onDifficultyChange = { viewModel.setDifficulty(it) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Game stats
            GameStats(
                moves = uiState.moves,
                matchedPairs = uiState.matchedPairs,
                totalPairs = uiState.cards.size / 2,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Card grid
            CardGrid(
                cards = uiState.cards,
                difficulty = uiState.config.difficulty,
                onCardClick = { index -> viewModel.onCardClick(index) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
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
                            Difficulty.EASY -> "Easy (6)"
                            Difficulty.MEDIUM -> "Medium (12)"
                            Difficulty.HARD -> "Hard (16)"
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
private fun GameStats(
    moves: Int,
    matchedPairs: Int,
    totalPairs: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Moves", value = moves.toString())
        StatItem(label = "Matched", value = "$matchedPairs / $totalPairs")
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CardGrid(
    cards: List<MemoryCardState>,
    difficulty: Difficulty,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine grid dimensions based on difficulty
    val columns = when (difficulty) {
        Difficulty.EASY -> 3    // 2 rows x 3 columns = 6 cards
        Difficulty.MEDIUM -> 4  // 3 rows x 4 columns = 12 cards
        Difficulty.HARD -> 4    // 4 rows x 4 columns = 16 cards
    }
    val rows = when (difficulty) {
        Difficulty.EASY -> 2
        Difficulty.MEDIUM -> 3
        Difficulty.HARD -> 4
    }

    val spacing = 8.dp

    // Fixed grid that fits all cards on screen
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                for (colIndex in 0 until columns) {
                    val cardIndex = rowIndex * columns + colIndex
                    if (cardIndex < cards.size) {
                        val card = cards[cardIndex]
                        // Use key with the grid position to ensure correct index
                        key(cardIndex) {
                            MemoryCard(
                                card = card,
                                onClick = { onCardClick(cardIndex) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}
