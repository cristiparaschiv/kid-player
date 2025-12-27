package com.kidplayer.app.presentation.games.numberbonds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun NumberBondsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NumberBondsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Number Bonds",
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
                totalRounds = NumberBondsConfig.TOTAL_ROUNDS,
                correctCount = uiState.correctCount
            )

            // Problem display with bond diagram
            uiState.currentProblem?.let { problem ->
                NumberBondDiagram(
                    problem = problem,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question text
                Text(
                    text = if (uiState.showResult) {
                        if (uiState.isCorrect) "CORRECT! ${problem.givenNumber} + ${problem.correctAnswer} = ${problem.targetNumber}"
                        else "${problem.givenNumber} + ${problem.correctAnswer} = ${problem.targetNumber}"
                    } else {
                        "WHAT NUMBER GOES WITH ${problem.givenNumber} TO MAKE ${problem.targetNumber}?"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = if (uiState.showResult && uiState.isCorrect) Color(0xFF4CAF50)
                    else if (uiState.showResult) Color(0xFFFF9800)
                    else MaterialTheme.colorScheme.onSurface
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
            Text(text = "🔗", fontSize = 28.sp)
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
private fun NumberBondDiagram(
    problem: NumberBondsProblem,
    selectedAnswer: Int?,
    showResult: Boolean,
    isCorrect: Boolean
) {
    val (bgColor, accentColor) = NumberBondsVisuals.getColorsForTarget(problem.targetNumber)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(bgColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .size(250.dp),
            contentAlignment = Alignment.Center
        ) {
            // Draw connecting lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val topY = 40f
                val bottomLeftX = size.width * 0.25f
                val bottomRightX = size.width * 0.75f
                val bottomY = size.height - 40f

                // Left line
                drawLine(
                    color = Color(accentColor),
                    start = Offset(centerX, topY + 30),
                    end = Offset(bottomLeftX, bottomY - 30),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )

                // Right line
                drawLine(
                    color = Color(accentColor),
                    start = Offset(centerX, topY + 30),
                    end = Offset(bottomRightX, bottomY - 30),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }

            // Top circle - Target number
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(80.dp)
                    .background(Color(accentColor), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${problem.targetNumber}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Bottom left - Given number
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 20.dp)
                    .size(70.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${problem.givenNumber}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(accentColor)
                )
            }

            // Bottom right - Answer (question mark or answer)
            val answerBgColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && !isCorrect -> Color(0xFFFF9800)
                selectedAnswer != null -> Color(0xFF9E9E9E)
                else -> Color.White
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-20).dp)
                    .size(70.dp)
                    .background(answerBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        showResult -> "${problem.correctAnswer}"
                        selectedAnswer != null -> "$selectedAnswer"
                        else -> "?"
                    },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        showResult || selectedAnswer != null -> Color.White
                        else -> Color(accentColor)
                    }
                )
            }

            // Plus sign in center
            Text(
                text = "+",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(accentColor),
                modifier = Modifier.offset(y = 30.dp)
            )
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
