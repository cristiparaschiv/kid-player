package com.kidplayer.app.presentation.games.match3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun Match3Screen(
    onNavigateBack: () -> Unit,
    viewModel: Match3ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_match3_name),
        gameId = "match3",
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game info bar
            GameInfoBar(
                movesRemaining = uiState.movesRemaining,
                comboLevel = uiState.comboLevel,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Game board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.board.isNotEmpty()) {
                    GameBoard(
                        board = uiState.board,
                        onTileTap = { row, col ->
                            haptic.performLight()
                            viewModel.onTileTap(row, col)
                        },
                        isProcessing = uiState.isProcessing
                    )
                }
            }

            // Combo indicator
            AnimatedVisibility(
                visible = uiState.comboLevel > 1,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                ComboIndicator(
                    level = uiState.comboLevel,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun GameInfoBar(
    movesRemaining: Int,
    @Suppress("UNUSED_PARAMETER") comboLevel: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Moves remaining
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MOVES",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$movesRemaining",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (movesRemaining <= 5) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameBoard(
    board: List<List<Tile>>,
    onTileTap: (Int, Int) -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D44)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            board.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEachIndexed { colIndex, tile ->
                        TileCell(
                            tile = tile,
                            onClick = {
                                if (!isProcessing) {
                                    onTileTap(rowIndex, colIndex)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TileCell(
    tile: Tile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when {
            tile.isMatched -> 0f
            tile.isSelected -> 1.15f
            else -> 1f
        },
        animationSpec = if (tile.isMatched) {
            tween(200)
        } else {
            spring(dampingRatio = 0.6f)
        },
        label = "tileScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (tile.isMatched) 0f else 1f,
        animationSpec = tween(200),
        label = "tileAlpha"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = tile.type.color.copy(alpha = alpha)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (tile.isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (tile.isSelected) {
                        Modifier.border(
                            width = 3.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tile.type.emoji,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComboIndicator(
    level: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                level >= 4 -> Color(0xFFFFD700) // Gold
                level >= 3 -> Color(0xFFFF6B6B) // Coral
                else -> Color(0xFF6C63FF) // Purple
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "COMBO x$level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = when {
                    level >= 4 -> "🔥🔥🔥"
                    level >= 3 -> "🔥🔥"
                    else -> "🔥"
                },
                fontSize = 18.sp
            )
        }
    }
}
