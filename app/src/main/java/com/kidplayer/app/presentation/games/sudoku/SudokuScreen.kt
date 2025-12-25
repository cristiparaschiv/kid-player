package com.kidplayer.app.presentation.games.sudoku

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun SudokuScreen(
    onNavigateBack: () -> Unit,
    viewModel: SudokuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Picture Sudoku",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Puzzle indicator
                PuzzleIndicator(
                    puzzleNumber = uiState.puzzleNumber,
                    totalPuzzles = SudokuConfig.TOTAL_PUZZLES,
                    level = uiState.level
                )

                // Instructions
                Text(
                    text = "Fill each row, column, and box with all 4 pictures!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Sudoku grid
                uiState.currentPuzzle?.let { puzzle ->
                    SudokuGrid(
                        puzzle = puzzle,
                        selectedCell = uiState.selectedCell,
                        onCellClick = { row, col ->
                            haptic.performLight()
                            viewModel.selectCell(row, col)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Emoji picker
                    EmojiPicker(
                        emojis = puzzle.emojis,
                        enabled = uiState.selectedCell != null,
                        onEmojiClick = { index ->
                            haptic.performMedium()
                            viewModel.placeEmoji(index)
                        },
                        onClearClick = {
                            haptic.performLight()
                            viewModel.clearCell()
                        }
                    )
                }
            }

            // Puzzle complete overlay
            AnimatedVisibility(
                visible = uiState.showPuzzleComplete,
                enter = scaleIn() + fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 48.sp
                        )
                        Text(
                            text = "Puzzle Complete!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleIndicator(
    puzzleNumber: Int,
    totalPuzzles: Int,
    level: Int
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🧩", fontSize = 28.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Puzzle",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$puzzleNumber/$totalPuzzles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Level",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = when (level) {
                        1 -> "Easy"
                        2 -> "Medium"
                        else -> "Hard"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (level) {
                        1 -> Color(0xFF4CAF50)
                        2 -> Color(0xFFFF9800)
                        else -> Color(0xFFE53935)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SudokuGrid(
    puzzle: SudokuPuzzle,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF333333)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            for (row in 0 until SudokuConfig.GRID_SIZE) {
                Row {
                    for (col in 0 until SudokuConfig.GRID_SIZE) {
                        val cell = puzzle.getCell(row, col)
                        val isSelected = selectedCell == (row to col)

                        // Add thicker borders for 2x2 boxes
                        val startPadding = if (col == 2) 2.dp else 1.dp
                        val topPadding = if (row == 2) 2.dp else 1.dp

                        SudokuCellView(
                            cell = cell,
                            emoji = if (cell.isGiven) {
                                puzzle.emojis[cell.correctValue]
                            } else if (cell.userValue != null) {
                                puzzle.emojis[cell.userValue!!]
                            } else null,
                            isSelected = isSelected,
                            isCorrect = if (!cell.isGiven && cell.userValue != null) cell.isCorrect else null,
                            modifier = Modifier
                                .padding(start = startPadding, top = topPadding, end = 1.dp, bottom = 1.dp),
                            onClick = { onCellClick(row, col) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SudokuCellView(
    cell: SudokuCell,
    emoji: String?,
    isSelected: Boolean,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color(0xFF2196F3)
        cell.isGiven -> Color(0xFFE8E8E8)
        isCorrect == true -> Color(0xFFE8F5E9)
        isCorrect == false -> Color(0xFFFFEBEE)
        else -> Color.White
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "cellScale"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .size(72.dp)
            .scale(scale),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = 36.sp
                )
            } else {
                // Empty cell indicator
                Text(
                    text = "?",
                    fontSize = 24.sp,
                    color = Color(0xFFBDBDBD)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiPicker(
    emojis: List<String>,
    enabled: Boolean,
    onEmojiClick: (Int) -> Unit,
    onClearClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            emojis.forEachIndexed { index, emoji ->
                Card(
                    onClick = { if (enabled) onEmojiClick(index) },
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (enabled) Color.White else Color(0xFFE0E0E0)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (enabled) 4.dp else 0.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Clear button
            Card(
                onClick = { if (enabled) onClearClick() },
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = if (enabled) Color(0xFFFFEBEE) else Color(0xFFE0E0E0)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (enabled) 4.dp else 0.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) Color(0xFFE53935) else Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}
