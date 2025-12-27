package com.kidplayer.app.presentation.games.common.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.util.bouncyClickable

/**
 * Data class for category items with emoji and color
 */
data class CategoryItem(
    val id: String,
    val name: String,
    val emoji: String,
    val backgroundColor: Color
)

/**
 * Data class for image selection items
 */
data class ImageItem(
    val id: String,
    val name: String,
    val thumbnail: ImageBitmap
)

/**
 * Kid-friendly category selector with large emoji buttons
 * Displays categories as big, colorful cards with emojis
 */
@Composable
fun KidFriendlyCategorySelector(
    categories: List<CategoryItem>,
    selectedCategoryId: String,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title with fun styling
        Text(
            text = "Pick a Theme!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category.id == selectedCategoryId
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f),
                    label = "categoryScale"
                )

                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(20.dp)
                                )
                            } else Modifier
                        )
                        .bouncyClickable {
                            haptic.performMedium()
                            onCategorySelect(category.id)
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = category.backgroundColor
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 12.dp else 4.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Large emoji
                            Text(
                                text = category.emoji,
                                fontSize = 40.sp
                            )
                            // Category name
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Check mark for selected
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = category.backgroundColor,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Kid-friendly image selector with large thumbnails
 * Displays images in a horizontal scrollable row with big cards
 */
@Composable
fun KidFriendlyImageSelector(
    images: List<ImageItem>,
    selectedImageId: String,
    onImageSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Pick a Picture!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            images.forEach { image ->
                val isSelected = image.id == selectedImageId
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f),
                    label = "imageScale"
                )

                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .shadow(
                            elevation = if (isSelected) 12.dp else 4.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                            } else Modifier
                        )
                        .bouncyClickable {
                            haptic.performMedium()
                            onImageSelect(image.id)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp  // Shadow handled above
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = image.thumbnail,
                            contentDescription = image.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Selection overlay
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Kid-friendly difficulty selector with visual icons
 */
@Composable
fun KidFriendlyDifficultySelector(
    difficulties: List<Pair<String, String>>,  // id to display text (e.g., "EASY" to "3x3")
    selectedDifficultyId: String,
    onDifficultySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        difficulties.forEach { (id, displayText) ->
            val isSelected = id == selectedDifficultyId
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                label = "difficultyBg"
            )

            Card(
                modifier = Modifier
                    .height(56.dp)
                    .bouncyClickable {
                        haptic.performLight()
                        onDifficultySelect(id)
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 6.dp else 2.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Big, fun start button for games
 */
@Composable
fun KidFriendlyStartButton(
    text: String = "Let's Play!",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val haptic = rememberHapticFeedback()

    Button(
        onClick = {
            haptic.performMedium()
            onClick()
        },
        modifier = modifier
            .height(64.dp)
            .widthIn(min = 200.dp),
        enabled = enabled,
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),  // Fun green
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
