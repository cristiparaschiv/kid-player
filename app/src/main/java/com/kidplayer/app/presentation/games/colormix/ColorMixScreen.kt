package com.kidplayer.app.presentation.games.colormix

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold

@Composable
fun ColorMixScreen(
    onNavigateBack: () -> Unit,
    viewModel: ColorMixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()
    val configuration = LocalConfiguration.current

    // Detect if we're on a compact height screen (landscape phone)
    // Medium phones in landscape have ~411dp height, so use 480dp threshold
    val isCompactHeight = configuration.screenHeightDp < 480

    GameScaffold(
        gameName = stringResource(R.string.game_colormix_name),
        gameId = "colormix",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        uiState.currentPuzzle?.let { puzzle ->
            if (isCompactHeight) {
                // Horizontal layout for landscape phones
                CompactColorMixLayout(
                    round = uiState.round,
                    totalRounds = ColorMixConfig.TOTAL_ROUNDS,
                    puzzle = puzzle,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect,
                    showMixAnimation = uiState.showMixAnimation,
                    onAnswerClick = { answer ->
                        haptic.performMedium()
                        viewModel.selectAnswer(answer)
                    }
                )
            } else {
                // Standard vertical layout for tablets
                StandardColorMixLayout(
                    round = uiState.round,
                    totalRounds = ColorMixConfig.TOTAL_ROUNDS,
                    puzzle = puzzle,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect,
                    showMixAnimation = uiState.showMixAnimation,
                    onAnswerClick = { answer ->
                        haptic.performMedium()
                        viewModel.selectAnswer(answer)
                    }
                )
            }
        }
    }
}

/**
 * Compact horizontal layout for landscape phones
 */
@Composable
private fun CompactColorMixLayout(
    round: Int,
    totalRounds: Int,
    puzzle: ColorMixPuzzle,
    selectedAnswer: SecondaryColor?,
    showResult: Boolean,
    isCorrect: Boolean,
    showMixAnimation: Boolean,
    onAnswerClick: (SecondaryColor) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Color mixing display
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompactRoundIndicator(round = round, totalRounds = totalRounds)

            Spacer(modifier = Modifier.height(8.dp))

            ColorMixingDisplayCompact(
                color1 = puzzle.color1,
                color2 = puzzle.color2,
                resultColor = if (showMixAnimation) puzzle.correctAnswer else null,
                showAnimation = showMixAnimation
            )
        }

        // Right side: Question + Options
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Question text
            Text(
                text = if (showResult) {
                    if (isCorrect) stringResource(R.string.colormix_correct).uppercase()
                    else stringResource(R.string.colormix_try_again).uppercase()
                } else {
                    stringResource(R.string.colormix_what_color).uppercase()
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = when {
                    !showResult -> MaterialTheme.colorScheme.onSurface
                    isCorrect -> Color(0xFF4CAF50)
                    else -> Color(0xFFFF9800)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Answer options in 2x2 grid
            AnswerOptionsCompact(
                options = puzzle.options,
                selectedAnswer = selectedAnswer,
                correctAnswer = puzzle.correctAnswer,
                showResult = showResult,
                onAnswerClick = onAnswerClick
            )
        }
    }
}

/**
 * Standard vertical layout for tablets
 */
@Composable
private fun StandardColorMixLayout(
    round: Int,
    totalRounds: Int,
    puzzle: ColorMixPuzzle,
    selectedAnswer: SecondaryColor?,
    showResult: Boolean,
    isCorrect: Boolean,
    showMixAnimation: Boolean,
    onAnswerClick: (SecondaryColor) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        RoundIndicator(round = round, totalRounds = totalRounds)

        ColorMixingDisplay(
            color1 = puzzle.color1,
            color2 = puzzle.color2,
            resultColor = if (showMixAnimation) puzzle.correctAnswer else null,
            showAnimation = showMixAnimation
        )

        Text(
            text = if (showResult) {
                if (isCorrect) {
                    stringResource(R.string.colormix_makes,
                        puzzle.color1.displayName.uppercase(),
                        puzzle.color2.displayName.uppercase(),
                        puzzle.correctAnswer.displayName.uppercase())
                } else {
                    stringResource(R.string.colormix_not_quite, puzzle.correctAnswer.displayName.uppercase()).uppercase()
                }
            } else {
                stringResource(R.string.colormix_what_color_make).uppercase()
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = when {
                !showResult -> MaterialTheme.colorScheme.onSurface
                isCorrect -> Color(0xFF4CAF50)
                else -> Color(0xFFFF9800)
            }
        )

        AnswerOptions(
            options = puzzle.options,
            selectedAnswer = selectedAnswer,
            correctAnswer = puzzle.correctAnswer,
            showResult = showResult,
            onAnswerClick = onAnswerClick
        )
    }
}

/**
 * Compact round indicator
 */
@Composable
private fun CompactRoundIndicator(round: Int, totalRounds: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "🎨", fontSize = 16.sp)
            Text(
                text = "$round/$totalRounds",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
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
                text = stringResource(R.string.colormix_round_of, round, totalRounds).uppercase(),
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

/**
 * Compact color mixing display for small screens
 */
@Composable
private fun ColorMixingDisplayCompact(
    color1: PrimaryColor,
    color2: PrimaryColor,
    resultColor: SecondaryColor?,
    showAnimation: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleAnimationCompact")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (showAnimation) 0f else 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleOffsetCompact"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        if (showAnimation && resultColor != null) {
            // Show merged result
            AnimatedVisibility(
                visible = true,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(resultColor.color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = resultColor.emoji, fontSize = 28.sp)
                    }
                    Text(
                        text = resultColor.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Show two colors
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorBubbleCompact(color = color1, modifier = Modifier.offset(x = offset.dp))

                Text(
                    text = "+",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ColorBubbleCompact(color = color2, modifier = Modifier.offset(x = (-offset).dp))
            }
        }
    }
}

@Composable
private fun ColorBubbleCompact(
    color: PrimaryColor,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color.color, CircleShape)
                .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = color.emoji, fontSize = 20.sp)
        }
        Text(
            text = color.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Compact 2x2 answer options grid
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerOptionsCompact(
    options: List<SecondaryColor>,
    selectedAnswer: SecondaryColor?,
    correctAnswer: SecondaryColor,
    showResult: Boolean,
    onAnswerClick: (SecondaryColor) -> Unit
) {
    val optionSize = 52.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        options.chunked(2).forEach { rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowOptions.forEach { option ->
                    val isSelected = option == selectedAnswer
                    val isCorrect = option == correctAnswer

                    val scale by animateFloatAsState(
                        targetValue = when {
                            showResult && isCorrect -> 1.1f
                            showResult && isSelected && !isCorrect -> 0.9f
                            else -> 1f
                        },
                        animationSpec = spring(dampingRatio = 0.6f),
                        label = "optionScaleCompact"
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
                            .size(optionSize)
                            .scale(scale),
                        shape = CircleShape,
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
                                        Modifier.border(3.dp, borderColor, CircleShape)
                                    } else {
                                        Modifier.border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option.emoji, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
