package com.kidplayer.app.presentation.games.shapepuzzle

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.util.bouncyClickable
import kotlin.math.cos
import kotlin.math.sin

/**
 * Shape Puzzle Game Screen
 * Tap a shape, then tap the matching target to place it
 */
@Composable
fun ShapePuzzleScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShapePuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedShapeId by remember { mutableStateOf<Int?>(null) }
    val haptic = rememberHapticFeedback()

    // Reset selection when game restarts
    LaunchedEffect(uiState.gameState) {
        if (uiState.gameState is GameState.Playing && uiState.placedCount == 0) {
            selectedShapeId = null
        }
    }

    GameScaffold(
        gameName = "Shape Puzzle",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = {
            selectedShapeId = null
            viewModel.startNewGame()
        },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Difficulty selector
            if (uiState.gameState is GameState.Playing || uiState.gameState == GameState.Ready) {
                DifficultySelector(
                    currentDifficulty = uiState.config.difficulty,
                    onDifficultyChange = {
                        selectedShapeId = null
                        viewModel.setDifficulty(it)
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Instructions and progress in a row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedShapeId != null) "Tap matching outline!" else "Tap a shape",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${uiState.placedCount}/${uiState.shapes.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Target area (outlines)
            Text(
                text = "Match the shapes:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            TargetGrid(
                shapes = uiState.shapes,
                targetOrder = uiState.targetOrder,
                selectedShapeId = selectedShapeId,
                onTargetClick = { targetId ->
                    if (selectedShapeId != null) {
                        val success = viewModel.onShapePlaced(selectedShapeId!!, targetId)
                        if (success) {
                            haptic.performConfirm()
                        } else {
                            haptic.performReject()
                        }
                        selectedShapeId = null
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Draggable shapes area
            Text(
                text = "Your shapes:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            ShapeSelectionGrid(
                shapes = uiState.shapes,
                selectedShapeId = selectedShapeId,
                onShapeClick = { shapeId ->
                    haptic.performLight()
                    selectedShapeId = if (selectedShapeId == shapeId) null else shapeId
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == currentDifficulty
            FilterChip(
                selected = isSelected,
                onClick = { onDifficultyChange(difficulty) },
                label = {
                    Text(
                        text = when (difficulty) {
                            Difficulty.EASY -> "Easy (3)"
                            Difficulty.MEDIUM -> "Medium (5)"
                            Difficulty.HARD -> "Hard (7)"
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun TargetGrid(
    shapes: List<PuzzleShape>,
    targetOrder: List<Int>,
    selectedShapeId: Int?,
    onTargetClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = when {
        shapes.size <= 3 -> 3
        shapes.size <= 5 -> 3
        else -> 4
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val rows = (targetOrder.size + columns - 1) / columns
        for (row in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < targetOrder.size) {
                        val targetId = targetOrder[index]
                        val shape = shapes.find { it.id == targetId }
                        if (shape != null) {
                            key(index) {
                                TargetSlot(
                                    shape = shape,
                                    isHighlighted = selectedShapeId != null && !shape.isPlaced,
                                    onClick = { onTargetClick(targetId) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetSlot(
    shape: PuzzleShape,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderAlpha by animateFloatAsState(
        targetValue = if (isHighlighted) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "border_alpha"
    )

    Card(
        modifier = modifier
            .then(
                if (isHighlighted && !shape.isPlaced) {
                    Modifier
                        .border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .bouncyClickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (shape.isPlaced) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
                if (shape.isPlaced) {
                    drawShapeType(shape.type, shape.color, Fill)
                } else {
                    drawShapeType(shape.type, Color.Gray.copy(alpha = 0.4f), Stroke(width = 3.dp.toPx()))
                }
            }
        }
    }
}

@Composable
private fun ShapeSelectionGrid(
    shapes: List<PuzzleShape>,
    selectedShapeId: Int?,
    onShapeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableShapes = shapes.filter { !it.isPlaced }
    val columns = when {
        shapes.size <= 3 -> 3
        shapes.size <= 5 -> 3
        else -> 4
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (availableShapes.isEmpty() && shapes.isNotEmpty()) {
            // Show "All placed!" when done
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "All shapes placed!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        } else {
            val rows = (availableShapes.size + columns - 1) / columns
            for (row in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < availableShapes.size) {
                            val shape = availableShapes[index]
                            key(shape.id) {
                                SelectableShape(
                                    shape = shape,
                                    isSelected = selectedShapeId == shape.id,
                                    onClick = { onShapeClick(shape.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableShape(
    shape: PuzzleShape,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "selection_scale"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .bouncyClickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
                drawShapeType(shape.type, shape.color, Fill)
            }
        }
    }
}

private fun DrawScope.drawShapeType(
    type: PuzzleShapeType,
    color: Color,
    style: androidx.compose.ui.graphics.drawscope.DrawStyle
) {
    val padding = size.minDimension * 0.1f
    val availableSize = size.minDimension - 2 * padding

    when (type) {
        PuzzleShapeType.CIRCLE -> {
            drawCircle(color = color, radius = availableSize / 2, center = center, style = style)
        }
        PuzzleShapeType.SQUARE -> {
            val squareSize = availableSize * 0.85f
            drawRoundRect(
                color = color,
                topLeft = Offset(center.x - squareSize / 2, center.y - squareSize / 2),
                size = Size(squareSize, squareSize),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = style
            )
        }
        PuzzleShapeType.TRIANGLE -> {
            val path = Path().apply {
                moveTo(center.x, center.y - availableSize / 2)
                lineTo(center.x + availableSize / 2, center.y + availableSize / 3)
                lineTo(center.x - availableSize / 2, center.y + availableSize / 3)
                close()
            }
            drawPath(path, color, style = style)
        }
        PuzzleShapeType.STAR -> {
            val path = createStarPath(center.x, center.y, availableSize / 2, availableSize / 4)
            drawPath(path, color, style = style)
        }
        PuzzleShapeType.HEART -> {
            val path = createHeartPath(center, availableSize)
            drawPath(path, color, style = style)
        }
        PuzzleShapeType.DIAMOND -> {
            val path = Path().apply {
                moveTo(center.x, center.y - availableSize / 2)
                lineTo(center.x + availableSize / 2.5f, center.y)
                lineTo(center.x, center.y + availableSize / 2)
                lineTo(center.x - availableSize / 2.5f, center.y)
                close()
            }
            drawPath(path, color, style = style)
        }
        PuzzleShapeType.HEXAGON -> {
            val path = createPolygonPath(center, availableSize / 2, 6)
            drawPath(path, color, style = style)
        }
        PuzzleShapeType.PENTAGON -> {
            val path = createPolygonPath(center, availableSize / 2, 5)
            drawPath(path, color, style = style)
        }
        PuzzleShapeType.OVAL -> {
            drawOval(
                color = color,
                topLeft = Offset(center.x - availableSize / 2, center.y - availableSize / 3),
                size = Size(availableSize, availableSize * 0.66f),
                style = style
            )
        }
        PuzzleShapeType.CRESCENT -> {
            drawCircle(color = color, radius = availableSize / 2.2f, center = center, style = style)
            if (style is Fill) {
                drawCircle(
                    color = Color.White,
                    radius = availableSize / 2.5f,
                    center = Offset(center.x + availableSize / 4, center.y - availableSize / 6)
                )
            }
        }
    }
}

private fun createStarPath(cx: Float, cy: Float, outerRadius: Float, innerRadius: Float): Path {
    val path = Path()
    val angleStep = Math.PI / 5
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = i * angleStep - Math.PI / 2
        val x = cx + (radius * cos(angle)).toFloat()
        val y = cy + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun createHeartPath(center: Offset, size: Float): Path {
    val width = size * 0.9f
    val height = size * 0.85f
    return Path().apply {
        val startX = center.x
        val startY = center.y + height / 3
        moveTo(startX, startY)
        cubicTo(startX - width / 2, startY - height / 3, startX - width / 2, center.y - height / 3, startX, center.y - height / 2.5f)
        cubicTo(startX + width / 2, center.y - height / 3, startX + width / 2, startY - height / 3, startX, startY)
        close()
    }
}

private fun createPolygonPath(center: Offset, radius: Float, sides: Int): Path {
    val path = Path()
    val angleStep = 2 * Math.PI / sides
    for (i in 0 until sides) {
        val angle = i * angleStep - Math.PI / 2
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
