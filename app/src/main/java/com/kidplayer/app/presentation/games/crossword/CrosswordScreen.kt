package com.kidplayer.app.presentation.games.crossword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun CrosswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: CrosswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Crossword",
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Puzzle info
                PuzzleInfo(
                    puzzleNumber = uiState.puzzleNumber,
                    totalPuzzles = CrosswordConfig.TOTAL_PUZZLES
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hints row
                uiState.puzzle?.let { puzzle ->
                    HintsRow(words = puzzle.words)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Crossword grid
                    CrosswordGrid(
                        puzzle = puzzle,
                        selectedCell = uiState.selectedCell,
                        onCellClick = { row, col ->
                            haptic.performLight()
                            viewModel.selectCell(row, col)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Keyboard overlay
            AnimatedVisibility(
                visible = uiState.showKeyboard,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                LetterKeyboard(
                    onLetterClick = { letter ->
                        haptic.performMedium()
                        viewModel.inputLetter(letter)
                    },
                    onClearClick = {
                        haptic.performLight()
                        viewModel.clearCell()
                    },
                    onDismiss = {
                        viewModel.hideKeyboard()
                    }
                )
            }

            // Puzzle complete overlay
            AnimatedVisibility(
                visible = uiState.puzzleComplete,
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
private fun PuzzleInfo(
    puzzleNumber: Int,
    totalPuzzles: Int
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "📝", fontSize = 24.sp)
            Text(
                text = "Puzzle $puzzleNumber of $totalPuzzles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun HintsRow(
    words: List<CrosswordWord>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(words) { word ->
            HintCard(
                hint = word.hint,
                wordLength = word.length,
                isHorizontal = word.isHorizontal
            )
        }
    }
}

@Composable
private fun HintCard(
    hint: String,
    wordLength: Int,
    isHorizontal: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = hint, fontSize = 28.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$wordLength letters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isHorizontal) "→" else "↓",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun CrosswordGrid(
    puzzle: CrosswordPuzzle,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cellSize = 64.dp  // Larger cells for easier tapping

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF333333)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                for (row in 0 until puzzle.gridSize) {
                    Row {
                        for (col in 0 until puzzle.gridSize) {
                            val cell = puzzle.getCell(row, col)
                            val isSelected = selectedCell == (row to col)

                            CrosswordCellView(
                                cell = cell,
                                isSelected = isSelected,
                                cellSize = cellSize,
                                onClick = { onCellClick(row, col) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CrosswordCellView(
    cell: CrosswordCell?,
    isSelected: Boolean,
    cellSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "cellScale"
    )

    val isPlayable = cell?.correctLetter != null

    val backgroundColor = when {
        cell == null || !isPlayable -> Color(0xFF333333)
        isSelected -> Color(0xFFBBDEFB)
        cell.isCorrect -> Color(0xFFE8F5E9)
        cell.isFilled -> Color(0xFFFFF3E0)
        else -> Color.White
    }

    val borderColor = when {
        isSelected -> Color(0xFF2196F3)
        cell?.isCorrect == true -> Color(0xFF4CAF50)
        else -> Color(0xFF757575)
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .padding(2.dp)
            .scale(scale)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .then(
                if (isPlayable) {
                    Modifier
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .bouncyClickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isPlayable && cell != null) {
            Text(
                text = cell.userLetter?.toString() ?: "",
                fontSize = 32.sp,  // Larger text for better visibility
                fontWeight = FontWeight.Bold,
                color = when {
                    cell.isCorrect -> Color(0xFF2E7D32)
                    cell.isFilled -> Color(0xFF333333)
                    else -> Color.Gray
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LetterKeyboard(
    onLetterClick: (Char) -> Unit,
    onClearClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val rows = listOf(
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    )

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap a letter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }

            // Letter rows - larger keys for kids
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { letter ->
                        Card(
                            onClick = { onLetterClick(letter) },
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$letter",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Clear button
            OutlinedButton(
                onClick = onClearClick,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Clear")
            }
        }
    }
}
