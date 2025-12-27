package com.kidplayer.app.presentation.games.spelling

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.kidplayer.app.presentation.util.bouncyClickable

@Composable
fun SpellingScreen(
    onNavigateBack: () -> Unit,
    viewModel: SpellingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Spelling Bee",
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
            // Round indicator
            Text(
                text = "ROUND ${uiState.round}/${SpellingConfig.TOTAL_ROUNDS}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.currentWord?.let { word ->
                // Emoji display
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        var visible by remember { mutableStateOf(false) }

                        LaunchedEffect(word) {
                            visible = false
                            kotlinx.coroutines.delay(100)
                            visible = true
                        }

                        val scale by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "emojiScale"
                        )

                        Text(
                            text = word.emoji,
                            fontSize = 80.sp,
                            modifier = Modifier.scale(scale)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hint
                Text(
                    text = word.hint.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Letter slots
                LetterSlots(
                    placedLetters = uiState.placedLetters,
                    wordComplete = uiState.wordComplete,
                    isCorrect = uiState.isCorrect,
                    correctWord = word.word,
                    onSlotClick = { tile ->
                        tile?.let {
                            haptic.performLight()
                            viewModel.onLetterTileClick(it)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Available letters
                LetterTiles(
                    tiles = uiState.letterTiles.filter { !it.isPlaced },
                    onTileClick = { tile ->
                        haptic.performMedium()
                        viewModel.onLetterTileClick(tile)
                    }
                )

                // Result indicator
                if (uiState.wordComplete) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ResultIndicator(isCorrect = uiState.isCorrect)
                }
            }
        }
    }
}

@Composable
private fun LetterSlots(
    placedLetters: List<LetterTile?>,
    wordComplete: Boolean,
    isCorrect: Boolean,
    correctWord: String,
    onSlotClick: (LetterTile?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        placedLetters.forEachIndexed { index, tile ->
            val slotColor = when {
                wordComplete && isCorrect -> Color(0xFF4CAF50)
                wordComplete && !isCorrect -> {
                    // Check if this specific letter is correct
                    val placedLetter = tile?.letter
                    val correctLetter = correctWord.getOrNull(index)
                    if (placedLetter == correctLetter) Color(0xFF4CAF50) else Color(0xFFE53935)
                }
                tile != null -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val scale by animateFloatAsState(
                targetValue = if (tile != null) 1f else 0.95f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "slotScale$index"
            )

            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .size(52.dp)
                    .scale(scale)
                    .bouncyClickable(enabled = tile != null && !wordComplete) {
                        onSlotClick(tile)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = slotColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (tile != null) 4.dp else 1.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (tile != null) {
                        Text(
                            text = tile.letter.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "_",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterTiles(
    tiles: List<LetterTile>,
    onTileClick: (LetterTile) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(tiles, key = { it.id }) { tile ->
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(tile) {
                visible = true
            }

            val scale by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "tileScale${tile.id}"
            )

            Card(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale)
                    .bouncyClickable { onTileClick(tile) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tile.letter.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultIndicator(isCorrect: Boolean) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "resultScale"
    )

    Card(
        modifier = Modifier
            .scale(scale)
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isCorrect) "CORRECT!" else "TRY AGAIN!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
