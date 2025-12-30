package com.kidplayer.app.presentation.games.puzzle.sliding

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.games.common.components.CategoryItem
import com.kidplayer.app.presentation.games.common.components.ImageItem
import com.kidplayer.app.presentation.games.common.components.KidFriendlyCategorySelector
import com.kidplayer.app.presentation.games.common.components.KidFriendlyDifficultySelector
import com.kidplayer.app.presentation.games.common.components.KidFriendlyImageSelector
import com.kidplayer.app.presentation.games.common.components.KidFriendlyStartButton
import com.kidplayer.app.presentation.games.puzzle.PuzzleCategory
import com.kidplayer.app.presentation.games.puzzle.PuzzleImage
import com.kidplayer.app.presentation.games.puzzle.PuzzleImageLoader
import com.kidplayer.app.presentation.games.puzzle.PuzzlePiece
import com.kidplayer.app.presentation.util.bouncyClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlidingPuzzleScreen(
    onNavigateBack: () -> Unit,
    viewModel: SlidingPuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_sliding_name),
        gameId = "sliding",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image selection and difficulty (only before game starts or when ready)
            if (uiState.gameState == GameState.Ready || uiState.pieces.isEmpty()) {
                val context = LocalContext.current

                // Convert categories to CategoryItems
                val categoryItems = remember {
                    PuzzleCategory.entries.map { cat ->
                        CategoryItem(
                            id = cat.name,
                            name = cat.displayName,
                            emoji = cat.emoji,
                            backgroundColor = Color(cat.backgroundColor)
                        )
                    }
                }

                // Convert images to ImageItems
                val imageItems = remember(uiState.availableImages) {
                    uiState.availableImages.map { img ->
                        ImageItem(
                            id = img.name,
                            name = img.displayName,
                            thumbnail = PuzzleImageLoader.loadFullImage(context, img)
                        )
                    }
                }

                // Kid-friendly category selector
                KidFriendlyCategorySelector(
                    categories = categoryItems,
                    selectedCategoryId = uiState.selectedCategory.name,
                    onCategorySelect = { id ->
                        PuzzleCategory.fromId(id)?.let { viewModel.selectCategory(it) }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Kid-friendly image selector
                KidFriendlyImageSelector(
                    images = imageItems,
                    selectedImageId = uiState.selectedImage.name,
                    onImageSelect = { id ->
                        PuzzleImage.fromId(id)?.let { viewModel.selectImage(it) }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Difficulty selector
                KidFriendlyDifficultySelector(
                    difficulties = listOf(
                        "EASY" to stringResource(R.string.game_difficulty_easy),
                        "MEDIUM" to stringResource(R.string.game_difficulty_medium),
                        "HARD" to stringResource(R.string.game_difficulty_hard)
                    ),
                    selectedDifficultyId = uiState.config.difficulty.name,
                    onDifficultySelect = { id ->
                        Difficulty.valueOf(id).let { viewModel.setDifficulty(it) }
                    },
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Start button
                KidFriendlyStartButton(
                    text = stringResource(R.string.game_start_puzzle),
                    onClick = { viewModel.startNewGame() },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                // Show difficulty during game
                DifficultySelector(
                    currentDifficulty = uiState.config.difficulty,
                    onDifficultyChange = { viewModel.setDifficulty(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Moves counter and preview image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preview thumbnail
                    uiState.previewImage?.let { preview ->
                        PreviewThumbnail(
                            image = preview,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.game_moves_count, uiState.moves),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Puzzle grid
            if (uiState.pieces.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    PuzzleGrid(
                        pieces = uiState.pieces,
                        gridSize = uiState.gridSize,
                        onTileTap = { position ->
                            haptic.performLight()
                            viewModel.onTileTap(position)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            val isSelected = difficulty == currentDifficulty
            FilterChip(
                selected = isSelected,
                onClick = { onDifficultyChange(difficulty) },
                label = {
                    Text(
                        text = when (difficulty) {
                            Difficulty.EASY -> "3x3"
                            Difficulty.MEDIUM -> "4x4"
                            Difficulty.HARD -> "5x5"
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun PuzzleGrid(
    pieces: List<PuzzlePiece>,
    gridSize: Int,
    onTileTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = 2.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.DarkGray)
            .padding(spacing)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            for (row in 0 until gridSize) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    for (col in 0 until gridSize) {
                        val position = row * gridSize + col
                        val piece = pieces.find { it.currentPosition == position }

                        key(position) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                if (piece != null && !piece.isEmpty) {
                                    PuzzleTile(
                                        piece = piece,
                                        onClick = { onTileTap(position) },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    // Empty space
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.DarkGray)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleTile(
    piece: PuzzlePiece,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .bouncyClickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Image(
            bitmap = piece.bitmap,
            contentDescription = "Puzzle piece ${piece.id}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PreviewThumbnail(
    image: ImageBitmap,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Image(
                bitmap = image,
                contentDescription = "Preview of complete puzzle",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
