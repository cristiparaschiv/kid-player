package com.kidplayer.app.presentation.games.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameIconType
import com.kidplayer.app.presentation.games.common.GameInfo
import com.kidplayer.app.presentation.util.bouncyClickable
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Game selection card with Canvas-drawn icon
 * Uses bouncy animation for playful feedback
 */
@Composable
fun GameCard(
    gameInfo: GameInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .bouncyClickable(
                scaleOnPress = 0.95f,
                enabled = gameInfo.isAvailable,
                onClick = {
                    haptic.performLight()
                    onClick()
                }
            ),
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (gameInfo.isAvailable) {
                Color(gameInfo.backgroundColor)
            } else {
                Color.Gray.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Canvas-drawn game icon
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(
                    iconType = gameInfo.iconType,
                    modifier = Modifier.size(80.dp)
                )
            }

            // Game name
            Text(
                text = gameInfo.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = gameInfo.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            if (!gameInfo.isAvailable) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Canvas-drawn game icon
 */
@Composable
fun GameIcon(
    iconType: GameIconType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        when (iconType) {
            GameIconType.MEMORY -> drawMemoryIcon()
            GameIconType.TICTACTOE -> drawTicTacToeIcon()
            GameIconType.PUZZLE -> drawPuzzleIcon()
            GameIconType.MATCH3 -> drawMatch3Icon()
            GameIconType.COLORING -> drawColoringIcon()
            GameIconType.SLIDING -> drawSlidingIcon()
            GameIconType.GRIDPUZZLE -> drawGridPuzzleIcon()
            GameIconType.PATTERN -> drawPatternIcon()
            GameIconType.COLORMIX -> drawColorMixIcon()
            GameIconType.LETTERMATCH -> drawLetterMatchIcon()
            GameIconType.MAZE -> drawMazeIcon()
            GameIconType.DOTS -> drawDotsIcon()
            GameIconType.ADDITION -> drawAdditionIcon()
            GameIconType.SUBTRACTION -> drawSubtractionIcon()
            GameIconType.NUMBERBONDS -> drawNumberBondsIcon()
            GameIconType.COMPARE -> drawCompareIcon()
            GameIconType.ODDONEOUT -> drawOddOneOutIcon()
            GameIconType.SUDOKU -> drawSudokuIcon()
            GameIconType.BALLSORT -> drawBallSortIcon()
            GameIconType.HANGMAN -> drawHangmanIcon()
            GameIconType.CROSSWORD -> drawCrosswordIcon()
            GameIconType.COUNTING -> drawCountingIcon()
            GameIconType.SHAPES -> drawShapesIcon()
            GameIconType.SPELLING -> drawSpellingIcon()
            GameIconType.WORDSEARCH -> drawWordSearchIcon()
            GameIconType.SPOTDIFF -> drawSpotDiffIcon()
        }
    }
}

private fun DrawScope.drawMemoryIcon() {
    val cardWidth = size.width / 3
    val cardHeight = size.height / 3
    val spacing = 4.dp.toPx()
    val cornerRadius = 4.dp.toPx()

    // Draw 4 cards in a 2x2 grid
    val positions = listOf(
        Offset(spacing, spacing),
        Offset(size.width / 2 + spacing / 2, spacing),
        Offset(spacing, size.height / 2 + spacing / 2),
        Offset(size.width / 2 + spacing / 2, size.height / 2 + spacing / 2)
    )

    val colors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFFFFE66D), // Yellow
        Color(0xFF95E1D3)  // Mint
    )

    positions.forEachIndexed { index, pos ->
        // Card background
        drawRoundRect(
            color = colors[index],
            topLeft = pos,
            size = Size(cardWidth - spacing, cardHeight - spacing),
            cornerRadius = CornerRadius(cornerRadius)
        )

        // Question mark on card
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = cardWidth / 6,
            center = Offset(
                pos.x + (cardWidth - spacing) / 2,
                pos.y + (cardHeight - spacing) / 2
            )
        )
    }
}

private fun DrawScope.drawTicTacToeIcon() {
    val strokeWidth = 4.dp.toPx()
    val padding = 8.dp.toPx()
    val gridSize = size.width - padding * 2

    // Draw grid lines
    val thirdWidth = gridSize / 3

    // Vertical lines
    drawLine(
        color = Color.White,
        start = Offset(padding + thirdWidth, padding),
        end = Offset(padding + thirdWidth, size.height - padding),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = Color.White,
        start = Offset(padding + thirdWidth * 2, padding),
        end = Offset(padding + thirdWidth * 2, size.height - padding),
        strokeWidth = strokeWidth
    )

    // Horizontal lines
    drawLine(
        color = Color.White,
        start = Offset(padding, padding + thirdWidth),
        end = Offset(size.width - padding, padding + thirdWidth),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = Color.White,
        start = Offset(padding, padding + thirdWidth * 2),
        end = Offset(size.width - padding, padding + thirdWidth * 2),
        strokeWidth = strokeWidth
    )

    // Draw X (top-left)
    val cellPadding = 6.dp.toPx()
    val xColor = Color(0xFFFF6B6B)
    drawLine(
        color = xColor,
        start = Offset(padding + cellPadding, padding + cellPadding),
        end = Offset(padding + thirdWidth - cellPadding, padding + thirdWidth - cellPadding),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = xColor,
        start = Offset(padding + thirdWidth - cellPadding, padding + cellPadding),
        end = Offset(padding + cellPadding, padding + thirdWidth - cellPadding),
        strokeWidth = strokeWidth
    )

    // Draw O (center)
    val oColor = Color(0xFF4ECDC4)
    drawCircle(
        color = oColor,
        radius = thirdWidth / 2 - cellPadding * 1.5f,
        center = Offset(padding + thirdWidth * 1.5f, padding + thirdWidth * 1.5f),
        style = Stroke(width = strokeWidth)
    )

    // Draw X (bottom-right)
    drawLine(
        color = xColor,
        start = Offset(padding + thirdWidth * 2 + cellPadding, padding + thirdWidth * 2 + cellPadding),
        end = Offset(size.width - padding - cellPadding, size.height - padding - cellPadding),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = xColor,
        start = Offset(size.width - padding - cellPadding, padding + thirdWidth * 2 + cellPadding),
        end = Offset(padding + thirdWidth * 2 + cellPadding, size.height - padding - cellPadding),
        strokeWidth = strokeWidth
    )
}

