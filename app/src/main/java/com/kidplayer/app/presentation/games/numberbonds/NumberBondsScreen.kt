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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
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
        gameName = stringResource(R.string.game_numberbonds_name),
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val isLandscape = maxWidth > maxHeight
            val isCompact = maxHeight < 500.dp

            if (isLandscape) {
                // Landscape layout - diagram on left, question & options on right
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Round indicator + Diagram
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoundIndicator(
                            round = uiState.round,
                            totalRounds = NumberBondsConfig.TOTAL_ROUNDS,
                            correctCount = uiState.correctCount,
                            isCompact = true
                        )

                        uiState.currentProblem?.let { problem ->
                            NumberBondDiagram(
                                problem = problem,
                                selectedAnswer = uiState.selectedAnswer,
                                showResult = uiState.showResult,
                                isCorrect = uiState.isCorrect,
                                isCompact = true
                            )
                        }
                    }

                    // Right side: Question + Options
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        uiState.currentProblem?.let { problem ->
                            // Question text
                            Text(
                                text = if (uiState.showResult) {
                                    if (uiState.isCorrect) "CORRECT!\n${problem.givenNumber} + ${problem.correctAnswer} = ${problem.targetNumber}"
                                    else "${problem.givenNumber} + ${problem.correctAnswer} = ${problem.targetNumber}"
                                } else {
                                    "What goes with ${problem.givenNumber}\nto make ${problem.targetNumber}?"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = if (uiState.showResult && uiState.isCorrect) Color(0xFF4CAF50)
                                else if (uiState.showResult) Color(0xFFFF9800)
                                else MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Answer options
                            AnswerOptions(
                                options = problem.options,
                                correctAnswer = problem.correctAnswer,
                                selectedAnswer = uiState.selectedAnswer,
                                showResult = uiState.showResult,
                                onAnswerClick = { answer ->
                                    haptic.performMedium()
                                    viewModel.selectAnswer(answer)
                                },
                                isCompact = true
                            )
                        }
                    }
                }
            } else {
                // Portrait layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isCompact) 8.dp else 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Round indicator
                    RoundIndicator(
                        round = uiState.round,
                        totalRounds = NumberBondsConfig.TOTAL_ROUNDS,
                        correctCount = uiState.correctCount,
                        isCompact = isCompact
                    )

                    // Problem display with bond diagram
                    uiState.currentProblem?.let { problem ->
                        NumberBondDiagram(
                            problem = problem,
                            selectedAnswer = uiState.selectedAnswer,
                            showResult = uiState.showResult,
                            isCorrect = uiState.isCorrect,
                            isCompact = isCompact
                        )

                        Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))

                        // Question text
                        Text(
                            text = if (uiState.showResult) {
                                if (uiState.isCorrect) "CORRECT! ${problem.givenNumber} + ${problem.correctAnswer} = ${problem.targetNumber}"
                                else "${problem.givenNumber} + ${problem.correctAnswer} = ${problem.targetNumber}"
                            } else {
                                "WHAT NUMBER GOES WITH ${problem.givenNumber} TO MAKE ${problem.targetNumber}?"
                            },
                            style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = if (uiState.showResult && uiState.isCorrect) Color(0xFF4CAF50)
                            else if (uiState.showResult) Color(0xFFFF9800)
                            else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 24.dp))

                        // Answer options
                        AnswerOptions(
                            options = problem.options,
                            correctAnswer = problem.correctAnswer,
                            selectedAnswer = uiState.selectedAnswer,
                            showResult = uiState.showResult,
                            onAnswerClick = { answer ->
                                haptic.performMedium()
                                viewModel.selectAnswer(answer)
                            },
                            isCompact = isCompact
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
    correctCount: Int,
    isCompact: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 24.dp,
                vertical = if (isCompact) 8.dp else 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 16.dp)
        ) {
            Text(text = "🔗", fontSize = if (isCompact) 20.sp else 28.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUND",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$round/$totalRounds",
                    style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
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
                    style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
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
    isCorrect: Boolean,
    isCompact: Boolean = false
) {
    val (bgColor, accentColor) = NumberBondsVisuals.getColorsForTarget(problem.targetNumber)

    // Sizes based on compact mode
    val diagramSize = if (isCompact) 160.dp else 250.dp
    val padding = if (isCompact) 16.dp else 32.dp
    val topCircleSize = if (isCompact) 50.dp else 80.dp
    val bottomCircleSize = if (isCompact) 45.dp else 70.dp
    val topFontSize = if (isCompact) 24.sp else 36.sp
    val bottomFontSize = if (isCompact) 20.sp else 32.sp
    val plusFontSize = if (isCompact) 28.sp else 40.sp
    val strokeWidth = if (isCompact) 4f else 6f
    val bottomOffset = if (isCompact) 12.dp else 20.dp

    Card(
        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(bgColor)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(padding)
                .size(diagramSize),
            contentAlignment = Alignment.Center
        ) {
            // Draw connecting lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val topY = if (isCompact) 25f else 40f
                val bottomLeftX = size.width * 0.25f
                val bottomRightX = size.width * 0.75f
                val bottomY = size.height - (if (isCompact) 25f else 40f)

                // Left line
                drawLine(
                    color = Color(accentColor),
                    start = Offset(centerX, topY + (if (isCompact) 20 else 30)),
                    end = Offset(bottomLeftX, bottomY - (if (isCompact) 20 else 30)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )

                // Right line
                drawLine(
                    color = Color(accentColor),
                    start = Offset(centerX, topY + (if (isCompact) 20 else 30)),
                    end = Offset(bottomRightX, bottomY - (if (isCompact) 20 else 30)),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Top circle - Target number
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(topCircleSize)
                    .background(Color(accentColor), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${problem.targetNumber}",
                    fontSize = topFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Bottom left - Given number
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = bottomOffset)
                    .size(bottomCircleSize)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${problem.givenNumber}",
                    fontSize = bottomFontSize,
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
                    .offset(x = -bottomOffset)
                    .size(bottomCircleSize)
                    .background(answerBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        showResult -> "${problem.correctAnswer}"
                        selectedAnswer != null -> "$selectedAnswer"
                        else -> "?"
                    },
                    fontSize = bottomFontSize,
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
                fontSize = plusFontSize,
                fontWeight = FontWeight.Bold,
                color = Color(accentColor),
                modifier = Modifier.offset(y = if (isCompact) 20.dp else 30.dp)
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
    onAnswerClick: (Int) -> Unit,
    isCompact: Boolean = false
) {
    val buttonSize = if (isCompact) 52.dp else 72.dp
    val fontSize = if (isCompact) 20.sp else 28.sp
    val spacing = if (isCompact) 10.dp else 16.dp

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
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
                    .size(buttonSize)
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
                        fontSize = fontSize,
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
