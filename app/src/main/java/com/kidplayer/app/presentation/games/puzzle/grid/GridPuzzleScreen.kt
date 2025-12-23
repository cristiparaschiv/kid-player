package com.kidplayer.app.presentation.games.puzzle.grid

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState
import com.kidplayer.app.presentation.games.puzzle.PuzzleCategory
import com.kidplayer.app.presentation.games.puzzle.PuzzleImage
import com.kidplayer.app.presentation.games.puzzle.PuzzleImageLoader
import com.kidplayer.app.presentation.games.puzzle.PuzzlePiece
import com.kidplayer.app.presentation.util.bouncyClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridPuzzleScreen(
    onNavigateBack: () -> Unit,
    viewModel: GridPuzzleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Picture Puzzle",
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
            // Image selection and difficulty (only before game starts)
            if (uiState.gameState == GameState.Ready || uiState.pieces.isEmpty()) {
                CategorySelector(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelect = { viewModel.selectCategory(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ImageSelector(
                    availableImages = uiState.availableImages,
                    selectedImage = uiState.selectedImage,
                    onImageSelect = { viewModel.selectImage(it) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DifficultySelector(
                    currentDifficulty = uiState.config.difficulty,
                    onDifficultyChange = { viewModel.setDifficulty(it) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { viewModel.startNewGame() },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Start Puzzle")
                }
            } else {
                // Show difficulty and instruction during game
                DifficultySelector(
                    currentDifficulty = uiState.config.difficulty,
                    onDifficultyChange = { viewModel.setDifficulty(it) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (uiState.selectedPiecePosition != null)
                                "Tap another to swap"
                            else
                                "Tap a piece to select",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Moves: ${uiState.moves}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
                        selectedPosition = uiState.selectedPiecePosition,
                        onPieceTap = { position ->
                            haptic.performLight()
                            viewModel.onPieceTap(position)
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
private fun CategorySelector(
    selectedCategory: PuzzleCategory,
    onCategorySelect: (PuzzleCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose a category:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PuzzleCategory.entries.forEach { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiary,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageSelector(
    availableImages: List<PuzzleImage>,
    selectedImage: PuzzleImage,
    onImageSelect: (PuzzleImage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose a picture:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            availableImages.forEach { image ->
                key(image.name) {
                    val isSelected = image == selectedImage
                    val thumbnail = remember(image) {
                        PuzzleImageLoader.loadFullImage(context, image)
                    }

                    Card(
                        onClick = { onImageSelect(image) },
                        modifier = Modifier
                            .size(70.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else Modifier
                            ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 8.dp else 2.dp
                        )
                    ) {
                        Image(
                            bitmap = thumbnail,
                            contentDescription = image.displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
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
    selectedPosition: Int?,
    onPieceTap: (Int) -> Unit,
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
                                if (piece != null) {
                                    val isSelected = selectedPosition == position
                                    val isCorrect = piece.id == piece.currentPosition

                                    PuzzleTile(
                                        piece = piece,
                                        isSelected = isSelected,
                                        isCorrect = isCorrect,
                                        onClick = { onPieceTap(position) },
                                        modifier = Modifier.fillMaxSize()
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
    isSelected: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tile_scale"
    )

    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.7f)
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (isSelected || isCorrect) {
                    Modifier.border(
                        width = 3.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                } else Modifier
            )
            .bouncyClickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
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
