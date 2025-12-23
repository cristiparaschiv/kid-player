package com.kidplayer.app.presentation.games.memory.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.memory.CardSymbol
import com.kidplayer.app.presentation.games.memory.MemoryCardState
import com.kidplayer.app.presentation.util.bouncyClickable
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Memory card with 3D flip animation
 * Shows card back when face down, symbol when face up
 */
@Composable
fun MemoryCard(
    card: MemoryCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    // Flip animation - rotates around Y axis
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "card_flip"
    )

    // Scale animation for matched cards
    val scale by animateFloatAsState(
        targetValue = if (card.isMatched) 0.95f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "card_scale"
    )

    val isClickable = !card.isFaceUp && !card.isMatched

    // Clickable area wrapper - click detection happens here BEFORE graphicsLayer
    Box(
        modifier = modifier
            .then(
                if (isClickable) {
                    Modifier.bouncyClickable(
                        scaleOnPress = 0.95f,
                        onClick = {
                            haptic.performLight()
                            onClick()
                        }
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Visual transformations applied to inner content only
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    scaleX = scale
                    scaleY = scale
                    cameraDistance = 12f * density
                },
            contentAlignment = Alignment.Center
        ) {
        // Show back or front based on rotation
        if (rotation <= 90f) {
            CardBack(
                modifier = Modifier.fillMaxSize(),
                isMatched = card.isMatched
            )
        } else {
            CardFront(
                symbol = card.symbol,
                isMatched = card.isMatched,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f } // Counter-rotate so content isn't mirrored
            )
        }
        }
    }
}

@Composable
private fun CardBack(
    modifier: Modifier = Modifier,
    isMatched: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.cardCornerRadiusSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMatched) {
                Color.Gray.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Draw decorative pattern on card back
            Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                drawCardBackPattern()
            }
        }
    }
}

@Composable
private fun CardFront(
    symbol: CardSymbol,
    isMatched: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.cardCornerRadiusSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMatched) {
                Color(0xFFE8F5E9) // Light green for matched
            } else {
                Color.White
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                drawSymbol(symbol, isMatched)
            }
        }
    }
}

private fun DrawScope.drawCardBackPattern() {
    val color = Color.White.copy(alpha = 0.3f)
    val spacing = size.minDimension / 6

    // Draw diagonal lines pattern
    var x = -size.height
    while (x < size.width + size.height) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x + size.height, size.height),
            strokeWidth = 2.dp.toPx()
        )
        x += spacing
    }

    // Draw center circle
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = size.minDimension / 4,
        center = center
    )

    // Draw question mark
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = size.minDimension / 8,
        center = Offset(center.x, center.y - size.minDimension / 12)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        radius = size.minDimension / 20,
        center = Offset(center.x, center.y + size.minDimension / 6)
    )
}

private fun DrawScope.drawSymbol(symbol: CardSymbol, isMatched: Boolean) {
    val alpha = if (isMatched) 0.7f else 1f

    when (symbol) {
        CardSymbol.STAR -> drawStar(symbol.color.copy(alpha = alpha))
        CardSymbol.HEART -> drawHeart(symbol.color.copy(alpha = alpha))
        CardSymbol.CIRCLE -> drawCircleSymbol(symbol.color.copy(alpha = alpha))
        CardSymbol.SQUARE -> drawSquare(symbol.color.copy(alpha = alpha))
        CardSymbol.TRIANGLE -> drawTriangle(symbol.color.copy(alpha = alpha))
        CardSymbol.DIAMOND -> drawDiamond(symbol.color.copy(alpha = alpha))
        CardSymbol.MOON -> drawMoon(symbol.color.copy(alpha = alpha))
        CardSymbol.SUN -> drawSun(symbol.color.copy(alpha = alpha))
        CardSymbol.FLOWER -> drawFlower(symbol.color.copy(alpha = alpha))
        CardSymbol.BUTTERFLY -> drawButterfly(symbol.color.copy(alpha = alpha))
        CardSymbol.FISH -> drawFish(symbol.color.copy(alpha = alpha))
        CardSymbol.BIRD -> drawBird(symbol.color.copy(alpha = alpha))
    }
}

private fun DrawScope.drawStar(color: Color) {
    val path = createStarPath(center.x, center.y, size.minDimension / 2, size.minDimension / 4)
    drawPath(path, color)
}

private fun DrawScope.drawHeart(color: Color) {
    val width = size.minDimension * 0.9f
    val height = size.minDimension * 0.85f
    val path = Path().apply {
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
    drawPath(path, color)
}

private fun DrawScope.drawCircleSymbol(color: Color) {
    drawCircle(color = color, radius = size.minDimension / 2.2f)
}

private fun DrawScope.drawSquare(color: Color) {
    val squareSize = size.minDimension * 0.8f
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - squareSize / 2, center.y - squareSize / 2),
        size = Size(squareSize, squareSize),
        cornerRadius = CornerRadius(8.dp.toPx())
    )
}

private fun DrawScope.drawTriangle(color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size.minDimension / 2.2f)
        lineTo(center.x + size.minDimension / 2.2f, center.y + size.minDimension / 3)
        lineTo(center.x - size.minDimension / 2.2f, center.y + size.minDimension / 3)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawDiamond(color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size.minDimension / 2.2f)
        lineTo(center.x + size.minDimension / 2.5f, center.y)
        lineTo(center.x, center.y + size.minDimension / 2.2f)
        lineTo(center.x - size.minDimension / 2.5f, center.y)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawMoon(color: Color) {
    val radius = size.minDimension / 2.2f
    drawCircle(color = color, radius = radius, center = center)
    // Cut out a circle to create crescent
    drawCircle(
        color = Color.White,
        radius = radius * 0.8f,
        center = Offset(center.x + radius * 0.4f, center.y - radius * 0.2f)
    )
}

