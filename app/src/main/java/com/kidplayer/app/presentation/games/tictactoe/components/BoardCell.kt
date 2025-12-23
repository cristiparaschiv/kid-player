package com.kidplayer.app.presentation.games.tictactoe.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.tictactoe.CellState
import com.kidplayer.app.presentation.util.bouncyClickable
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Individual cell in the Tic-Tac-Toe board
 * Displays X, O, or empty with animations
 */
@Composable
fun BoardCell(
    state: CellState,
    isWinningCell: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    // Animation for drawing X or O
    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "symbol_draw"
    )

    // Trigger animation when state changes
    LaunchedEffect(state) {
        targetProgress = if (state != CellState.EMPTY) 1f else 0f
    }

    // Scale animation for winning cells
    val scale by animateFloatAsState(
        targetValue = if (isWinningCell) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "winning_scale"
    )

    val isClickable = state == CellState.EMPTY

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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
        shape = RoundedCornerShape(Dimensions.cardCornerRadiusSmall),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isWinningCell -> Color(0xFFE8F5E9) // Light green for winning
                state == CellState.EMPTY -> Color.White
                else -> Color(0xFFFAFAFA)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = size.minDimension / 8
                val padding = size.minDimension * 0.15f

                when (state) {
                    CellState.X -> {
                        val xColor = if (isWinningCell) Color(0xFF1B5E20) else Color(0xFF2196F3)

                        // Draw X with animation
                        // First line: top-left to bottom-right
                        val line1End = Offset(
                            padding + (size.width - 2 * padding) * animatedProgress,
                            padding + (size.height - 2 * padding) * animatedProgress
                        )
                        drawLine(
                            color = xColor,
                            start = Offset(padding, padding),
                            end = line1End,
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )

                        // Second line: top-right to bottom-left (starts after first is half done)
                        if (animatedProgress > 0.5f) {
                            val secondProgress = (animatedProgress - 0.5f) * 2
                            val line2End = Offset(
                                size.width - padding - (size.width - 2 * padding) * secondProgress,
                                padding + (size.height - 2 * padding) * secondProgress
                            )
                            drawLine(
                                color = xColor,
                                start = Offset(size.width - padding, padding),
                                end = line2End,
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                    CellState.O -> {
                        val oColor = if (isWinningCell) Color(0xFF1B5E20) else Color(0xFFE91E63)

                        // Draw O with animation (sweep angle based on progress)
                        val radius = (size.minDimension - 2 * padding) / 2
                        drawArc(
                            color = oColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    CellState.EMPTY -> {
                        // Draw nothing
                    }
                }
            }
        }
    }
}
