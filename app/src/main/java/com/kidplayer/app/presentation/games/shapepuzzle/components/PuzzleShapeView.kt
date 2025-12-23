package com.kidplayer.app.presentation.games.shapepuzzle.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.games.shapepuzzle.PuzzleShape
import com.kidplayer.app.presentation.games.shapepuzzle.PuzzleShapeType
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Draggable shape that can be moved around
 */
@Composable
fun DraggableShape(
    shape: PuzzleShape,
    onDragEnd: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "drag_scale"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (shape.isPlaced) 0f else 1f
            }
            .pointerInput(shape.isPlaced) {
                if (!shape.isPlaced) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            onDragEnd(Offset(offsetX, offsetY))
                            // Reset position if not placed
                            offsetX = 0f
                            offsetY = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            offsetX = 0f
                            offsetY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    )
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawShape(shape.type, shape.color, Fill)
        }
    }
}

/**
 * Target silhouette where shapes should be placed
 */
@Composable
fun ShapeTarget(
    shapeType: PuzzleShapeType,
    isPlaced: Boolean,
    placedColor: Color?,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (isPlaced) 1f else 0.3f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "target_alpha"
    )

    val targetScale by animateFloatAsState(
        targetValue = if (isPlaced) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "target_scale"
    )

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = targetScale
            scaleY = targetScale
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isPlaced && placedColor != null) {
                // Show filled shape when placed
                drawShape(shapeType, placedColor, Fill)
            } else {
                // Show outline silhouette
                drawShape(shapeType, Color.Gray.copy(alpha = alpha), Stroke(width = 4.dp.toPx()))
            }
        }
    }
}

/**
 * Draw a shape based on type
 */
private fun DrawScope.drawShape(type: PuzzleShapeType, color: Color, style: androidx.compose.ui.graphics.drawscope.DrawStyle) {
    val padding = size.minDimension * 0.1f
    val availableSize = size.minDimension - 2 * padding

    when (type) {
        PuzzleShapeType.CIRCLE -> {
            drawCircle(
                color = color,
                radius = availableSize / 2,
                center = center,
                style = style
            )
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
            // Draw crescent moon
            drawCircle(color = color, radius = availableSize / 2.2f, center = center, style = style)
            if (style is Fill) {
                // Cut out for crescent effect
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

        // Left curve
        cubicTo(
            startX - width / 2, startY - height / 3,
            startX - width / 2, center.y - height / 3,
            startX, center.y - height / 2.5f
        )

        // Right curve
        cubicTo(
            startX + width / 2, center.y - height / 3,
            startX + width / 2, startY - height / 3,
            startX, startY
        )
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
