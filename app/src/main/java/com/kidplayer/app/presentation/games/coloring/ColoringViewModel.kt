package com.kidplayer.app.presentation.games.coloring

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ColoringViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColoringUiState())
    val uiState: StateFlow<ColoringUiState> = _uiState.asStateFlow()

    // Keep track of bitmap states for undo
    private val undoStack = mutableListOf<Bitmap>()
    private var currentBitmap: Bitmap? = null

    init {
        val initialCategory = ColoringCategory.ANIMALS
        val initialImage = ColoringImage.defaultForCategory(initialCategory)

        _uiState.update {
            it.copy(
                selectedCategory = initialCategory,
                selectedImage = initialImage,
                availableImages = ColoringImage.forCategory(initialCategory)
            )
        }
    }

    /**
     * Select a coloring category
     */
    fun selectCategory(category: ColoringCategory) {
        val imagesInCategory = ColoringImage.forCategory(category)
        val defaultImage = imagesInCategory.first()

        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedImage = defaultImage,
                availableImages = imagesInCategory,
                isColoring = false,
                displayBitmap = null
            )
        }
        currentBitmap = null
        undoStack.clear()
    }

    /**
     * Select a coloring image
     */
    fun selectImage(image: ColoringImage) {
        _uiState.update {
            it.copy(
                selectedImage = image,
                isColoring = false,
                displayBitmap = null
            )
        }
        currentBitmap = null
        undoStack.clear()
    }

    /**
     * Select a color from the palette
     */
    fun selectColor(color: Color) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    /**
     * Start coloring - load the image as mutable bitmap
     */
    fun startColoring() {
        val image = _uiState.value.selectedImage
        currentBitmap = ColoringImageLoader.loadMutableBitmap(context, image)
        undoStack.clear()

        _uiState.update {
            it.copy(
                isColoring = true,
                displayBitmap = currentBitmap?.asImageBitmap()
            )
        }
    }

    /**
     * Handle tap on the canvas - fill the tapped area with selected color
     */
    fun onCanvasTap(x: Float, y: Float, canvasWidth: Float, canvasHeight: Float) {
        val bitmap = currentBitmap ?: return

        // Convert canvas coordinates to bitmap coordinates
        val bitmapX = (x / canvasWidth * bitmap.width).toInt()
        val bitmapY = (y / canvasHeight * bitmap.height).toInt()

        if (bitmapX < 0 || bitmapX >= bitmap.width || bitmapY < 0 || bitmapY >= bitmap.height) return

        // Save current state for undo (limit stack size)
        if (undoStack.size >= 20) {
            undoStack.removeAt(0)
        }
        undoStack.add(bitmap.copy(bitmap.config, true))

        // Perform flood fill
        val fillColor = _uiState.value.selectedColor.toArgb()
        FloodFill.fill(bitmap, bitmapX, bitmapY, fillColor)

        // Update display
        _uiState.update {
            it.copy(
                displayBitmap = bitmap.asImageBitmap(),
                canUndo = undoStack.isNotEmpty()
            )
        }
    }

    /**
     * Undo the last fill operation
     */
    fun undo() {
        if (undoStack.isEmpty()) return

        val previousBitmap = undoStack.removeAt(undoStack.lastIndex)
        currentBitmap = previousBitmap

        _uiState.update {
            it.copy(
                displayBitmap = previousBitmap.asImageBitmap(),
                canUndo = undoStack.isNotEmpty()
            )
        }
    }

    /**
     * Clear and reset to original image
     */
    fun resetImage() {
        val image = _uiState.value.selectedImage
        currentBitmap = ColoringImageLoader.loadMutableBitmap(context, image)
        undoStack.clear()

        _uiState.update {
            it.copy(
                displayBitmap = currentBitmap?.asImageBitmap(),
                canUndo = false
            )
        }
    }

    /**
     * Go back to image selection
     */
    fun backToSelection() {
        _uiState.update {
            it.copy(
                isColoring = false,
                displayBitmap = null
            )
        }
        currentBitmap = null
        undoStack.clear()
    }
}

data class ColoringUiState(
    val selectedCategory: ColoringCategory = ColoringCategory.ANIMALS,
    val selectedImage: ColoringImage = ColoringImage.ANIMALS_1,
    val availableImages: List<ColoringImage> = ColoringImage.forCategory(ColoringCategory.ANIMALS),
    val selectedColor: Color = ColorPalette.colors.first(),
    val isColoring: Boolean = false,
    val displayBitmap: ImageBitmap? = null,
    val canUndo: Boolean = false
)
