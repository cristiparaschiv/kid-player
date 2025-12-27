package com.kidplayer.app.presentation.games.colormix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
fun ColorMixScreen(
    onNavigateBack: () -> Unit,
    viewModel: ColorMixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Color Mix",
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
                totalRounds = ColorMixConfig.TOTAL_ROUNDS
            )

            // Color mixing display
            uiState.currentPuzzle?.let { puzzle ->
                ColorMixingDisplay(
                    color1 = puzzle.color1,
                    color2 = puzzle.color2,
                    resultColor = if (uiState.showMixAnimation) puzzle.correctAnswer else null,
                    showAnimation = uiState.showMixAnimation
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Question text
                Text(
                    text = if (uiState.showResult) {
                        if (uiState.isCorrect) {
                            "${puzzle.color1.displayName.uppercase()} + ${puzzle.color2.displayName.uppercase()} = ${puzzle.correctAnswer.displayName.uppercase()}!"
                        } else {
                            "NOT QUITE! IT MAKES ${puzzle.correctAnswer.displayName.uppercase()}"
                        }
                    } else {
                        "WHAT COLOR DO THESE MAKE?"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = when {
                        !uiState.showResult -> MaterialTheme.colorScheme.onSurface
                        uiState.isCorrect -> Color(0xFF4CAF50)
                        else -> Color(0xFFFF9800)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Answer options
                AnswerOptions(
                    options = puzzle.options,
                    selectedAnswer = uiState.selectedAnswer,
                    correctAnswer = puzzle.correctAnswer,
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
    totalRounds: Int
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🎨",
                fontSize = 24.sp
            )
            Text(
                text = "ROUND $round OF $totalRounds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ColorMixingDisplay(
    color1: PrimaryColor,
    color2: PrimaryColor,
    resultColor: SecondaryColor?,
    showAnimation: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleAnimation")

    // Animate colors moving together
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (showAnimation) 0f else 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleOffset"
    )

    val mergeProgress by animateFloatAsState(
        targetValue = if (showAnimation) 1f else 0f,
        animationSpec = tween(500),
        label = "mergeProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showAnimation && resultColor != null) {
            // Show merged result
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(resultColor.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = resultColor.emoji,
                            fontSize = 48.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultColor.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Show two colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color 1
                ColorBubble(
                    color = color1,
                    modifier = Modifier.offset(x = offset.dp)
                )

                // Plus sign
                Text(
                    text = "+",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Color 2
                ColorBubble(
                    color = color2,
                    modifier = Modifier.offset(x = (-offset).dp)
                )
            }
        }
    }
}

@Composable
private fun ColorBubble(
    color: PrimaryColor,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color.color, CircleShape)
                .border(4.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = color.emoji,
                fontSize = 32.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = color.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerOptions(
    options: List<SecondaryColor>,
    selectedAnswer: SecondaryColor?,
    correctAnswer: SecondaryColor,
    showResult: Boolean,
    onAnswerClick: (SecondaryColor) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selectedAnswer
            val isCorrect = option == correctAnswer

            val scale by animateFloatAsState(
                targetValue = when {
                    showResult && isCorrect -> 1.2f
                    showResult && isSelected && !isCorrect -> 0.85f
                    else -> 1f
                },
                animationSpec = spring(dampingRatio = 0.6f),
                label = "optionScale"
            )

            val borderColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFE53935)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> Color.Transparent
            }

            Card(
                onClick = { if (!showResult) onAnswerClick(option) },
                modifier = Modifier
                    .size(90.dp)
                    .scale(scale),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = option.color
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 12.dp else 6.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (borderColor != Color.Transparent) {
                                Modifier.border(5.dp, borderColor, CircleShape)
                            } else {
                                Modifier.border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = option.emoji,
                            fontSize = 28.sp
                        )
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
