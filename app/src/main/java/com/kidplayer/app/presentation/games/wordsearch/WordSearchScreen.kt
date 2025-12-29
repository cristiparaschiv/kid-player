package com.kidplayer.app.presentation.games.wordsearch

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun WordSearchScreen(
    onNavigateBack: () -> Unit,
    viewModel: WordSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_wordsearch_name),
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
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Level indicator
            Text(
                text = stringResource(R.string.game_level, uiState.level).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Words to find
            WordList(
                words = uiState.hiddenWords,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid
            WordGrid(
                grid = uiState.grid,
                onCellClick = { row, col ->
                    haptic.performLight()
                    viewModel.onCellClick(row, col)
                },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Clear selection button
            if (uiState.selectedCells.isNotEmpty()) {
                Button(
                    onClick = {
                        haptic.performMedium()
                        viewModel.clearSelection()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.cancel)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.wordsearch_clear_selection).uppercase())
                }
            }
        }
    }
}

@Composable
private fun WordList(
    words: List<HiddenWord>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(words) { word ->
                val scale by animateFloatAsState(
                    targetValue = if (word.found) 0.9f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "wordScale"
                )

                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (word.found) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (word.found) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.scale(scale)
                )
            }
        }
    }
}

@Composable
private fun WordGrid(
    grid: List<List<GridCell>>,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (grid.isEmpty()) return

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            grid.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    row.forEachIndexed { colIndex, cell ->
                        GridCellView(
                            cell = cell,
                            onClick = { onCellClick(rowIndex, colIndex) },
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

@Composable
private fun GridCellView(
    cell: GridCell,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        cell.isFound && cell.isSelected -> Color(0xFF2196F3) // Blue when re-selecting a found cell
        cell.isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        cell.isFound -> Color(0xFF4CAF50).copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        cell.isFound || cell.isSelected -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val scale by animateFloatAsState(
        targetValue = if (cell.isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cellScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .bouncyClickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (cell.isSelected) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cell.letter.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
