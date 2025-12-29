package com.kidplayer.app.presentation.games.compare

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
fun CompareScreen(
    onNavigateBack: () -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_compare_name),
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
                totalRounds = CompareConfig.TOTAL_ROUNDS,
                correctCount = uiState.correctCount
            )

            // Problem display
            uiState.currentProblem?.let { problem ->
                ComparisonDisplay(
                    problem = problem,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question text
                Text(
                    text = if (uiState.showResult) {
                        if (uiState.isCorrect) "CORRECT! ${problem.leftCount} ${problem.correctSymbol} ${problem.rightCount}"
                        else "THE ANSWER IS ${problem.correctSymbol}"
                    } else {
                        "WHICH SIDE HAS MORE?"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = if (uiState.showResult && uiState.isCorrect) Color(0xFF4CAF50)
                    else if (uiState.showResult) Color(0xFFFF9800)
                    else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Comparison buttons
                ComparisonButtons(
                    correctAnswer = problem.correctAnswer,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    onAnswerClick = { answer ->
                        haptic.performMedium()
                        viewModel.selectAnswer(answer)
                    }
                )
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
            Text(text = "⚖️", fontSize = 28.sp)
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

@Composable
private fun ComparisonDisplay(
    problem: CompareProblem,
    selectedAnswer: ComparisonResult?,
    showResult: Boolean,
    isCorrect: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left group
        GroupCard(
            count = problem.leftCount,
            emoji = problem.leftEmoji,
            label = "${problem.leftCount}",
            isHighlighted = showResult && problem.correctAnswer == ComparisonResult.GREATER
        )

        // VS or symbol
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = when {
                    showResult && isCorrect -> Color(0xFF4CAF50)
                    showResult && !isCorrect -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            ),
            modifier = Modifier.size(60.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showResult) problem.correctSymbol else "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (showResult) Color.White
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Right group
        GroupCard(
            count = problem.rightCount,
            emoji = problem.rightEmoji,
            label = "${problem.rightCount}",
            isHighlighted = showResult && problem.correctAnswer == ComparisonResult.LESS
        )
    }
}

@Composable
private fun GroupCard(
    count: Int,
    emoji: String,
    label: String,
    isHighlighted: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(140.dp)
            .then(
                if (isHighlighted) Modifier.border(3.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display emojis in grid
            EmojiGrid(count = count, emoji = emoji)

            // Count label
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) Color(0xFF4CAF50) else Color(0xFF333333)
            )
        }
    }
}

@Composable
private fun EmojiGrid(
    count: Int,
    emoji: String
) {
    val itemsPerRow = when {
        count <= 3 -> count
        count <= 6 -> 3
        else -> 4
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        var remaining = count
        while (remaining > 0) {
            val itemsInRow = minOf(itemsPerRow, remaining)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(itemsInRow) {
                    Text(
                        text = emoji,
                        fontSize = if (count <= 5) 28.sp else 22.sp
                    )
                }
            }
            remaining -= itemsInRow
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComparisonButtons(
    correctAnswer: ComparisonResult,
    selectedAnswer: ComparisonResult?,
    showResult: Boolean,
    onAnswerClick: (ComparisonResult) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            ComparisonResult.GREATER to "<",  // Left is greater (alligator eats left)
            ComparisonResult.EQUAL to "=",
            ComparisonResult.LESS to ">"      // Right is greater (alligator eats right)
        ).forEach { (result, symbol) ->
            val displaySymbol = when (result) {
                ComparisonResult.GREATER -> ">"
                ComparisonResult.LESS -> "<"
                ComparisonResult.EQUAL -> "="
            }

            val isSelected = selectedAnswer == result
            val isCorrect = result == correctAnswer

            val scale by animateFloatAsState(
                targetValue = when {
                    showResult && isCorrect -> 1.2f
                    showResult && isSelected && !isCorrect -> 0.85f
                    else -> 1f
                },
                animationSpec = spring(dampingRatio = 0.6f),
                label = "buttonScale"
            )

            val backgroundColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFE53935)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primaryContainer
            }

            Card(
                onClick = { if (!showResult) onAnswerClick(result) },
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 4.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displaySymbol,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            showResult && (isCorrect || isSelected) -> Color.White
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }
    }
}
