package com.kidplayer.app.presentation.games.dots

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun DotsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DotsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Connect Dots",
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
            // Puzzle info
            uiState.currentPuzzle?.let { puzzle ->
                PuzzleInfo(
                    puzzleNumber = uiState.puzzleIndex + 1,
                    totalPuzzles = DotsConfig.TOTAL_PUZZLES,
                    nextDot = uiState.currentDotNumber,
                    totalDots = puzzle.dots.size
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dots canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                uiState.currentPuzzle?.let { puzzle ->
                    DotsCanvas(
                        puzzle = puzzle,
                        connectedDots = uiState.connectedDots,
                        currentDotNumber = uiState.currentDotNumber,
                        showingShape = uiState.showingShape,
                        onDotTap = { dotNumber ->
                            haptic.performLight()
                            viewModel.onDotTap(dotNumber)
                        }
                    )

                    // Show emoji when complete
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.showingShape,
                        enter = scaleIn() + fadeIn()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = puzzle.emoji,
                                fontSize = 80.sp
                            )
                            Text(
                                text = "It's a ${puzzle.name}!",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = puzzle.color
                            )
                        }
                    }
                }
            }

            // Hint
            if (!uiState.puzzleComplete) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "Tap the dots in order: 1, 2, 3...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PuzzleInfo(
    puzzleNumber: Int,
    totalPuzzles: Int,
    nextDot: Int,
    totalDots: Int
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
                    text = "Picture",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$puzzleNumber/$totalPuzzles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Next Dot",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (nextDot <= totalDots) "$nextDot" else "Done!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun DotsCanvas(
    puzzle: DotPuzzle,
    connectedDots: List<Int>,
    currentDotNumber: Int,
    showingShape: Boolean,
    onDotTap: (Int) -> Unit
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val padding = with(density) { 24.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White.copy(alpha = if (showingShape) 0.3f else 0.95f),
                RoundedCornerShape(16.dp)
            )
            .onSizeChanged { canvasSize = it }
            .pointerInput(puzzle, currentDotNumber) {
                detectTapGestures { offset ->
                    val width = (canvasSize.width - padding * 2)
                    val height = (canvasSize.height - padding * 2)

                    // Find if a dot was tapped
                    puzzle.dots.forEach { dot ->
                        val dotX = padding + dot.x * width
                        val dotY = padding + dot.y * height
                        val distance = kotlin.math.sqrt(
                            (offset.x - dotX) * (offset.x - dotX) +
                            (offset.y - dotY) * (offset.y - dotY)
                        )
                        if (distance < 40f) { // Larger tap area for kids
                            onDotTap(dot.number)
                        }
                    }
                }
            }
            .drawBehind {
                if (showingShape) return@drawBehind

                val width = size.width - padding * 2
                val height = size.height - padding * 2

                // Draw connected lines
                for (i in 0 until connectedDots.size - 1) {
                    val dot1 = puzzle.dots.find { it.number == connectedDots[i] }
                    val dot2 = puzzle.dots.find { it.number == connectedDots[i + 1] }

                    if (dot1 != null && dot2 != null) {
                        drawLine(
                            color = puzzle.color,
                            start = Offset(padding + dot1.x * width, padding + dot1.y * height),
                            end = Offset(padding + dot2.x * width, padding + dot2.y * height),
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Close the shape if complete
                if (connectedDots.size == puzzle.dots.size && connectedDots.isNotEmpty()) {
                    val firstDot = puzzle.dots.find { it.number == connectedDots.first() }
                    val lastDot = puzzle.dots.find { it.number == connectedDots.last() }

                    if (firstDot != null && lastDot != null) {
                        drawLine(
                            color = puzzle.color,
                            start = Offset(padding + lastDot.x * width, padding + lastDot.y * height),
                            end = Offset(padding + firstDot.x * width, padding + firstDot.y * height),
                            strokeWidth = 6f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Draw ALL dots - make them visible!
                puzzle.dots.forEach { dot ->
                    val x = padding + dot.x * width
                    val y = padding + dot.y * height
                    val isNext = dot.number == currentDotNumber
                    val isConnected = connectedDots.contains(dot.number)

                    val dotRadius = if (isNext) 28f else 22f

                    // Draw outer filled circle for ALL dots
                    drawCircle(
                        color = when {
                            isConnected -> puzzle.color
                            isNext -> Color(0xFF4CAF50)
                            else -> Color(0xFFBDBDBD) // Light gray - visible!
                        },
                        radius = dotRadius,
                        center = Offset(x, y),
                        style = Fill
                    )

                    // Draw border
                    drawCircle(
                        color = when {
                            isConnected -> puzzle.color.copy(alpha = 0.8f)
                            isNext -> Color(0xFF2E7D32)
                            else -> Color(0xFF757575)
                        },
                        radius = dotRadius,
                        center = Offset(x, y),
                        style = Stroke(width = 3f)
                    )

                    // Inner highlight circle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f),
                        radius = dotRadius * 0.4f,
                        center = Offset(x - dotRadius * 0.15f, y - dotRadius * 0.15f)
                    )
                }
            }
    ) {
        // Draw numbers on top of dots
        if (!showingShape && canvasSize.width > 0) {
            val width = canvasSize.width - padding * 2
            val height = canvasSize.height - padding * 2

            puzzle.dots.forEach { dot ->
                val isNext = dot.number == currentDotNumber
                val isConnected = connectedDots.contains(dot.number)

                val xDp = with(density) { (padding + dot.x * width).toDp() }
                val yDp = with(density) { (padding + dot.y * height).toDp() }

                Text(
                    text = "${dot.number}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isConnected || isNext -> Color.White
                        else -> Color.DarkGray
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(
                        x = xDp - 6.dp,
                        y = yDp - 8.dp
                    )
                )
            }
        }
    }
}
