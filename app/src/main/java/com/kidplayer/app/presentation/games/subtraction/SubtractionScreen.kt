package com.kidplayer.app.presentation.games.subtraction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import kotlinx.coroutines.delay

@Composable
fun SubtractionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubtractionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    // Auto-show crossed out animation after a delay
    LaunchedEffect(uiState.currentProblem) {
        if (uiState.currentProblem != null && !uiState.showCrossedOut) {
            delay(800)
            viewModel.showTakeAway()
        }
    }

    GameScaffold(
        gameName = "Subtraction Safari",
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
                totalRounds = SubtractionConfig.TOTAL_ROUNDS,
                correctCount = uiState.correctCount
            )

            // Problem display
            uiState.currentProblem?.let { problem ->
                SubtractionProblemDisplay(
                    problem = problem,
                    showCrossedOut = uiState.showCrossedOut,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Answer options
                AnswerOptions(
                    options = problem.options,
                    correctAnswer = problem.correctAnswer,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    enabled = uiState.showCrossedOut,
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
            containerColor = Color(0xFFFFF3E0) // Safari orange tint
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🦁", fontSize = 28.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "QUESTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF795548)
                )
                Text(
                    text = "$round/$totalRounds",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF795548)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CORRECT",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF795548)
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
private fun SubtractionProblemDisplay(
    problem: SubtractionProblem,
    showCrossedOut: Boolean,
    showResult: Boolean,
    isCorrect: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Problem text
        Text(
            text = "${problem.startCount} ${problem.emojiName} - ${problem.takeAway} = ?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
        )

        // Visual representation
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF8E1) // Light safari background
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Animals display
                SafariAnimals(
                    totalCount = problem.startCount,
                    takeAwayCount = problem.takeAway,
                    emoji = problem.emoji,
                    showCrossedOut = showCrossedOut
                )

                // Instruction text
                AnimatedVisibility(
                    visible = showCrossedOut && !showResult,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "${problem.takeAway} RAN AWAY! HOW MANY ARE LEFT?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF795548),
                        textAlign = TextAlign.Center
                    )
                }

                // Result
                AnimatedVisibility(
                    visible = showResult,
                    enter = scaleIn() + fadeIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${problem.correctAnswer} LEFT!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                        Text(
                            text = if (isCorrect) "EXCELLENT!" else "THE ANSWER WAS ${problem.correctAnswer}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SafariAnimals(
    totalCount: Int,
    takeAwayCount: Int,
    emoji: String,
    showCrossedOut: Boolean
) {
    // Display animals in rows, with some crossed out
    val remainingCount = totalCount - takeAwayCount

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val itemsPerRow = minOf(5, totalCount)
        val rows = (totalCount + itemsPerRow - 1) / itemsPerRow
        var animalIndex = 0

        repeat(rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val itemsInRow = minOf(itemsPerRow, totalCount - animalIndex)
                repeat(itemsInRow) {
                    val currentIndex = animalIndex
                    val isCrossedOut = showCrossedOut && currentIndex >= remainingCount

                    val alpha by animateFloatAsState(
                        targetValue = if (isCrossedOut) 0.3f else 1f,
                        animationSpec = tween(500),
                        label = "animalAlpha"
                    )

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 40.sp,
                            modifier = Modifier.alpha(alpha)
                        )

                        // Red X for crossed out animals
                        if (isCrossedOut) {
                            Text(
                                text = "❌",
                                fontSize = 28.sp
                            )
                        }
                    }
                    animalIndex++
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerOptions(
    options: List<Int>,
    correctAnswer: Int,
    selectedAnswer: Int?,
    showResult: Boolean,
    enabled: Boolean,
    onAnswerClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selectedAnswer
            val isCorrect = option == correctAnswer

            val scale by animateFloatAsState(
                targetValue = when {
                    showResult && isCorrect -> 1.15f
                    showResult && isSelected && !isCorrect -> 0.9f
                    else -> 1f
                },
                animationSpec = spring(dampingRatio = 0.6f),
                label = "optionScale"
            )

            val backgroundColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFE53935)
                isSelected -> Color(0xFF8D6E63)
                !enabled -> Color(0xFFD7CCC8)
                else -> Color(0xFFBCAAA4)
            }

            Card(
                onClick = { if (!showResult && enabled) onAnswerClick(option) },
                modifier = Modifier
                    .size(72.dp)
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
                        text = "$option",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