private fun DrawScope.drawSun(color: Color) {
    val radius = size.minDimension / 3.5f
    // Center circle
    drawCircle(color = color, radius = radius, center = center)
    // Rays
    val rayLength = radius * 0.8f
    for (i in 0 until 8) {
        val angle = i * Math.PI / 4
        val startX = center.x + ((radius + 4.dp.toPx()) * kotlin.math.cos(angle)).toFloat()
        val startY = center.y + ((radius + 4.dp.toPx()) * kotlin.math.sin(angle)).toFloat()
        val endX = center.x + ((radius + rayLength) * kotlin.math.cos(angle)).toFloat()
        val endY = center.y + ((radius + rayLength) * kotlin.math.sin(angle)).toFloat()
        drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth = 4.dp.toPx())
    }
}

private fun DrawScope.drawFlower(color: Color) {
    val petalRadius = size.minDimension / 5
    val centerRadius = size.minDimension / 6
    // Draw petals
    for (i in 0 until 6) {
        val angle = i * Math.PI / 3
        val petalX = center.x + (petalRadius * 1.3f * kotlin.math.cos(angle)).toFloat()
        val petalY = center.y + (petalRadius * 1.3f * kotlin.math.sin(angle)).toFloat()
        drawCircle(color = color, radius = petalRadius, center = Offset(petalX, petalY))
    }
    // Center
    drawCircle(color = Color(0xFFFFEB3B), radius = centerRadius, center = center)
}

private fun DrawScope.drawButterfly(color: Color) {
    val wingWidth = size.minDimension / 2.5f
    val wingHeight = size.minDimension / 3f
    // Left wing
    drawOval(
        color = color,
        topLeft = Offset(center.x - wingWidth * 1.8f, center.y - wingHeight),
        size = Size(wingWidth * 1.5f, wingHeight * 2)
    )
    // Right wing
    drawOval(
        color = color,
        topLeft = Offset(center.x + wingWidth * 0.3f, center.y - wingHeight),
        size = Size(wingWidth * 1.5f, wingHeight * 2)
    )
    // Body
    drawRoundRect(
        color = Color(0xFF5D4037),
        topLeft = Offset(center.x - 4.dp.toPx(), center.y - wingHeight),
        size = Size(8.dp.toPx(), wingHeight * 2),
        cornerRadius = CornerRadius(4.dp.toPx())
    )
}

private fun DrawScope.drawFish(color: Color) {
    val bodyWidth = size.minDimension * 0.7f
    val bodyHeight = size.minDimension * 0.45f
    // Body
    drawOval(
        color = color,
        topLeft = Offset(center.x - bodyWidth / 2, center.y - bodyHeight / 2),
        size = Size(bodyWidth, bodyHeight)
    )
    // Tail
    val tailPath = Path().apply {
        moveTo(center.x + bodyWidth / 3, center.y)
        lineTo(center.x + bodyWidth / 1.5f, center.y - bodyHeight / 2)
        lineTo(center.x + bodyWidth / 1.5f, center.y + bodyHeight / 2)
        close()
    }
    drawPath(tailPath, color)
    // Eye
    drawCircle(
        color = Color.White,
        radius = bodyHeight / 5,
        center = Offset(center.x - bodyWidth / 4, center.y - bodyHeight / 8)
    )
    drawCircle(
        color = Color.Black,
        radius = bodyHeight / 10,
        center = Offset(center.x - bodyWidth / 4, center.y - bodyHeight / 8)
    )
}

private fun DrawScope.drawBird(color: Color) {
    // Body
    drawOval(
        color = color,
        topLeft = Offset(center.x - size.minDimension / 4, center.y - size.minDimension / 6),
        size = Size(size.minDimension / 2, size.minDimension / 3)
    )
    // Head
    drawCircle(
        color = color,
        radius = size.minDimension / 6,
        center = Offset(center.x + size.minDimension / 5, center.y - size.minDimension / 6)
    )
    // Beak
    val beakPath = Path().apply {
        moveTo(center.x + size.minDimension / 3, center.y - size.minDimension / 6)
        lineTo(center.x + size.minDimension / 2, center.y - size.minDimension / 8)
        lineTo(center.x + size.minDimension / 3, center.y - size.minDimension / 12)
        close()
    }
    drawPath(beakPath, Color(0xFFFF9800))
    // Eye
    drawCircle(
        color = Color.Black,
        radius = size.minDimension / 20,
        center = Offset(center.x + size.minDimension / 4, center.y - size.minDimension / 5)
    )
    // Wing
    drawOval(
        color = color.copy(alpha = 0.7f),
        topLeft = Offset(center.x - size.minDimension / 6, center.y - size.minDimension / 10),
        size = Size(size.minDimension / 4, size.minDimension / 5)
    )
}

private fun createStarPath(cx: Float, cy: Float, outerRadius: Float, innerRadius: Float): Path {
    val path = Path()
    val angleStep = Math.PI / 5

    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = i * angleStep - Math.PI / 2
        val x = cx + (radius * kotlin.math.cos(angle)).toFloat()
        val y = cy + (radius * kotlin.math.sin(angle)).toFloat()

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    return path
}
