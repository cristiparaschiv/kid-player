package com.kidplayer.app.presentation.games.coloring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    onNavigateBack: () -> Unit,
    viewModel: ColoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = "Coloring Book",
        gameState = if (uiState.isColoring) GameState.Playing() else GameState.Ready,
        onBackClick = {
            if (uiState.isColoring) {
                viewModel.backToSelection()
            } else {
                onNavigateBack()
            }
        },
        onPauseClick = { },
        onRestartClick = {
            if (uiState.isColoring) {
                viewModel.resetImage()
            }
        },
        onResumeClick = { },
        showScore = false
    ) {
        if (uiState.isColoring) {
            // Coloring mode
            ColoringCanvas(
                displayBitmap = uiState.displayBitmap,
                selectedColor = uiState.selectedColor,
                canUndo = uiState.canUndo,
                onCanvasTap = { x, y, width, height ->
                    haptic.performLight()
                    viewModel.onCanvasTap(x, y, width, height)
                },
                onColorSelect = { viewModel.selectColor(it) },
                onUndo = {
                    haptic.performLight()
                    viewModel.undo()
                },
                onReset = {
                    haptic.performMedium()
                    viewModel.resetImage()
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Image selection mode
            ImageSelectionScreen(
                selectedCategory = uiState.selectedCategory,
                selectedImage = uiState.selectedImage,
                availableImages = uiState.availableImages,
                onCategorySelect = { viewModel.selectCategory(it) },
                onImageSelect = { viewModel.selectImage(it) },
                onStartColoring = { viewModel.startColoring() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageSelectionScreen(
    selectedCategory: ColoringCategory,
    selectedImage: ColoringImage,
    availableImages: List<ColoringImage>,
    onCategorySelect: (ColoringCategory) -> Unit,
    onImageSelect: (ColoringImage) -> Unit,
    onStartColoring: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Category selector
        Text(
            text = "Choose a category:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            ColoringCategory.entries.forEach { category ->
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

        // Image selector with thumbnails
        Text(
            text = "Choose a picture:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            availableImages.forEach { image ->
                key(image.name) {
                    val isSelected = image == selectedImage
                    val thumbnail = remember(image) {
                        ColoringImageLoader.loadPreview(context, image)
                    }

                    Card(
                        onClick = { onImageSelect(image) },
                        modifier = Modifier
                            .size(80.dp)
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

        // Start button
        Button(
            onClick = onStartColoring,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Start Coloring",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ColoringCanvas(
    displayBitmap: ImageBitmap?,
    selectedColor: Color,
    canUndo: Boolean,
    onCanvasTap: (Float, Float, Float, Float) -> Unit,
    onColorSelect: (Color) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Column(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Color palette at the top
        ColorPaletteRow(
            colors = ColorPalette.colors,
            selectedColor = selectedColor,
            onColorSelect = onColorSelect,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Canvas area - takes remaining space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            displayBitmap?.let { bitmap ->
                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                Card(
                    modifier = Modifier
                        .aspectRatio(aspectRatio, matchHeightConstraintsFirst = true)
                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Coloring canvas",
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { canvasSize = it }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    onCanvasTap(
                                        offset.x,
                                        offset.y,
                                        canvasSize.width.toFloat(),
                                        canvasSize.height.toFloat()
                                    )
                                }
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // Toolbar at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo button
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (canUndo) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Reset button
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun ColorPaletteRow(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelect: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(colors) { color ->
                ColorButton(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = {
                        haptic.performLight()
                        onColorSelect(color)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (color == Color.White || color == Color(0xFFFFEB3B) || color == Color(0xFFFFF176)) {
        Color.Gray
    } else {
        color
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
                }
            ),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        // Empty - just shows the color
    }
}
