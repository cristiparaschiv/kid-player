package com.kidplayer.app.presentation.games.analogclock

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun AnalogClockScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalogClockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()
    val configuration = LocalConfiguration.current
    val isCompactHeight = configuration.screenHeightDp < 500

    GameScaffold(
        gameName = stringResource(R.string.game_analogclock_name),
        gameId = "analogclock",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        if (isCompactHeight) {
            // Landscape / compact layout - clock on left, options on right
            CompactLayout(
                uiState = uiState,
                onAnswerClick = { time ->
                    haptic.performMedium()
                    viewModel.selectAnswer(time)
                }
            )
        } else {
            // Portrait / spacious layout - clock on top, options below
            StandardLayout(
                uiState = uiState,
                onAnswerClick = { time ->
                    haptic.performMedium()
                    viewModel.selectAnswer(time)
                }
            )
        }
    }
}

@Composable
private fun StandardLayout(
    uiState: AnalogClockUiState,
    onAnswerClick: (ClockTime) -> Unit
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
            totalRounds = AnalogClockConfig.TOTAL_ROUNDS,
            correctCount = uiState.correctCount
        )

        // Question text
        Text(
            text = if (uiState.showResult) {
                if (uiState.isCorrect) stringResource(R.string.game_correct)
                else stringResource(R.string.analogclock_answer_is, uiState.currentProblem?.correctTime?.format() ?: "")
            } else {
                stringResource(R.string.analogclock_what_time)
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = when {
                uiState.showResult && uiState.isCorrect -> Color(0xFF4CAF50)
                uiState.showResult -> Color(0xFFFF9800)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        // Clock display
        uiState.currentProblem?.let { problem ->
            AnalogClock(
                time = problem.correctTime,
                modifier = Modifier.size(200.dp)
            )
        }

        // Answer options
        uiState.currentProblem?.let { problem ->
            AnswerOptions(
                options = problem.options,
                correctTime = problem.correctTime,
                selectedAnswer = uiState.selectedAnswer,
                showResult = uiState.showResult,
                onAnswerClick = onAnswerClick
            )
        }
    }
}

@Composable
private fun CompactLayout(
    uiState: AnalogClockUiState,
    onAnswerClick: (ClockTime) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - Clock and round info
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            RoundIndicator(
                round = uiState.round,
                totalRounds = AnalogClockConfig.TOTAL_ROUNDS,
                correctCount = uiState.correctCount
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.currentProblem?.let { problem ->
                AnalogClock(
                    time = problem.correctTime,
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question/result text
            Text(
                text = if (uiState.showResult) {
                    if (uiState.isCorrect) stringResource(R.string.game_correct)
                    else uiState.currentProblem?.correctTime?.format() ?: ""
                } else {
                    stringResource(R.string.analogclock_what_time)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = when {
                    uiState.showResult && uiState.isCorrect -> Color(0xFF4CAF50)
                    uiState.showResult -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }

        // Right side - Answer options in 2x2 grid
        uiState.currentProblem?.let { problem ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnswerOptionsGrid(
                    options = problem.options,
                    correctTime = problem.correctTime,
                    selectedAnswer = uiState.selectedAnswer,
                    showResult = uiState.showResult,
                    onAnswerClick = onAnswerClick,
                    buttonSize = 70.dp
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
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🕐", fontSize = 24.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.game_round_label),
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
                    text = stringResource(R.string.game_correct_label),
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
private fun AnalogClock(
    time: ClockTime,
    modifier: Modifier = Modifier
) {
    val clockFaceColor = Color.White
    val clockBorderColor = Color(0xFF333333)
    val hourHandColor = Color(0xFF333333)
    val minuteHandColor = Color(0xFF1976D2)
    val centerDotColor = Color(0xFFE53935)

    Card(
        modifier = modifier,
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = clockFaceColor)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2

            // Draw clock face border
            drawCircle(
                color = clockBorderColor,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw hour markers
            drawHourMarkers(center, radius, clockBorderColor)

            // Draw hour numbers
            drawHourNumbers(center, radius)

            // Draw minute hand (longer, thinner)
            drawHand(
                center = center,
                length = radius * 0.75f,
                angle = time.minuteHandAngle(),
                color = minuteHandColor,
                strokeWidth = 4.dp.toPx()
            )

            // Draw hour hand (shorter, thicker)
            drawHand(
                center = center,
                length = radius * 0.5f,
                angle = time.hourHandAngle(),
                color = hourHandColor,
                strokeWidth = 6.dp.toPx()
            )

            // Draw center dot
            drawCircle(
                color = centerDotColor,
                radius = 8.dp.toPx(),
                center = center
            )
        }
    }
}

private fun DrawScope.drawHourMarkers(
    center: Offset,
    radius: Float,
    color: Color
) {
    for (i in 0 until 12) {
        val angle = Math.toRadians((i * 30 - 90).toDouble())
        val innerRadius = radius * 0.85f
        val outerRadius = radius * 0.95f

        val startX = center.x + (innerRadius * cos(angle)).toFloat()
        val startY = center.y + (innerRadius * sin(angle)).toFloat()
        val endX = center.x + (outerRadius * cos(angle)).toFloat()
        val endY = center.y + (outerRadius * sin(angle)).toFloat()

        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawHourNumbers(
    center: Offset,
    radius: Float
) {
    // Note: In Compose Canvas, we can't easily draw text
    // The hour markers serve as visual guides instead
    // For a more complete implementation, you could use drawIntoCanvas
}

private fun DrawScope.drawHand(
    center: Offset,
    length: Float,
    angle: Float,
    color: Color,
    strokeWidth: Float
) {
    val angleRad = Math.toRadians((angle - 90).toDouble())
    val endX = center.x + (length * cos(angleRad)).toFloat()
    val endY = center.y + (length * sin(angleRad)).toFloat()

    drawLine(
        color = color,
        start = center,
        end = Offset(endX, endY),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

@Composable
private fun AnswerOptions(
    options: List<ClockTime>,
    correctTime: ClockTime,
    selectedAnswer: ClockTime?,
    showResult: Boolean,
    onAnswerClick: (ClockTime) -> Unit
) {
    AnswerOptionsGrid(
        options = options,
        correctTime = correctTime,
        selectedAnswer = selectedAnswer,
        showResult = showResult,
        onAnswerClick = onAnswerClick,
        buttonSize = 80.dp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnswerOptionsGrid(
    options: List<ClockTime>,
    correctTime: ClockTime,
    selectedAnswer: ClockTime?,
    showResult: Boolean,
    onAnswerClick: (ClockTime) -> Unit,
    buttonSize: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 2x2 grid
        for (row in 0 until 2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0 until 2) {
                    val index = row * 2 + col
                    if (index < options.size) {
                        val option = options[index]
                        TimeOptionButton(
                            time = option,
                            isCorrect = option == correctTime,
                            isSelected = option == selectedAnswer,
                            showResult = showResult,
                            onClick = { onAnswerClick(option) },
                            size = buttonSize
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeOptionButton(
    time: ClockTime,
    isCorrect: Boolean,
    isSelected: Boolean,
    showResult: Boolean,
    onClick: () -> Unit,
    size: Dp
) {
    val scale by animateFloatAsState(
        targetValue = when {
            showResult && isCorrect -> 1.15f
            showResult && isSelected && !isCorrect -> 0.9f
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

    val textColor = when {
        showResult && (isCorrect || isSelected) -> Color.White
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        onClick = { if (!showResult) onClick() },
        modifier = Modifier
            .size(size)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected || (showResult && isCorrect)) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time.format(),
                fontSize = if (size >= 80.dp) 22.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
