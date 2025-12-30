package com.kidplayer.app.presentation.games.shapes

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.util.bouncyClickable
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ShapesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShapesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_shapes_name),
        gameId = "shapes",
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
                text = stringResource(R.string.game_round, uiState.round, ShapesConfig.TOTAL_ROUNDS).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.currentChallenge?.let { challenge ->
                // Question
                Text(
                    text = challenge.getQuestion(uiState.isRomanian).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Shape display area
                when (challenge.type) {
                    ChallengeType.FIND_SHAPE -> {
                        FindShapeDisplay(
                            shapes = challenge.displayShapes,
                            showResult = uiState.showResult,
                            onShapeClick = { shape ->
                                haptic.performMedium()
                                viewModel.selectShape(shape)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                    else -> {
                        SingleShapeDisplay(
                            shape = challenge.targetShape,
                            color = challenge.targetColor,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Answer options (not for FIND_SHAPE type)
                if (challenge.type != ChallengeType.FIND_SHAPE) {
                    AnswerOptions(
                        options = challenge.getOptions(uiState.isRomanian),
                        selectedAnswer = uiState.selectedAnswer,
                        correctAnswer = if (uiState.showResult) challenge.getCorrectAnswer(uiState.isRomanian) else null,
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
}

@Composable
private fun SingleShapeDisplay(
    shape: Shape,
    color: ShapeColor,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(shape, color) {
        visible = false
        kotlinx.coroutines.delay(100)
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "shapeScale"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            ShapeCanvas(
                shape = shape,
                color = color.color,
                modifier = Modifier.size(180.dp)
            )
        }
    }
}

@Composable
private fun FindShapeDisplay(
    shapes: List<DisplayShape>,
    showResult: Boolean,
    onShapeClick: (DisplayShape) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            shapes.forEach { displayShape ->
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(displayShape) {
                    kotlinx.coroutines.delay(100)
                    visible = true
                }

                val scale by animateFloatAsState(
                    targetValue = when {
                        showResult && displayShape.isTarget -> 1.2f
                        visible -> 1f
                        else -> 0f
                    },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "findShapeScale"
                )

                val borderColor = when {
                    showResult && displayShape.isTarget -> Color(0xFF4CAF50)
                    else -> Color.Transparent
                }

                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .bouncyClickable(enabled = !showResult) {
                            onShapeClick(displayShape)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = if (borderColor != Color.Transparent) {
                        androidx.compose.foundation.BorderStroke(4.dp, borderColor)
                    } else null
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ShapeCanvas(
                            shape = displayShape.shape,
                            color = displayShape.color.color,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapeCanvas(
    shape: Shape,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasSize = min(size.width, size.height)
        val center = Offset(size.width / 2, size.height / 2)

        when (shape) {
            Shape.CIRCLE -> drawCircle(color, radius = canvasSize / 2 * 0.9f, center = center)
            Shape.OVAL -> drawOval(color, topLeft = Offset(size.width * 0.1f, size.height * 0.2f), size = Size(size.width * 0.8f, size.height * 0.6f))
            Shape.SQUARE -> drawRect(color, topLeft = Offset(size.width * 0.1f, size.height * 0.1f), size = Size(canvasSize * 0.8f, canvasSize * 0.8f))
            Shape.RECTANGLE -> drawRect(color, topLeft = Offset(size.width * 0.05f, size.height * 0.2f), size = Size(size.width * 0.9f, size.height * 0.6f))
            Shape.TRIANGLE -> drawPolygon(color, center, canvasSize * 0.45f, 3)
            Shape.PENTAGON -> drawPolygon(color, center, canvasSize * 0.45f, 5)
            Shape.HEXAGON -> drawPolygon(color, center, canvasSize * 0.45f, 6)
            Shape.DIAMOND -> drawDiamond(color, center, canvasSize * 0.45f)
            Shape.STAR -> drawStar(color, center, canvasSize * 0.45f)
            Shape.HEART -> drawHeart(color, center, canvasSize * 0.4f)
        }
    }
}

private fun DrawScope.drawPolygon(color: Color, center: Offset, radius: Float, sides: Int) {
    val path = Path()
    val angleStep = (2 * Math.PI / sides).toFloat()
    val startAngle = (-Math.PI / 2).toFloat() // Start from top

    for (i in 0 until sides) {
        val angle = startAngle + i * angleStep
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)

        if (i == 0) path.moveTo(x, y)
        else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawDiamond(color: Color, center: Offset, radius: Float) {
    val path = Path()
    path.moveTo(center.x, center.y - radius) // Top
    path.lineTo(center.x + radius, center.y) // Right
    path.lineTo(center.x, center.y + radius) // Bottom
    path.lineTo(center.x - radius, center.y) // Left
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawStar(color: Color, center: Offset, outerRadius: Float) {
    val innerRadius = outerRadius * 0.4f
    val path = Path()
    val points = 5

    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = (i * Math.PI / points - Math.PI / 2).toFloat()
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)

        if (i == 0) path.moveTo(x, y)
        else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawHeart(color: Color, center: Offset, size: Float) {
    val path = Path()
    val width = size * 2
    val height = size * 1.8f

    // Starting point at bottom
    path.moveTo(center.x, center.y + height * 0.4f)

    // Left curve
    path.cubicTo(
        center.x - width * 0.5f, center.y,
        center.x - width * 0.5f, center.y - height * 0.4f,
        center.x, center.y - height * 0.1f
    )

    // Right curve
    path.cubicTo(
        center.x + width * 0.5f, center.y - height * 0.4f,
        center.x + width * 0.5f, center.y,
        center.x, center.y + height * 0.4f
    )

    path.close()
    drawPath(path, color)
}

@Composable
private fun AnswerOptions(
    options: List<String>,
    selectedAnswer: String?,
    correctAnswer: String?,
    showResult: Boolean,
    onAnswerSelect: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Display options in 2x2 grid
        for (i in options.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (j in i until minOf(i + 2, options.size)) {
                    val option = options[j]
                    val isSelected = option == selectedAnswer
                    val isCorrect = option == correctAnswer
                    val isWrong = showResult && isSelected && !isCorrect

                    val backgroundColor = when {
                        isCorrect && showResult -> Color(0xFF4CAF50)
                        isWrong -> Color(0xFFE53935)
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
                            isCorrect && showResult -> 1.05f
                            isWrong -> 0.95f
                            else -> 1f
                        },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "optionScale$j"
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
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
                            Text(
                                text = option.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}
