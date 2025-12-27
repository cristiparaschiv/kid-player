package com.kidplayer.app.presentation.games.counting

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.SoundType
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.components.rememberSoundManager
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun CountingScreen(
    onNavigateBack: () -> Unit,
    viewModel: CountingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()
    val soundManager = rememberSoundManager()

    // Play sound on result
    LaunchedEffect(uiState.showResult, uiState.selectedAnswer) {
        if (uiState.showResult && uiState.selectedAnswer != null) {
            val isCorrect = uiState.selectedAnswer == uiState.currentChallenge?.correctAnswer
            if (isCorrect) {
                soundManager.playCorrect()
            } else {
                soundManager.playWrong()
            }
        }
    }

    GameScaffold(
        gameName = "Counting",
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
            // Round indicator
            Text(
                text = "ROUND ${uiState.round}/${CountingConfig.TOTAL_ROUNDS}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Question
            uiState.currentChallenge?.let { challenge ->
                Text(
                    text = "HOW MANY ${challenge.objectName.uppercase()}?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Objects display area
                ObjectsDisplay(
                    challenge = challenge,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Answer options
                AnswerOptions(
                    options = challenge.options,
                    selectedAnswer = uiState.selectedAnswer,
                    correctAnswer = if (uiState.showResult) challenge.correctAnswer else null,
                    showResult = uiState.showResult,
                    onAnswerSelect = { answer ->
                        haptic.performMedium()
                        viewModel.selectAnswer(answer)
                    }
                )
            }
        }
    }
}

@Composable
private fun ObjectsDisplay(
    challenge: CountingChallenge,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val boxWidth = maxWidth
            val boxHeight = maxHeight

            challenge.objectPositions.forEachIndexed { index, position ->
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(challenge) {
                    kotlinx.coroutines.delay(index * 100L)
                    visible = true
                }

                val scale by animateFloatAsState(
                    targetValue = if (visible) position.scale else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "objectScale$index"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = boxWidth * position.x - 24.dp,
                            y = boxHeight * position.y - 24.dp
                        )
                        .scale(scale)
                        .rotate(position.rotation)
                ) {
                    Text(
                        text = challenge.emoji,
                        fontSize = 48.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerOptions(
    options: List<Int>,
    selectedAnswer: Int?,
    correctAnswer: Int?,
    showResult: Boolean,
    onAnswerSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedAnswer
            val isCorrect = option == correctAnswer
            val isWrong = showResult && isSelected && !isCorrect

            val backgroundColor = when {
                isCorrect && showResult -> Color(0xFF4CAF50) // Green
                isWrong -> Color(0xFFE53935) // Red
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val textColor = when {
                isCorrect && showResult -> Color.White
                isWrong -> Color.White
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            val scale by animateFloatAsState(
                targetValue = when {
                    isCorrect && showResult -> 1.1f
                    isWrong -> 0.95f
                    else -> 1f
                },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "optionScale$option"
            )

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
                    .scale(scale)
                    .bouncyClickable(enabled = !showResult) {
                        onAnswerSelect(option)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 4.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = option.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        // Show feedback icon
                        if (showResult && (isCorrect || isWrong)) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(
                                    id = if (isCorrect) R.drawable.ic_checkmark else R.drawable.ic_cross
                                ),
                                contentDescription = if (isCorrect) "Correct" else "Wrong",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
