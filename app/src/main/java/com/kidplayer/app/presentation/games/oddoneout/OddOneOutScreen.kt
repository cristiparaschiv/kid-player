package com.kidplayer.app.presentation.games.oddoneout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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

@Composable
fun OddOneOutScreen(
    onNavigateBack: () -> Unit,
    viewModel: OddOneOutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_oddoneout_name),
        gameId = "oddoneout",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Round indicator
            RoundIndicator(
                round = uiState.round,
                totalRounds = OddOneOutConfig.TOTAL_ROUNDS,
                correctCount = uiState.correctCount
            )

            // Instructions
            Text(
                text = if (uiState.showResult) {
                    if (uiState.isCorrect) "CORRECT! GREAT JOB!"
                    else "OOPS! LOOK FOR WHAT DOESN'T BELONG"
                } else {
                    "TAP THE ONE THAT DOESN'T BELONG!"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = when {
                    uiState.showResult && uiState.isCorrect -> Color(0xFF4CAF50)
                    uiState.showResult -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Items grid
            uiState.currentPuzzle?.let { puzzle ->
                ItemsGrid(
                    puzzle = puzzle,
                    selectedIndex = uiState.selectedIndex,
                    showResult = uiState.showResult,
                    onItemClick = { index ->
                        haptic.performMedium()
                        viewModel.selectItem(index)
                    }
                )

                // Explanation when showing result
                AnimatedVisibility(
                    visible = uiState.showResult,
                    enter = scaleIn() + fadeIn()
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "${puzzle.oddItem.emoji} is ${puzzle.oddCategoryName}, not ${puzzle.categoryName}!",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundIndicator(
    round: Int,
    totalRounds: Int,
    correctCount: Int
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🔍", fontSize = 28.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUND",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$round/$totalRounds",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CORRECT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$correctCount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemsGrid(
    puzzle: OddOneOutPuzzle,
    selectedIndex: Int?,
    showResult: Boolean,
    onItemClick: (Int) -> Unit
) {
    // Arrange items in a grid
    val itemCount = puzzle.items.size
    val columns = when {
        itemCount <= 4 -> 2
        else -> 3
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var index = 0
        while (index < itemCount) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(minOf(columns, itemCount - index)) { col ->
                    val currentIndex = index + col
                    val item = puzzle.items[currentIndex]
                    val isSelected = currentIndex == selectedIndex
                    val isOdd = currentIndex == puzzle.oddItemIndex

                    val scale by animateFloatAsState(
                        targetValue = when {
                            showResult && isOdd -> 1.15f
                            showResult && isSelected && !isOdd -> 0.9f
                            else -> 1f
                        },
                        animationSpec = spring(dampingRatio = 0.6f),
                        label = "itemScale"
                    )

                    val backgroundColor = when {
                        showResult && isOdd -> Color(0xFF4CAF50)
                        showResult && isSelected && !isOdd -> Color(0xFFE53935)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> Color.White
                    }

                    val borderColor = when {
                        showResult && isOdd -> Color(0xFF2E7D32)
                        showResult && isSelected && !isOdd -> Color(0xFFB71C1C)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> Color(0xFFE0E0E0)
                    }

                    Card(
                        onClick = { if (!showResult) onItemClick(currentIndex) },
                        modifier = Modifier
                            .size(100.dp)
                            .scale(scale),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected || (showResult && isOdd)) 8.dp else 4.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(3.dp, borderColor, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.emoji,
                                fontSize = 48.sp
                            )

                            // Show checkmark or X on result
                            if (showResult) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(24.dp)
                                        .background(
                                            if (isOdd) Color(0xFF4CAF50) else Color.Transparent,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isOdd) {
                                        Text(
                                            text = "✓",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            index += columns
        }
    }
}
