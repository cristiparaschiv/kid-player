package com.kidplayer.app.presentation.games.ballsort

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun BallSortScreen(
    onNavigateBack: () -> Unit,
    viewModel: BallSortViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_ballsort_name),
        gameId = "ballsort",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Level info
                LevelInfo(
                    level = uiState.level,
                    moves = uiState.moves
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Instructions
                Text(
                    text = stringResource(R.string.ballsort_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tubes display
                uiState.puzzle?.let { puzzle ->
                    TubesDisplay(
                        puzzle = puzzle,
                        selectedTubeIndex = uiState.selectedTubeIndex,
                        onTubeClick = { index ->
                            haptic.performLight()
                            viewModel.selectTube(index)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Level complete overlay
            AnimatedVisibility(
                visible = uiState.levelComplete,
                enter = scaleIn() + fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 48.sp
                        )
                        Text(
                            text = stringResource(R.string.game_level_complete, uiState.level),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.game_moves_count, uiState.moves),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelInfo(
    level: Int,
    moves: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.game_level_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$level",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.game_moves_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$moves",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun TubesDisplay(
    puzzle: BallSortPuzzle,
    selectedTubeIndex: Int?,
    onTubeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tubeCount = puzzle.tubes.size

    // Arrange tubes in rows
    val tubesPerRow = when {
        tubeCount <= 4 -> tubeCount
        tubeCount <= 6 -> 3
        else -> 4
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var index = 0
        while (index < tubeCount) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val rowCount = minOf(tubesPerRow, tubeCount - index)
                repeat(rowCount) { col ->
                    val tubeIndex = index + col
                    val tube = puzzle.tubes[tubeIndex]
                    val isSelected = tubeIndex == selectedTubeIndex

                    TubeView(
                        tube = tube,
                        isSelected = isSelected,
                        onClick = { onTubeClick(tubeIndex) }
                    )
                }
            }
            index += tubesPerRow
        }
    }
}

@Composable
private fun TubeView(
    tube: Tube,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "tubeScale"
    )

    val tubeWidth = 64.dp
    val tubeHeight = 200.dp  // Fits 4 balls properly
    val ballSize = 44.dp     // 4 balls × 44dp = 176dp + padding fits in 200dp
    val tubeCorner = 12.dp

    Box(
        modifier = Modifier
            .scale(scale)
            .bouncyClickable(onClick = onClick)
    ) {
        // Tube container
        Box(
            modifier = Modifier
                .width(tubeWidth)
                .height(tubeHeight)
                .clip(RoundedCornerShape(bottomStart = tubeCorner, bottomEnd = tubeCorner))
                .background(
                    if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                )
                .border(
                    width = if (isSelected) 3.dp else 2.dp,
                    color = if (isSelected) Color(0xFF2196F3) else Color(0xFFBDBDBD),
                    shape = RoundedCornerShape(bottomStart = tubeCorner, bottomEnd = tubeCorner)
                )
        ) {
            // Balls inside tube (reversed so bottom ball appears at bottom)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display from top to bottom: last ball (top of tube) first, then down to first ball (bottom)
                tube.balls.asReversed().forEach { ballColor ->
                    Box(
                        modifier = Modifier
                            .size(ballSize)
                            .padding(2.dp)
                            .background(ballColor, CircleShape)
                            .border(2.dp, ballColor.copy(alpha = 0.7f), CircleShape)
                    ) {
                        // Highlight on ball
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.4f),
                                radius = size.width * 0.2f,
                                center = Offset(size.width * 0.35f, size.height * 0.35f)
                            )
                        }
                    }
                }
            }
        }

        // Selection indicator arrow
        if (isSelected) {
            Text(
                text = "▼",
                fontSize = 20.sp,
                color = Color(0xFF2196F3),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp)
            )
        }

        // Sorted indicator
        if (tube.isSorted() && !tube.isEmpty) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(24.dp)
                    .background(Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
