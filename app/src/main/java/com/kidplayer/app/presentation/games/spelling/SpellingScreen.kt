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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
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
    val configuration = LocalConfiguration.current
    val isCompactHeight = configuration.screenHeightDp < 480

    GameScaffold(
        gameName = stringResource(R.string.game_spelling_name),
        gameId = "spelling",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        uiState.currentWord?.let { word ->
            if (isCompactHeight) {
                // Compact horizontal layout for landscape phones
                CompactSpellingLayout(
                    round = uiState.round,
                    totalRounds = SpellingConfig.TOTAL_ROUNDS,
                    word = word,
                    isRomanian = uiState.isRomanian,
                    placedLetters = uiState.placedLetters,
                    letterTiles = uiState.letterTiles,
                    wordComplete = uiState.wordComplete,
                    isCorrect = uiState.isCorrect,
                    onSlotClick = { tile ->
                        tile?.let {
                            haptic.performLight()
                            viewModel.onLetterTileClick(it)
                        }
                    },
                    onTileClick = { tile ->
                        haptic.performMedium()
                        viewModel.onLetterTileClick(tile)
                    }
                )
            } else {
                // Standard vertical layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.game_round, uiState.round, SpellingConfig.TOTAL_ROUNDS).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(word) {
                                visible = false
                                kotlinx.coroutines.delay(100)
                                visible = true
                            }
                            val scale by animateFloatAsState(
                                targetValue = if (visible) 1f else 0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                                label = "emojiScale"
                            )
                            Text(text = word.emoji, fontSize = 80.sp, modifier = Modifier.scale(scale))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = word.getHint(uiState.isRomanian).uppercase(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    LetterSlots(
                        placedLetters = uiState.placedLetters,
                        wordComplete = uiState.wordComplete,
                        isCorrect = uiState.isCorrect,
                        correctWord = word.getWord(uiState.isRomanian),
                        onSlotClick = { tile ->
                            tile?.let {
                                haptic.performLight()
                                viewModel.onLetterTileClick(it)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    LetterTiles(
                        tiles = uiState.letterTiles.filter { !it.isPlaced },
                        onTileClick = { tile ->
                            haptic.performMedium()
                            viewModel.onLetterTileClick(tile)
                        }
                    )

                    if (uiState.wordComplete) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ResultIndicator(isCorrect = uiState.isCorrect)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactSpellingLayout(
    round: Int,
    totalRounds: Int,
    word: SpellingWord,
    isRomanian: Boolean,
    placedLetters: List<LetterTile?>,
    letterTiles: List<LetterTile>,
    wordComplete: Boolean,
    isCorrect: Boolean,
    onSlotClick: (LetterTile?) -> Unit,
    onTileClick: (LetterTile) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Emoji + hint
        Column(
            modifier = Modifier.weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ROUND $round/$totalRounds",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
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
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                        label = "emojiScale"
                    )
                    Text(text = word.emoji, fontSize = 56.sp, modifier = Modifier.scale(scale))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = word.getHint(isRomanian).uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Right side: Letter slots + available letters
        Column(
            modifier = Modifier.weight(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Letter slots (compact)
            LetterSlotsCompact(
                placedLetters = placedLetters,
                wordComplete = wordComplete,
                isCorrect = isCorrect,
                correctWord = word.getWord(isRomanian),
                onSlotClick = onSlotClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Available letters
            LetterTilesCompact(
                tiles = letterTiles.filter { !it.isPlaced },
                onTileClick = onTileClick
            )

            if (wordComplete) {
                Spacer(modifier = Modifier.height(8.dp))
                ResultIndicatorCompact(isCorrect = isCorrect)
            }
        }
    }
}

@Composable
private fun LetterSlotsCompact(
    placedLetters: List<LetterTile?>,
    wordComplete: Boolean,
    isCorrect: Boolean,
    correctWord: String,
    onSlotClick: (LetterTile?) -> Unit
) {
    Row(horizontalArrangement = Arrangement.Center) {
        placedLetters.forEachIndexed { index, tile ->
            val slotColor = when {
                wordComplete && isCorrect -> Color(0xFF4CAF50)
                wordComplete && !isCorrect -> {
                    val placedLetter = tile?.letter
                    val correctLetter = correctWord.getOrNull(index)
                    if (placedLetter == correctLetter) Color(0xFF4CAF50) else Color(0xFFE53935)
                }
                tile != null -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            Card(
                modifier = Modifier
                    .padding(2.dp)
                    .size(40.dp)
                    .bouncyClickable(enabled = tile != null && !wordComplete) { onSlotClick(tile) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = slotColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (tile != null) 3.dp else 1.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = tile?.letter?.toString() ?: "_",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (tile != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterTilesCompact(
    tiles: List<LetterTile>,
    onTileClick: (LetterTile) -> Unit
) {
    // Display in rows of up to 6
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tiles.chunked(6).forEach { rowTiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                rowTiles.forEach { tile ->
                    Card(
                        modifier = Modifier
                            .size(40.dp)
                            .bouncyClickable { onTileClick(tile) },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = tile.letter.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultIndicatorCompact(isCorrect: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935))
    ) {
        Text(
            text = stringResource(if (isCorrect) R.string.game_correct else R.string.game_wrong).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
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
                text = stringResource(if (isCorrect) R.string.game_correct else R.string.game_wrong).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