private fun DrawScope.drawPuzzleIcon() {
    val colors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFF4ECDC4),
        Color(0xFFFFE66D),
        Color(0xFF95E1D3)
    )

    // Draw simple geometric shapes
    val shapeSize = size.width / 3
    val spacing = 8.dp.toPx()

    // Circle (top-left)
    drawCircle(
        color = colors[0],
        radius = shapeSize / 2 - spacing,
        center = Offset(shapeSize / 2 + spacing, shapeSize / 2 + spacing)
    )

    // Square (top-right)
    drawRect(
        color = colors[1],
        topLeft = Offset(size.width / 2 + spacing, spacing),
        size = Size(shapeSize - spacing * 2, shapeSize - spacing * 2)
    )

    // Triangle (bottom-left)
    val trianglePath = Path().apply {
        moveTo(shapeSize / 2 + spacing, size.height / 2 + spacing)
        lineTo(spacing, size.height - spacing)
        lineTo(shapeSize, size.height - spacing)
        close()
    }
    drawPath(trianglePath, colors[2])

    // Star (bottom-right)
    val starCenterX = size.width * 3 / 4
    val starCenterY = size.height * 3 / 4
    val starRadius = shapeSize / 2 - spacing
    val starPath = createStarPath(starCenterX, starCenterY, starRadius, starRadius * 0.4f)
    drawPath(starPath, colors[3])
}

private fun DrawScope.drawMatch3Icon() {
    val tileSize = size.width / 4
    val spacing = 3.dp.toPx()
    val cornerRadius = 4.dp.toPx()

    val colors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFFC107), // Amber
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF5722)  // Orange
    )

    // Draw a 3x3 grid of colorful tiles
    for (row in 0 until 3) {
        for (col in 0 until 3) {
            val colorIndex = (row * 3 + col) % colors.size
            drawRoundRect(
                color = colors[colorIndex],
                topLeft = Offset(
                    col * (tileSize + spacing) + spacing,
                    row * (tileSize + spacing) + spacing
                ),
                size = Size(tileSize - spacing, tileSize - spacing),
                cornerRadius = CornerRadius(cornerRadius)
            )
        }
    }
}

private fun DrawScope.drawColoringIcon() {
    // Palette
    val paletteRadius = size.width / 3
    val paletteCenter = Offset(size.width / 2, size.height * 0.6f)

    // Draw palette base (oval)
    drawOval(
        color = Color(0xFFF5DEB3), // Tan
        topLeft = Offset(size.width * 0.1f, size.height * 0.3f),
        size = Size(size.width * 0.8f, size.height * 0.6f)
    )

    // Color dots on palette
    val dotColors = listOf(
        Color(0xFFE91E63),
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFFC107),
        Color(0xFF9C27B0)
    )

    val dotRadius = size.width / 12
    dotColors.forEachIndexed { index, color ->
        val angle = Math.PI * 0.8 + index * Math.PI * 0.2
        val x = paletteCenter.x + paletteRadius * 0.5f * kotlin.math.cos(angle).toFloat()
        val y = paletteCenter.y + paletteRadius * 0.3f * kotlin.math.sin(angle).toFloat()
        drawCircle(color = color, radius = dotRadius, center = Offset(x, y))
    }

    // Brush handle
    drawLine(
        color = Color(0xFF8B4513), // Brown
        start = Offset(size.width * 0.7f, size.height * 0.1f),
        end = Offset(size.width * 0.5f, size.height * 0.4f),
        strokeWidth = 8.dp.toPx()
    )

    // Brush tip
    drawCircle(
        color = Color(0xFFE91E63),
        radius = size.width / 10,
        center = Offset(size.width * 0.5f, size.height * 0.4f)
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

private fun DrawScope.drawSlidingIcon() {
    val tileSize = size.width / 3.5f
    val spacing = 3.dp.toPx()
    val cornerRadius = 4.dp.toPx()
    val startX = (size.width - tileSize * 3 - spacing * 2) / 2
    val startY = (size.height - tileSize * 3 - spacing * 2) / 2

    // Draw a 3x3 grid with numbers (one empty)
    val colors = listOf(
        Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFB74D),
        Color(0xFFBA68C8), Color(0xFF4DD0E1), Color(0xFFF06292),
        Color(0xFFAED581), Color(0xFFFFD54F)
    )

    var colorIndex = 0
    for (row in 0 until 3) {
        for (col in 0 until 3) {
            // Skip bottom-right (empty space)
            if (row == 2 && col == 2) {
                // Draw empty space indicator
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(
                        startX + col * (tileSize + spacing),
                        startY + row * (tileSize + spacing)
                    ),
                    size = Size(tileSize, tileSize),
                    cornerRadius = CornerRadius(cornerRadius),
                    style = Stroke(width = 2.dp.toPx())
                )
                continue
            }

            drawRoundRect(
                color = colors[colorIndex % colors.size],
                topLeft = Offset(
                    startX + col * (tileSize + spacing),
                    startY + row * (tileSize + spacing)
                ),
                size = Size(tileSize, tileSize),
                cornerRadius = CornerRadius(cornerRadius)
            )
            colorIndex++
        }
    }

    // Draw arrows to indicate sliding
    val arrowColor = Color.White.copy(alpha = 0.8f)
    val arrowSize = tileSize / 4
    val arrowX = startX + 2 * (tileSize + spacing) + tileSize / 2
    val arrowY = startY + (tileSize + spacing) + tileSize / 2

    // Left arrow
    drawLine(
        color = arrowColor,
        start = Offset(arrowX - arrowSize, arrowY),
        end = Offset(arrowX + arrowSize / 2, arrowY),
        strokeWidth = 3.dp.toPx()
    )
    drawLine(
        color = arrowColor,
        start = Offset(arrowX - arrowSize, arrowY),
        end = Offset(arrowX - arrowSize / 2, arrowY - arrowSize / 2),
        strokeWidth = 3.dp.toPx()
    )
    drawLine(
        color = arrowColor,
        start = Offset(arrowX - arrowSize, arrowY),
        end = Offset(arrowX - arrowSize / 2, arrowY + arrowSize / 2),
        strokeWidth = 3.dp.toPx()
    )
}

