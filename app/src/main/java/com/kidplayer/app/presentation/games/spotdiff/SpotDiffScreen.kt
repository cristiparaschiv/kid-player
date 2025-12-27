package com.kidplayer.app.presentation.games.spotdiff

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun SpotDiffScreen(
    onNavigateBack: () -> Unit,
    viewModel: SpotDiffViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Spot the Difference",
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
            // Round and progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROUND ${uiState.round}/${SpotDiffConfig.TOTAL_ROUNDS}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "FOUND: ${uiState.differencesFound}/${uiState.totalDifferences}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Instruction
            Text(
                text = "FIND THE DIFFERENCES IN THE BOTTOM PICTURE!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Base picture (reference)
            PictureGrid(
                grid = uiState.baseGrid,
                title = "ORIGINAL",
                isClickable = false,
                onCellClick = { _, _ -> },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Difference picture (clickable)
            PictureGrid(
                grid = uiState.diffGrid,
                title = "FIND DIFFERENCES HERE!",
                isClickable = true,
                onCellClick = { row, col ->
                    haptic.performMedium()
                    viewModel.onCellClick(row, col)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // Round complete indicator
            if (uiState.roundComplete) {
                Spacer(modifier = Modifier.height(8.dp))
                RoundCompleteIndicator()
            }
        }
    }
}

@Composable
private fun PictureGrid(
    grid: List<List<PictureCell>>,
    title: String,
    isClickable: Boolean,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (grid.isEmpty()) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isClickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                grid.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        row.forEachIndexed { colIndex, cell ->
                            EmojiCell(
                                cell = cell,
                                isClickable = isClickable,
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
}

@Composable
private fun EmojiCell(
    cell: PictureCell,
    isClickable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (cell.isFound) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cellScale"
    )

    val backgroundColor = when {
        cell.isFound -> Color(0xFF4CAF50).copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when {
        cell.isFound -> Color(0xFF4CAF50)
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .scale(scale)
            .then(
                if (cell.isFound) {
                    Modifier.border(
                        width = 3.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else Modifier
            )
            .then(
                if (isClickable && !cell.isFound) {
                    Modifier.bouncyClickable { onClick() }
                } else Modifier
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cell.emoji,
                fontSize = 28.sp
            )

            // Show checkmark for found differences
            if (cell.isFound) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundCompleteIndicator() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "completeScale"
    )

    Card(
        modifier = Modifier
            .scale(scale)
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ALL FOUND! NEXT ROUND...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
