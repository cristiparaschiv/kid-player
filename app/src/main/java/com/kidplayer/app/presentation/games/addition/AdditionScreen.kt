package com.kidplayer.app.presentation.games.addition

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun AdditionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdditionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Addition Adventure",
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
                totalRounds = AdditionConfig.TOTAL_ROUNDS,
                correctCount = uiState.correctCount
            )

            // Problem display
            uiState.currentProblem?.let { problem ->
                AdditionProblemDisplay(
                    problem = problem,
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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "QUESTION",
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
private fun AdditionProblemDisplay(
    problem: AdditionProblem,
    showResult: Boolean,
    isCorrect: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual representation with emojis
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First group
                EmojiGroup(count = problem.num1, emoji = problem.emoji)

                // Plus sign
                Text(
                    text = "+",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )

                // Second group
                EmojiGroup(count = problem.num2, emoji = problem.emoji)

                // Equals sign and question/answer
                Divider(
                    modifier = Modifier.width(200.dp),
                    thickness = 3.dp,
                    color = Color(0xFF333333)
                )

                AnimatedVisibility(
                    visible = showResult,
                    enter = scaleIn() + fadeIn()
                ) {
                    Text(
                        text = "${problem.correctAnswer} ${problem.emojiName}!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                }

                if (!showResult) {
                    Text(
                        text = "= ?",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                }
            }
        }

        // Feedback text
        if (showResult) {
            Text(
                text = if (isCorrect) "GREAT JOB!" else "ALMOST! THE ANSWER IS ${problem.correctAnswer}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }
    }
}

@Composable
private fun EmojiGroup(
    count: Int,
    emoji: String
) {
    // Display emojis in rows of up to 5
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val rows = (count + 4) / 5
        var remaining = count

        repeat(rows) {
            val itemsInRow = minOf(remaining, 5)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(itemsInRow) {
                    Text(
                        text = emoji,
                        fontSize = 36.sp
                    )
                }
            }
            remaining -= itemsInRow
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
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.primaryContainer
            }

            val textColor = when {
                showResult && isCorrect -> Color.White
                showResult && isSelected && !isCorrect -> Color.White
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }

            Card(
                onClick = { if (!showResult) onAnswerClick(option) },
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
                        color = textColor
                    )
                }
            }
        }
    }
}
