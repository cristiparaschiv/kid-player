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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun CrosswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: CrosswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_crossword_name),
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight
            val isCompact = maxHeight < 500.dp

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLandscape) {
                    // Landscape layout - hints on left, grid in center
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: Puzzle info + Hints (vertical)
                        Column(
                            modifier = Modifier.width(160.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PuzzleInfo(
                                puzzleNumber = uiState.puzzleNumber,
                                totalPuzzles = CrosswordConfig.TOTAL_PUZZLES,
                                isCompact = true
                            )

                            uiState.puzzle?.let { puzzle ->
                                HintsColumn(words = puzzle.words)
                            }
                        }

                        // Center: Crossword grid - square aspect ratio
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
                                uiState.puzzle?.let { puzzle ->
                                    CrosswordGrid(
                                        puzzle = puzzle,
                                        selectedCell = uiState.selectedCell,
                                        onCellClick = { row, col ->
                                            haptic.performLight()
                                            viewModel.selectCell(row, col)
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        isLandscape = true
                                    )
                                }

                                // Puzzle complete overlay
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = uiState.puzzleComplete,
                                    enter = scaleIn() + fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    PuzzleCompleteOverlay(isCompact = true)
                                }
                            }
                        }
                    }
                } else {
                    // Portrait layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isCompact) 8.dp else 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Puzzle info
                        PuzzleInfo(
                            puzzleNumber = uiState.puzzleNumber,
                            totalPuzzles = CrosswordConfig.TOTAL_PUZZLES,
                            isCompact = isCompact
                        )

                        Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

                        // Hints row
                        uiState.puzzle?.let { puzzle ->
                            HintsRow(words = puzzle.words, isCompact = isCompact)

                            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))

                            // Crossword grid
                            CrosswordGrid(
                                puzzle = puzzle,
                                selectedCell = uiState.selectedCell,
                                onCellClick = { row, col ->
                                    haptic.performLight()
                                    viewModel.selectCell(row, col)
                                },
                                modifier = Modifier.weight(1f),
                                isLandscape = false
                            )
                        }
                    }

                    // Puzzle complete overlay (portrait)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = uiState.puzzleComplete,
                        enter = scaleIn() + fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        PuzzleCompleteOverlay(isCompact = isCompact)
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
            }
        }
    }
}

@Composable
private fun PuzzleInfo(
    puzzleNumber: Int,
    totalPuzzles: Int,
    isCompact: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 12.dp else 24.dp,
                vertical = if (isCompact) 8.dp else 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 8.dp)
        ) {
            Text(text = "📝", fontSize = if (isCompact) 18.sp else 24.sp)
            Text(
                text = if (isCompact) "$puzzleNumber/$totalPuzzles" else "Puzzle $puzzleNumber of $totalPuzzles",
                style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun PuzzleCompleteOverlay(isCompact: Boolean = false) {
    Card(
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
                text = "🎉",
                fontSize = if (isCompact) 32.sp else 48.sp
            )
            Text(
                text = if (isCompact) "Complete!" else "Puzzle Complete!",
                style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun HintsRow(
    words: List<CrosswordWord>,
    isCompact: Boolean = false
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp),
        contentPadding = PaddingValues(horizontal = if (isCompact) 4.dp else 8.dp)
    ) {
        items(words) { word ->
            HintCard(
                hint = word.hint,
                wordLength = word.length,
                isHorizontal = word.isHorizontal,
                isCompact = isCompact
            )
        }
    }
}

@Composable
private fun HintsColumn(
    words: List<CrosswordWord>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        words.forEach { word ->
            HintCardCompact(
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
    isHorizontal: Boolean,
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
                horizontal = if (isCompact) 8.dp else 12.dp,
                vertical = if (isCompact) 4.dp else 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 8.dp)
        ) {
            Text(text = hint, fontSize = if (isCompact) 20.sp else 28.sp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$wordLength letters",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isHorizontal) "→" else "↓",
                    fontSize = if (isCompact) 12.sp else 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun HintCardCompact(
    hint: String,
    wordLength: Int,
    isHorizontal: Boolean
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = hint, fontSize = 20.sp)
                Text(
                    text = if (isHorizontal) "→" else "↓",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "$wordLength",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CrosswordGrid(
    puzzle: CrosswordPuzzle,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Calculate cell size based on available space
        val availableSize = minOf(maxWidth, maxHeight)
        // Account for padding (4dp on each side) and cell spacing (2dp per cell)
        val gridPadding = 8.dp
        val cellSpacing = 4.dp * puzzle.gridSize
        val usableSize = availableSize - gridPadding - cellSpacing

        // Calculate cell size to fill available space (with max limit for very large screens)
        val calculatedCellSize = usableSize / puzzle.gridSize
        val maxCellSize = if (isLandscape) 80.dp else 64.dp
        val cellSize = minOf(calculatedCellSize, maxCellSize)

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

    // Calculate font size based on cell size (approximately 50% of cell size)
    val fontSize = (cellSize.value * 0.5f).sp

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
                fontSize = fontSize,
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Calculate key size based on available width
            val maxKeyWidth = (maxWidth - 54.dp) / 10  // 10 keys + 9 gaps
            val keySize = minOf(maxKeyWidth, 56.dp)
            val fontSize = (keySize.value * 0.43f).sp
            val spacing = minOf(6.dp, (maxWidth.value * 0.008f).dp)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TAP A LETTER",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                }

                // Letter rows - responsive keys
                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        row.forEach { letter ->
                            Card(
                                onClick = { onLetterClick(letter) },
                                modifier = Modifier.size(keySize),
                                shape = RoundedCornerShape(8.dp),
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
                                        fontSize = fontSize,
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
                    Text("CLEAR")
                }
            }
        }
    }
}
