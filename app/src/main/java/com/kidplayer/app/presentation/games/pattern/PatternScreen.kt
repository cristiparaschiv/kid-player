package com.kidplayer.app.presentation.games.pattern

import androidx.compose.animation.animateColorAsState
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
fun PatternScreen(
    onNavigateBack: () -> Unit,
    viewModel: PatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Pattern",
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
            // Level indicator
            LevelIndicator(
                level = uiState.level,
                round = uiState.round,
                totalRounds = PatternConfig.ROUNDS_PER_LEVEL
            )

            // Pattern display
            uiState.currentPuzzle?.let { puzzle ->
                PatternDisplay(
                    pattern = puzzle.pattern,
                    selectedAnswer = uiState.selectedOption,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Instruction text
                Text(
                    text = if (uiState.showResult) {
                        if (uiState.isCorrect) "CORRECT!" else "TRY AGAIN!"
                    } else {
                        "WHAT COMES NEXT?"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        !uiState.showResult -> MaterialTheme.colorScheme.onSurface
                        uiState.isCorrect -> Color(0xFF4CAF50)
                        else -> Color(0xFFE53935)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Options
                OptionsGrid(
                    options = puzzle.options,
                    selectedOption = uiState.selectedOption,
                    correctAnswer = puzzle.correctAnswer,
                    showResult = uiState.showResult,
                    onOptionClick = { option ->
                        haptic.performMedium()
                        viewModel.selectOption(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelIndicator(
    level: Int,
    round: Int,
    totalRounds: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$level",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUND",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$round/$totalRounds",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun PatternDisplay(
    pattern: List<PatternElement?>,
    selectedAnswer: PatternElement?,
    showResult: Boolean,
    isCorrect: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pattern.forEachIndexed { index, element ->
                if (element != null) {
                    PatternElementDisplay(
                        element = element,
                        size = 60.dp
                    )
                } else {
                    // Missing element slot
                    MissingSlot(
                        selectedAnswer = selectedAnswer,
                        showResult = showResult,
                        isCorrect = isCorrect,
                        size = 60.dp
                    )
                }

                if (index < pattern.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
private fun PatternElementDisplay(
    element: PatternElement,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = element.color,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = element.shape.symbol,
            fontSize = (size.value * 0.5f).sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MissingSlot(
    selectedAnswer: PatternElement?,
    showResult: Boolean,
    isCorrect: Boolean,
    size: androidx.compose.ui.unit.Dp
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            showResult && isCorrect -> Color(0xFF4CAF50)
            showResult && !isCorrect -> Color(0xFFE53935)
            else -> MaterialTheme.colorScheme.outline
        },
        label = "borderColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (showResult && isCorrect) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(
                color = selectedAnswer?.color ?: Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selectedAnswer != null) {
            Text(
                text = selectedAnswer.shape.symbol,
                fontSize = (size.value * 0.5f).sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "?",
                fontSize = (size.value * 0.5f).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionsGrid(
    options: List<PatternElement>,
    selectedOption: PatternElement?,
    correctAnswer: PatternElement,
    showResult: Boolean,
    onOptionClick: (PatternElement) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            val isCorrect = option == correctAnswer

            val borderColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFE53935)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> Color.Transparent
            }

            val scale by animateFloatAsState(
                targetValue = when {
                    showResult && isCorrect -> 1.15f
                    showResult && isSelected && !isCorrect -> 0.9f
                    else -> 1f
                },
                animationSpec = spring(dampingRatio = 0.6f),
                label = "optionScale"
            )

            Card(
                onClick = { if (!showResult) onOptionClick(option) },
                modifier = Modifier
                    .size(70.dp)
                    .scale(scale),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = option.color
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (borderColor != Color.Transparent) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.shape.symbol,
                        fontSize = 32.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
