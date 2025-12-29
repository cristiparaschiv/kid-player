package com.kidplayer.app.presentation.games.maze

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import kotlin.math.abs

@Composable
fun MazeScreen(
    onNavigateBack: () -> Unit,
    viewModel: MazeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_maze_name),
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
                // Landscape layout - controls on left, maze in center
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side: Level info + Direction controls (stacked)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LevelInfo(
                            level = uiState.level,
                            moves = uiState.moves,
                            playerEmoji = uiState.playerEmoji,
                            goalEmoji = uiState.goalEmoji,
                            isCompact = true
                        )

                        DirectionControls(
                            onMove = { direction ->
                                haptic.performLight()
                                viewModel.move(direction)
                            },
                            enabled = uiState.gameState is GameState.Playing && !uiState.levelComplete,
                            isCompact = false
                        )
                    }

                    // Center: Maze display - square aspect ratio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Force square aspect ratio based on height
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            uiState.maze?.let { maze ->
                                MazeDisplay(
                                    maze = maze,
                                    playerPosition = uiState.playerPosition,
                                    playerEmoji = uiState.playerEmoji,
                                    goalEmoji = uiState.goalEmoji,
                                    onSwipe = { direction ->
                                        haptic.performLight()
                                        viewModel.move(direction)
                                    }
                                )
                            }

                            // Level complete overlay
                            androidx.compose.animation.AnimatedVisibility(
                                visible = uiState.levelComplete,
                                enter = scaleIn() + fadeIn(),
                                exit = fadeOut()
                            ) {
                                LevelCompleteOverlay(
                                    level = uiState.level,
                                    playerEmoji = uiState.playerEmoji,
                                    goalEmoji = uiState.goalEmoji,
                                    isCompact = true
                                )
                            }
                        }
                    }
                }
            } else {
                // Portrait layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Level info
                    LevelInfo(
                        level = uiState.level,
                        moves = uiState.moves,
                        playerEmoji = uiState.playerEmoji,
                        goalEmoji = uiState.goalEmoji,
                        isCompact = isCompact
                    )

                    Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                    // Maze display
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        uiState.maze?.let { maze ->
                            MazeDisplay(
                                maze = maze,
                                playerPosition = uiState.playerPosition,
                                playerEmoji = uiState.playerEmoji,
                                goalEmoji = uiState.goalEmoji,
                                onSwipe = { direction ->
                                    haptic.performLight()
                                    viewModel.move(direction)
                                }
                            )
                        }

                        // Level complete overlay
                        androidx.compose.animation.AnimatedVisibility(
                            visible = uiState.levelComplete,
                            enter = scaleIn() + fadeIn(),
                            exit = fadeOut()
                        ) {
                            LevelCompleteOverlay(
                                level = uiState.level,
                                playerEmoji = uiState.playerEmoji,
                                goalEmoji = uiState.goalEmoji,
                                isCompact = isCompact
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                    // Direction buttons
                    DirectionControls(
                        onMove = { direction ->
                            haptic.performLight()
                            viewModel.move(direction)
                        },
                        enabled = uiState.gameState is GameState.Playing && !uiState.levelComplete,
                        isCompact = isCompact
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelInfo(
    level: Int,
    moves: Int,
    playerEmoji: String,
    goalEmoji: String,
    isCompact: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(if (isCompact) 8.dp else 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 20.dp,
                vertical = if (isCompact) 6.dp else 10.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 16.dp else 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL",
                    style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "$level",
                    style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MOVES",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "$moves",
                    style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = playerEmoji, fontSize = if (isCompact) 20.sp else 24.sp)
                Text(text = "→", fontSize = if (isCompact) 14.sp else 16.sp)
                Text(text = goalEmoji, fontSize = if (isCompact) 20.sp else 24.sp)
            }
        }
    }
}

@Composable
private fun MazeDisplay(
    maze: Maze,
    playerPosition: Position,
    playerEmoji: String,
    goalEmoji: String,
    onSwipe: (Direction) -> Unit
) {
    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragStartY by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartX = offset.x
                        dragStartY = offset.y
                    },
                    onDragEnd = { },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val totalDragX = change.position.x - dragStartX
                        val totalDragY = change.position.y - dragStartY

                        val threshold = 50f
                        if (abs(totalDragX) > threshold || abs(totalDragY) > threshold) {
                            val direction = if (abs(totalDragX) > abs(totalDragY)) {
                                if (totalDragX > 0) Direction.RIGHT else Direction.LEFT
                            } else {
                                if (totalDragY > 0) Direction.DOWN else Direction.UP
                            }
                            onSwipe(direction)
                            // Reset drag start to current position
                            dragStartX = change.position.x
                            dragStartY = change.position.y
                        }
                    }
                )
            }
    ) {
        // Get actual size in dp
        val containerWidth = maxWidth
        val containerHeight = maxHeight
        val containerSizeDp = minOf(containerWidth, containerHeight)
        val cellSizeDp = containerSizeDp / maze.size
        val emojiFontSize = (cellSizeDp.value * 0.6f).sp

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / maze.size
            val wallColor = Color(0xFF2D2D44)
            val pathColor = Color(0xFFF5F5F5)
            val wallWidth = 4f

            // Draw background
            drawRect(pathColor)

            // Draw walls
            for (row in 0 until maze.size) {
                for (col in 0 until maze.size) {
                    val cell = maze.cells[row][col]
                    val x = col * cellSize
                    val y = row * cellSize

                    if (cell.topWall) {
                        drawLine(
                            color = wallColor,
                            start = Offset(x, y),
                            end = Offset(x + cellSize, y),
                            strokeWidth = wallWidth,
                            cap = StrokeCap.Round
                        )
                    }
                    if (cell.leftWall) {
                        drawLine(
                            color = wallColor,
                            start = Offset(x, y),
                            end = Offset(x, y + cellSize),
                            strokeWidth = wallWidth,
                            cap = StrokeCap.Round
                        )
                    }
                    if (cell.bottomWall) {
                        drawLine(
                            color = wallColor,
                            start = Offset(x, y + cellSize),
                            end = Offset(x + cellSize, y + cellSize),
                            strokeWidth = wallWidth,
                            cap = StrokeCap.Round
                        )
                    }
                    if (cell.rightWall) {
                        drawLine(
                            color = wallColor,
                            start = Offset(x + cellSize, y),
                            end = Offset(x + cellSize, y + cellSize),
                            strokeWidth = wallWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Draw goal - position centered in cell
        val goalOffsetX = cellSizeDp * maze.goal.col + (cellSizeDp - cellSizeDp * 0.7f) / 2
        val goalOffsetY = cellSizeDp * maze.goal.row + (cellSizeDp - cellSizeDp * 0.7f) / 2
        Text(
            text = goalEmoji,
            fontSize = emojiFontSize,
            modifier = Modifier.offset(x = goalOffsetX, y = goalOffsetY)
        )

        // Draw player with animation - position centered in cell
        val playerScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f),
            label = "playerScale"
        )

        val playerOffsetX = cellSizeDp * playerPosition.col + (cellSizeDp - cellSizeDp * 0.7f) / 2
        val playerOffsetY = cellSizeDp * playerPosition.row + (cellSizeDp - cellSizeDp * 0.7f) / 2
        Text(
            text = playerEmoji,
            fontSize = emojiFontSize,
            modifier = Modifier
                .scale(playerScale)
                .offset(x = playerOffsetX, y = playerOffsetY)
        )
    }
}

@Composable
private fun DirectionControls(
    onMove: (Direction) -> Unit,
    enabled: Boolean,
    isCompact: Boolean = false
) {
    val buttonSize = if (isCompact) 48.dp else 56.dp
    val iconSize = if (isCompact) 28.dp else 32.dp
    val spacing = if (isCompact) 2.dp else 4.dp
    val horizontalGap = if (isCompact) 32.dp else 48.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Up button
        DirectionButton(
            icon = Icons.Default.KeyboardArrowUp,
            onClick = { onMove(Direction.UP) },
            enabled = enabled,
            buttonSize = buttonSize,
            iconSize = iconSize
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalGap)
        ) {
            // Left button
            DirectionButton(
                icon = Icons.Default.KeyboardArrowLeft,
                onClick = { onMove(Direction.LEFT) },
                enabled = enabled,
                buttonSize = buttonSize,
                iconSize = iconSize
            )

            // Right button
            DirectionButton(
                icon = Icons.Default.KeyboardArrowRight,
                onClick = { onMove(Direction.RIGHT) },
                enabled = enabled,
                buttonSize = buttonSize,
                iconSize = iconSize
            )
        }

        // Down button
        DirectionButton(
            icon = Icons.Default.KeyboardArrowDown,
            onClick = { onMove(Direction.DOWN) },
            enabled = enabled,
            buttonSize = buttonSize,
            iconSize = iconSize
        )
    }
}

@Composable
private fun DirectionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    buttonSize: androidx.compose.ui.unit.Dp = 56.dp,
    iconSize: androidx.compose.ui.unit.Dp = 32.dp
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(
                if (enabled) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LevelCompleteOverlay(
    level: Int,
    playerEmoji: String,
    goalEmoji: String,
    isCompact: Boolean = false
) {
    Card(
        modifier = Modifier.padding(if (isCompact) 16.dp else 32.dp),
        shape = RoundedCornerShape(if (isCompact) 16.dp else 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(if (isCompact) 16.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$playerEmoji found $goalEmoji!",
                fontSize = if (isCompact) 24.sp else 32.sp
            )
            Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))
            Text(
                text = "LEVEL $level COMPLETE!",
                style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