private fun DrawScope.drawGridPuzzleIcon() {
    val tileSize = size.width / 3.2f
    val spacing = 2.dp.toPx()
    val cornerRadius = 3.dp.toPx()
    val startX = (size.width - tileSize * 3 - spacing * 2) / 2
    val startY = (size.height - tileSize * 3 - spacing * 2) / 2

    // Draw a 3x3 grid representing picture pieces
    // Use gradient-like colors to simulate an image
    val colors = listOf(
        listOf(Color(0xFF1976D2), Color(0xFF2196F3), Color(0xFF64B5F6)),
        listOf(Color(0xFF388E3C), Color(0xFF4CAF50), Color(0xFF81C784)),
        listOf(Color(0xFFF57C00), Color(0xFFFF9800), Color(0xFFFFB74D))
    )

    for (row in 0 until 3) {
        for (col in 0 until 3) {
            drawRoundRect(
                color = colors[row][col],
                topLeft = Offset(
                    startX + col * (tileSize + spacing),
                    startY + row * (tileSize + spacing)
                ),
                size = Size(tileSize, tileSize),
                cornerRadius = CornerRadius(cornerRadius)
            )

            // Add small image indicator (mountain/sun pattern)
            if (row == 0 && col == 1) {
                // Sun
                drawCircle(
                    color = Color(0xFFFFEB3B),
                    radius = tileSize / 5,
                    center = Offset(
                        startX + col * (tileSize + spacing) + tileSize / 2,
                        startY + row * (tileSize + spacing) + tileSize / 2
                    )
                )
            }
        }
    }

    // Draw swap indicator arrows between two tiles
    val swapY = startY + tileSize + spacing / 2
    val swapX1 = startX + tileSize / 2
    val swapX2 = startX + tileSize + spacing + tileSize / 2
    val arrowColor = Color.White

    // Curved double arrow
    drawLine(
        color = arrowColor,
        start = Offset(swapX1 + tileSize / 4, swapY + tileSize / 2),
        end = Offset(swapX2 - tileSize / 4, swapY + tileSize / 2),
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawPatternIcon() {
    val shapeSize = size.width / 4
    val spacing = 8.dp.toPx()
    val y = size.height / 2

    val colors = listOf(
        Color(0xFFE91E63),
        Color(0xFF2196F3),
        Color(0xFFE91E63),
        Color(0xFF9E9E9E)
    )

    // Draw pattern: circle, square, circle, ?
    for (i in 0 until 4) {
        val x = spacing + i * (shapeSize + spacing / 2)
        when (i) {
            0, 2 -> drawCircle(
                color = colors[i],
                radius = shapeSize / 2.5f,
                center = Offset(x + shapeSize / 2, y)
            )
            1 -> drawRect(
                color = colors[i],
                topLeft = Offset(x + shapeSize / 6, y - shapeSize / 3),
                size = Size(shapeSize * 0.7f, shapeSize * 0.7f)
            )
            3 -> {
                // Question mark
                drawCircle(
                    color = colors[i].copy(alpha = 0.5f),
                    radius = shapeSize / 2.5f,
                    center = Offset(x + shapeSize / 2, y),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

private fun DrawScope.drawColorMixIcon() {
    val blobRadius = size.width / 4

    // Red blob
    drawCircle(
        color = Color(0xFFE53935),
        radius = blobRadius,
        center = Offset(size.width * 0.3f, size.height * 0.4f)
    )

    // Yellow blob
    drawCircle(
        color = Color(0xFFFFEB3B),
        radius = blobRadius,
        center = Offset(size.width * 0.7f, size.height * 0.4f)
    )

    // Orange result (overlapping)
    drawCircle(
        color = Color(0xFFFF9800),
        radius = blobRadius * 0.8f,
        center = Offset(size.width * 0.5f, size.height * 0.65f)
    )
}

private fun DrawScope.drawLetterMatchIcon() {
    val letterSize = size.width / 3

    // Draw "A"
    drawRoundRect(
        color = Color(0xFF2196F3),
        topLeft = Offset(size.width * 0.1f, size.height * 0.2f),
        size = Size(letterSize, letterSize),
        cornerRadius = CornerRadius(8.dp.toPx())
    )

    // Draw apple emoji representation (red circle with stem)
    drawCircle(
        color = Color(0xFFE53935),
        radius = letterSize / 2.5f,
        center = Offset(size.width * 0.7f, size.height * 0.4f)
    )

    // Arrow between
    drawLine(
        color = Color.White,
        start = Offset(size.width * 0.4f, size.height * 0.5f),
        end = Offset(size.width * 0.55f, size.height * 0.5f),
        strokeWidth = 3.dp.toPx()
    )
}

private fun DrawScope.drawMazeIcon() {
    val wallColor = Color(0xFF2D2D44)
    val pathColor = Color(0xFFF5F5F5)
    val wallWidth = 3.dp.toPx()

    // Draw background
    drawRect(pathColor)

    // Draw simple maze walls
    val lines = listOf(
        // Outer walls
        Pair(Offset(0f, 0f), Offset(size.width, 0f)),
        Pair(Offset(0f, 0f), Offset(0f, size.height)),
        Pair(Offset(size.width, 0f), Offset(size.width, size.height)),
        Pair(Offset(0f, size.height), Offset(size.width, size.height)),
        // Inner walls
        Pair(Offset(size.width * 0.3f, 0f), Offset(size.width * 0.3f, size.height * 0.6f)),
        Pair(Offset(size.width * 0.6f, size.height * 0.4f), Offset(size.width * 0.6f, size.height)),
        Pair(Offset(0f, size.height * 0.5f), Offset(size.width * 0.15f, size.height * 0.5f)),
    )

    lines.forEach { (start, end) ->
        drawLine(wallColor, start, end, wallWidth)
    }

    // Player dot
    drawCircle(
        color = Color(0xFF4CAF50),
        radius = size.width / 10,
        center = Offset(size.width * 0.15f, size.height * 0.15f)
    )

    // Goal
    drawCircle(
        color = Color(0xFFFF9800),
        radius = size.width / 10,
        center = Offset(size.width * 0.85f, size.height * 0.85f)
    )
}

private fun DrawScope.drawDotsIcon() {
    val dotRadius = size.width / 12
    val lineColor = Color(0xFF2196F3)

    // Dot positions (star shape)
    val dots = listOf(
        Offset(size.width * 0.5f, size.height * 0.15f),
        Offset(size.width * 0.75f, size.height * 0.4f),
        Offset(size.width * 0.65f, size.height * 0.75f),
        Offset(size.width * 0.35f, size.height * 0.75f),
        Offset(size.width * 0.25f, size.height * 0.4f)
    )

    // Draw connecting lines
    for (i in 0 until dots.size - 1) {
        drawLine(
            color = lineColor,
            start = dots[i],
            end = dots[i + 1],
            strokeWidth = 3.dp.toPx()
        )
    }

    // Draw dots with numbers
    dots.forEachIndexed { index, offset ->
        drawCircle(
            color = if (index < 3) lineColor else Color.Gray,
            radius = dotRadius,
            center = offset
        )
        drawCircle(
            color = Color.White,
            radius = dotRadius * 0.6f,
            center = offset
        )
    }
}

private fun DrawScope.drawAdditionIcon() {
    val circleRadius = size.width / 6
    val circleColor = Color(0xFFE8F5E9)

    // Draw apple circles (2 + 3 = ?)
    // Left group (2)
    drawCircle(color = Color(0xFFE53935), radius = circleRadius, center = Offset(size.width * 0.15f, size.height * 0.35f))
    drawCircle(color = Color(0xFFE53935), radius = circleRadius, center = Offset(size.width * 0.35f, size.height * 0.35f))

    // Plus sign
    val plusStroke = 4.dp.toPx()
    drawLine(Color.White, Offset(size.width * 0.5f - 12.dp.toPx(), size.height * 0.35f), Offset(size.width * 0.5f + 12.dp.toPx(), size.height * 0.35f), plusStroke)
    drawLine(Color.White, Offset(size.width * 0.5f, size.height * 0.35f - 12.dp.toPx()), Offset(size.width * 0.5f, size.height * 0.35f + 12.dp.toPx()), plusStroke)

    // Right group (3)
    drawCircle(color = Color(0xFFE53935), radius = circleRadius, center = Offset(size.width * 0.65f, size.height * 0.35f))
    drawCircle(color = Color(0xFFE53935), radius = circleRadius, center = Offset(size.width * 0.85f, size.height * 0.35f))
    drawCircle(color = Color(0xFFE53935), radius = circleRadius, center = Offset(size.width * 0.75f, size.height * 0.55f))

    // Equals and question mark
    drawLine(Color.White, Offset(size.width * 0.3f, size.height * 0.75f), Offset(size.width * 0.5f, size.height * 0.75f), plusStroke)
    drawLine(Color.White, Offset(size.width * 0.3f, size.height * 0.85f), Offset(size.width * 0.5f, size.height * 0.85f), plusStroke)

    drawCircle(color = Color.White.copy(alpha = 0.5f), radius = circleRadius * 1.2f, center = Offset(size.width * 0.7f, size.height * 0.8f), style = Stroke(width = 3.dp.toPx()))
}

private fun DrawScope.drawSubtractionIcon() {
    val animalRadius = size.width / 7

    // Draw animals (5 animals, 2 crossed out)
    val positions = listOf(
        Offset(size.width * 0.15f, size.height * 0.4f),
        Offset(size.width * 0.35f, size.height * 0.4f),
        Offset(size.width * 0.55f, size.height * 0.4f),
        Offset(size.width * 0.75f, size.height * 0.4f),
        Offset(size.width * 0.45f, size.height * 0.65f)
    )

    positions.forEachIndexed { index, pos ->
        val isCrossed = index >= 3
        val alpha = if (isCrossed) 0.4f else 1f

        // Animal circle
        drawCircle(
            color = Color(0xFFFFB74D).copy(alpha = alpha),
            radius = animalRadius,
            center = pos
        )

        // Cross out the last two
        if (isCrossed) {
            val crossSize = animalRadius * 0.7f
            drawLine(
                Color(0xFFE53935),
                Offset(pos.x - crossSize, pos.y - crossSize),
                Offset(pos.x + crossSize, pos.y + crossSize),
                4.dp.toPx()
            )
            drawLine(
                Color(0xFFE53935),
                Offset(pos.x + crossSize, pos.y - crossSize),
                Offset(pos.x - crossSize, pos.y + crossSize),
                4.dp.toPx()
            )
        }
    }

    // Minus sign at bottom
    drawLine(Color.White, Offset(size.width * 0.3f, size.height * 0.85f), Offset(size.width * 0.7f, size.height * 0.85f), 4.dp.toPx())
}

private fun DrawScope.drawNumberBondsIcon() {
    val topRadius = size.width / 5
    val bottomRadius = size.width / 6.5f

    // Top circle (target number)
    drawCircle(
        color = Color(0xFF2196F3),
        radius = topRadius,
        center = Offset(size.width * 0.5f, size.height * 0.25f)
    )

    // Connecting lines
    drawLine(
        Color.White.copy(alpha = 0.7f),
        Offset(size.width * 0.5f, size.height * 0.25f + topRadius),
        Offset(size.width * 0.3f, size.height * 0.7f - bottomRadius),
        3.dp.toPx()
    )
    drawLine(
        Color.White.copy(alpha = 0.7f),
        Offset(size.width * 0.5f, size.height * 0.25f + topRadius),
        Offset(size.width * 0.7f, size.height * 0.7f - bottomRadius),
        3.dp.toPx()
    )

    // Bottom circles (parts)
    drawCircle(
        color = Color.White,
        radius = bottomRadius,
        center = Offset(size.width * 0.3f, size.height * 0.7f)
    )
    drawCircle(
        color = Color.White,
        radius = bottomRadius,
        center = Offset(size.width * 0.7f, size.height * 0.7f)
    )

    // Plus sign in middle
    drawCircle(
        color = Color(0xFF4CAF50),
        radius = bottomRadius * 0.5f,
        center = Offset(size.width * 0.5f, size.height * 0.7f)
    )
}

private fun DrawScope.drawCompareIcon() {
    val groupRadius = size.width / 10

    // Left group (3 items)
    for (i in 0 until 3) {
        drawCircle(
            color = Color(0xFF4CAF50),
            radius = groupRadius,
            center = Offset(size.width * 0.15f + i * groupRadius * 2.2f, size.height * 0.35f)
        )
    }

    // Right group (5 items in 2 rows)
    for (i in 0 until 3) {
        drawCircle(
            color = Color(0xFF2196F3),
            radius = groupRadius,
            center = Offset(size.width * 0.55f + i * groupRadius * 2.2f, size.height * 0.25f)
        )
    }
    for (i in 0 until 2) {
        drawCircle(
            color = Color(0xFF2196F3),
            radius = groupRadius,
            center = Offset(size.width * 0.65f + i * groupRadius * 2.2f, size.height * 0.45f)
        )
    }

    // Greater than / Less than symbol
    val symbolX = size.width * 0.5f
    val symbolY = size.height * 0.75f
    val symbolSize = size.width / 6

    // Draw < symbol
    drawLine(Color.White, Offset(symbolX + symbolSize, symbolY - symbolSize * 0.6f), Offset(symbolX - symbolSize * 0.5f, symbolY), 5.dp.toPx())
    drawLine(Color.White, Offset(symbolX - symbolSize * 0.5f, symbolY), Offset(symbolX + symbolSize, symbolY + symbolSize * 0.6f), 5.dp.toPx())
}

private fun DrawScope.drawOddOneOutIcon() {
    val itemRadius = size.width / 8

    // Draw 4 items - 3 same (circles), 1 different (square)
    val positions = listOf(
        Offset(size.width * 0.25f, size.height * 0.35f),
        Offset(size.width * 0.75f, size.height * 0.35f),
        Offset(size.width * 0.25f, size.height * 0.65f),
        Offset(size.width * 0.75f, size.height * 0.65f)
    )

    positions.forEachIndexed { index, pos ->
        if (index == 2) {
            // Odd one - square
            drawRect(
                color = Color(0xFFE53935),
                topLeft = Offset(pos.x - itemRadius, pos.y - itemRadius),
                size = Size(itemRadius * 2, itemRadius * 2)
            )
            // Highlight ring
            drawCircle(
                color = Color(0xFFFFEB3B),
                radius = itemRadius * 1.5f,
                center = pos,
                style = Stroke(width = 3.dp.toPx())
            )
        } else {
            // Same - circles
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = itemRadius,
                center = pos
            )
        }
    }

    // Magnifying glass
    val glassX = size.width * 0.85f
    val glassY = size.height * 0.15f
    drawCircle(Color.White, radius = size.width / 12, center = Offset(glassX, glassY), style = Stroke(width = 3.dp.toPx()))
    drawLine(Color.White, Offset(glassX + size.width / 15, glassY + size.width / 15), Offset(glassX + size.width / 8, glassY + size.width / 8), 3.dp.toPx())
}

private fun DrawScope.drawSudokuIcon() {
    val gridSize = size.width * 0.9f
    val cellSize = gridSize / 4
    val startX = (size.width - gridSize) / 2
    val startY = (size.height - gridSize) / 2
    val cornerRadius = 4.dp.toPx()

    // Background
    drawRoundRect(
        color = Color(0xFF333333),
        topLeft = Offset(startX - 2.dp.toPx(), startY - 2.dp.toPx()),
        size = Size(gridSize + 4.dp.toPx(), gridSize + 4.dp.toPx()),
        cornerRadius = CornerRadius(cornerRadius)
    )

    // Draw 4x4 grid cells
    val colors = listOf(
        Color(0xFFE8F5E9), Color(0xFFE3F2FD), Color(0xFFFCE4EC), Color(0xFFFFF3E0),
        Color(0xFFFFF3E0), Color(0xFFFCE4EC), Color(0xFFE8F5E9), Color(0xFFE3F2FD),
        Color(0xFFE3F2FD), Color(0xFFE8F5E9), Color(0xFFFFF3E0), Color(0xFFFCE4EC),
        Color(0xFFFCE4EC), Color(0xFFFFF3E0), Color(0xFFE3F2FD), Color(0xFFE8F5E9)
    )

    for (row in 0 until 4) {
        for (col in 0 until 4) {
            val cellX = startX + col * cellSize + 1.dp.toPx()
            val cellY = startY + row * cellSize + 1.dp.toPx()
            val actualCellSize = cellSize - 2.dp.toPx()

            drawRoundRect(
                color = colors[row * 4 + col],
                topLeft = Offset(cellX, cellY),
                size = Size(actualCellSize, actualCellSize),
                cornerRadius = CornerRadius(2.dp.toPx())
            )

            // Draw some emoji-like dots in cells
            if ((row + col) % 2 == 0) {
                drawCircle(
                    color = Color(0xFF9C27B0).copy(alpha = 0.6f),
                    radius = cellSize / 5,
                    center = Offset(cellX + actualCellSize / 2, cellY + actualCellSize / 2)
                )
            }
        }
    }

    // Thicker lines for 2x2 box boundaries
    val midX = startX + gridSize / 2
    val midY = startY + gridSize / 2
    drawLine(Color(0xFF333333), Offset(midX, startY), Offset(midX, startY + gridSize), 3.dp.toPx())
    drawLine(Color(0xFF333333), Offset(startX, midY), Offset(startX + gridSize, midY), 3.dp.toPx())
}

private fun DrawScope.drawBallSortIcon() {
    val tubeWidth = size.width / 4.5f
    val tubeHeight = size.height * 0.7f
    val ballRadius = tubeWidth / 2.5f
    val tubeSpacing = (size.width - tubeWidth * 3) / 4
    val startY = size.height * 0.15f

    val ballColors = listOf(
        listOf(Color(0xFFE53935), Color(0xFF4CAF50), Color(0xFF2196F3)),
        listOf(Color(0xFF2196F3), Color(0xFFE53935), Color(0xFF4CAF50)),
        listOf(Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFE53935))
    )

    for (tube in 0 until 3) {
        val tubeX = tubeSpacing + tube * (tubeWidth + tubeSpacing)

        // Draw tube outline
        drawRoundRect(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = Offset(tubeX, startY),
            size = Size(tubeWidth, tubeHeight),
            cornerRadius = CornerRadius(tubeWidth / 2),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw balls in tube
        for (ball in 0 until 3) {
            val ballY = startY + tubeHeight - (ball + 1) * (ballRadius * 2) - ballRadius / 2
            drawCircle(
                color = ballColors[tube][ball],
                radius = ballRadius,
                center = Offset(tubeX + tubeWidth / 2, ballY)
            )
        }
    }
}

private fun DrawScope.drawHangmanIcon() {
    val strokeWidth = 3.dp.toPx()
    val gallowsColor = Color(0xFFD7CCC8)
    val figureColor = Color.White

    // Gallows base
    drawLine(gallowsColor, Offset(size.width * 0.15f, size.height * 0.9f), Offset(size.width * 0.85f, size.height * 0.9f), strokeWidth)

    // Gallows pole
    drawLine(gallowsColor, Offset(size.width * 0.3f, size.height * 0.9f), Offset(size.width * 0.3f, size.height * 0.1f), strokeWidth)

    // Gallows top
    drawLine(gallowsColor, Offset(size.width * 0.3f, size.height * 0.1f), Offset(size.width * 0.7f, size.height * 0.1f), strokeWidth)

    // Rope
    drawLine(gallowsColor, Offset(size.width * 0.7f, size.height * 0.1f), Offset(size.width * 0.7f, size.height * 0.2f), strokeWidth * 0.7f)

    // Head
    val headRadius = size.width * 0.1f
    val headCenterY = size.height * 0.2f + headRadius
    drawCircle(figureColor, radius = headRadius, center = Offset(size.width * 0.7f, headCenterY), style = Stroke(width = strokeWidth))

    // Body
    drawLine(figureColor, Offset(size.width * 0.7f, headCenterY + headRadius), Offset(size.width * 0.7f, size.height * 0.6f), strokeWidth)

    // Arms
    drawLine(figureColor, Offset(size.width * 0.7f, size.height * 0.4f), Offset(size.width * 0.55f, size.height * 0.5f), strokeWidth)
    drawLine(figureColor, Offset(size.width * 0.7f, size.height * 0.4f), Offset(size.width * 0.85f, size.height * 0.5f), strokeWidth)

    // Legs
    drawLine(figureColor, Offset(size.width * 0.7f, size.height * 0.6f), Offset(size.width * 0.55f, size.height * 0.8f), strokeWidth)
    drawLine(figureColor, Offset(size.width * 0.7f, size.height * 0.6f), Offset(size.width * 0.85f, size.height * 0.8f), strokeWidth)
}

private fun DrawScope.drawCrosswordIcon() {
    val gridSize = size.width * 0.85f
    val cellSize = gridSize / 5
    val startX = (size.width - gridSize) / 2
    val startY = (size.height - gridSize) / 2

    // Draw a 5x5 crossword pattern
    val filled = listOf(
        listOf(true, true, true, false, false),
        listOf(false, true, false, false, false),
        listOf(true, true, true, true, true),
        listOf(false, true, false, false, true),
        listOf(false, true, false, false, true)
    )

    for (row in 0 until 5) {
        for (col in 0 until 5) {
            if (filled[row][col]) {
                val cellX = startX + col * cellSize
                val cellY = startY + row * cellSize

                // Draw white cell
                drawRect(
                    color = Color.White,
                    topLeft = Offset(cellX, cellY),
                    size = Size(cellSize - 2.dp.toPx(), cellSize - 2.dp.toPx())
                )

                // Draw border
                drawRect(
                    color = Color(0xFF333333),
                    topLeft = Offset(cellX, cellY),
                    size = Size(cellSize - 2.dp.toPx(), cellSize - 2.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }

    // Add some letters
    val letterPositions = listOf(
        Triple(0, 0, Color(0xFF4CAF50)),
        Triple(0, 1, Color(0xFF2196F3)),
        Triple(2, 0, Color(0xFFE53935)),
        Triple(2, 2, Color(0xFFFF9800))
    )

    letterPositions.forEach { (row, col, color) ->
        val cellX = startX + col * cellSize + cellSize / 2
        val cellY = startY + row * cellSize + cellSize / 2
        drawCircle(
            color = color.copy(alpha = 0.6f),
            radius = cellSize / 4,
            center = Offset(cellX, cellY)
        )
    }
}

private fun DrawScope.drawCountingIcon() {
    val itemRadius = size.width / 10

    // Draw scattered objects to count
    val positions = listOf(
        Offset(size.width * 0.2f, size.height * 0.25f),
        Offset(size.width * 0.5f, size.height * 0.2f),
        Offset(size.width * 0.8f, size.height * 0.3f),
        Offset(size.width * 0.3f, size.height * 0.5f),
        Offset(size.width * 0.7f, size.height * 0.5f)
    )

    positions.forEach { pos ->
        // Draw apple-like circle
        drawCircle(
            color = Color(0xFFE53935),
            radius = itemRadius,
            center = pos
        )
        // Stem
        drawLine(
            color = Color(0xFF8B4513),
            start = Offset(pos.x, pos.y - itemRadius),
            end = Offset(pos.x + itemRadius * 0.3f, pos.y - itemRadius * 1.4f),
            strokeWidth = 3.dp.toPx()
        )
    }

    // Question mark with number
    drawCircle(
        color = Color.White,
        radius = size.width / 5,
        center = Offset(size.width * 0.5f, size.height * 0.8f)
    )
    drawCircle(
        color = Color(0xFF66BB6A),
        radius = size.width / 5.5f,
        center = Offset(size.width * 0.5f, size.height * 0.8f)
    )
}

private fun DrawScope.drawShapesIcon() {
    val shapeSize = size.width / 3.5f

    // Circle
    drawCircle(
        color = Color(0xFFE91E63),
        radius = shapeSize / 2,
        center = Offset(size.width * 0.25f, size.height * 0.3f)
    )

    // Square
    drawRect(
        color = Color(0xFF2196F3),
        topLeft = Offset(size.width * 0.55f, size.height * 0.15f),
        size = Size(shapeSize, shapeSize)
    )

    // Triangle
    val trianglePath = Path().apply {
        moveTo(size.width * 0.25f, size.height * 0.55f)
        lineTo(size.width * 0.1f, size.height * 0.85f)
        lineTo(size.width * 0.4f, size.height * 0.85f)
        close()
    }
    drawPath(trianglePath, Color(0xFF4CAF50))

    // Star
    val starPath = createStarPath(
        cx = size.width * 0.72f,
        cy = size.height * 0.7f,
        outerRadius = shapeSize / 2,
        innerRadius = shapeSize / 4
    )
    drawPath(starPath, Color(0xFFFFB300))
}

private fun DrawScope.drawSpellingIcon() {
    val tileSize = size.width / 4.5f
    val spacing = 4.dp.toPx()
    val startY = size.height * 0.3f

    // Letter tiles
    val letters = listOf("C", "A", "T")
    val colors = listOf(Color(0xFF66BB6A), Color(0xFF42A5F5), Color(0xFFFFB74D))

    letters.forEachIndexed { index, _ ->
        val x = spacing + index * (tileSize + spacing) + size.width * 0.1f

        // Tile background
        drawRoundRect(
            color = colors[index],
            topLeft = Offset(x, startY),
            size = Size(tileSize, tileSize),
            cornerRadius = CornerRadius(6.dp.toPx())
        )

        // White inner area
        drawRoundRect(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = Offset(x + 4.dp.toPx(), startY + 4.dp.toPx()),
            size = Size(tileSize - 8.dp.toPx(), tileSize - 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx())
        )
    }

    // Cat emoji representation (simple circle with ears)
    val emojiY = size.height * 0.75f
    val emojiRadius = size.width / 6

    drawCircle(
        color = Color(0xFFFFB74D),
        radius = emojiRadius,
        center = Offset(size.width / 2, emojiY)
    )

    // Ears
    val earPath = Path().apply {
        moveTo(size.width / 2 - emojiRadius * 0.8f, emojiY - emojiRadius * 0.5f)
        lineTo(size.width / 2 - emojiRadius * 0.5f, emojiY - emojiRadius * 1.1f)
        lineTo(size.width / 2 - emojiRadius * 0.2f, emojiY - emojiRadius * 0.5f)
        close()
    }
    drawPath(earPath, Color(0xFFFFB74D))

    val earPath2 = Path().apply {
        moveTo(size.width / 2 + emojiRadius * 0.8f, emojiY - emojiRadius * 0.5f)
        lineTo(size.width / 2 + emojiRadius * 0.5f, emojiY - emojiRadius * 1.1f)
        lineTo(size.width / 2 + emojiRadius * 0.2f, emojiY - emojiRadius * 0.5f)
        close()
    }
    drawPath(earPath2, Color(0xFFFFB74D))
}

private fun DrawScope.drawWordSearchIcon() {
    val gridSize = size.width * 0.85f
    val cellSize = gridSize / 5
    val startX = (size.width - gridSize) / 2
    val startY = (size.height - gridSize) / 2

    // Draw grid background
    drawRoundRect(
        color = Color(0xFF29B6F6).copy(alpha = 0.3f),
        topLeft = Offset(startX, startY),
        size = Size(gridSize, gridSize),
        cornerRadius = CornerRadius(8.dp.toPx())
    )

    // Draw letter cells
    for (row in 0 until 5) {
        for (col in 0 until 5) {
            val cellX = startX + col * cellSize
            val cellY = startY + row * cellSize

            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(cellX + 2.dp.toPx(), cellY + 2.dp.toPx()),
                size = Size(cellSize - 4.dp.toPx(), cellSize - 4.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }

    // Highlight a found word (horizontal)
    drawRoundRect(
        color = Color(0xFF4CAF50).copy(alpha = 0.5f),
        topLeft = Offset(startX + cellSize, startY + 2 * cellSize),
        size = Size(cellSize * 3, cellSize),
        cornerRadius = CornerRadius(cellSize / 2)
    )

    // Highlight another word (vertical)
    drawRoundRect(
        color = Color(0xFFE91E63).copy(alpha = 0.5f),
        topLeft = Offset(startX + 3 * cellSize, startY),
        size = Size(cellSize, cellSize * 3),
        cornerRadius = CornerRadius(cellSize / 2)
    )
}

private fun DrawScope.drawSpotDiffIcon() {
    val pictureWidth = size.width * 0.4f
    val pictureHeight = size.height * 0.35f
    val cornerRadius = 8.dp.toPx()

    // Left picture
    drawRoundRect(
        color = Color(0xFFE3F2FD),
        topLeft = Offset(size.width * 0.05f, size.height * 0.1f),
        size = Size(pictureWidth, pictureHeight),
        cornerRadius = CornerRadius(cornerRadius)
    )

    // Right picture
    drawRoundRect(
        color = Color(0xFFE3F2FD),
        topLeft = Offset(size.width * 0.55f, size.height * 0.1f),
        size = Size(pictureWidth, pictureHeight),
        cornerRadius = CornerRadius(cornerRadius)
    )

    // Content in left picture (sun and tree)
    drawCircle(
        color = Color(0xFFFFEB3B),
        radius = size.width / 12,
        center = Offset(size.width * 0.15f, size.height * 0.2f)
    )
    drawCircle(
        color = Color(0xFF4CAF50),
        radius = size.width / 10,
        center = Offset(size.width * 0.35f, size.height * 0.3f)
    )

    // Content in right picture (sun and different tree)
    drawCircle(
        color = Color(0xFFFFEB3B),
        radius = size.width / 12,
        center = Offset(size.width * 0.65f, size.height * 0.2f)
    )
    drawCircle(
        color = Color(0xFFE53935), // Different color - this is a difference!
        radius = size.width / 10,
        center = Offset(size.width * 0.85f, size.height * 0.3f)
    )

    // Magnifying glass with question marks
    val glassX = size.width * 0.5f
    val glassY = size.height * 0.7f
    val glassRadius = size.width / 6

    drawCircle(
        color = Color.White,
        radius = glassRadius,
        center = Offset(glassX, glassY)
    )
    drawCircle(
        color = Color(0xFF8E24AA),
        radius = glassRadius,
        center = Offset(glassX, glassY),
        style = Stroke(width = 4.dp.toPx())
    )

    // Handle
    drawLine(
        color = Color(0xFF8E24AA),
        start = Offset(glassX + glassRadius * 0.7f, glassY + glassRadius * 0.7f),
        end = Offset(glassX + glassRadius * 1.3f, glassY + glassRadius * 1.3f),
        strokeWidth = 6.dp.toPx()
    )

    // Red circle on difference
    drawCircle(
        color = Color(0xFFE53935),
        radius = size.width / 8,
        center = Offset(size.width * 0.85f, size.height * 0.3f),
        style = Stroke(width = 3.dp.toPx())
    )
}
