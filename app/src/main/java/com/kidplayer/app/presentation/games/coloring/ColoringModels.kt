package com.kidplayer.app.presentation.games.coloring

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.kidplayer.app.R

/**
 * Coloring image categories with emojis for kid-friendly display
 */
enum class ColoringCategory(
    val displayName: String,
    val emoji: String,
    val backgroundColor: Long
) {
    ANIMALS("Animals", "🐻", 0xFF4CAF50),  // Green
    CARS("Cars", "🚗", 0xFF2196F3),        // Blue
    SPACE("Space", "🚀", 0xFF9C27B0);      // Purple

    companion object {
        fun fromId(id: String): ColoringCategory? = entries.find { it.name == id }
    }
}

/**
 * Available coloring images grouped by category
 */
enum class ColoringImage(
    @DrawableRes val resourceId: Int,
    val displayName: String,
    val category: ColoringCategory
) {
    // Animals
    ANIMALS_1(R.drawable.coloring_animals1, "Animals 1", ColoringCategory.ANIMALS),
    ANIMALS_2(R.drawable.coloring_animals2, "Animals 2", ColoringCategory.ANIMALS),

    // Cars
    CARS_1(R.drawable.coloring_cars1, "Cars 1", ColoringCategory.CARS),
    CARS_2(R.drawable.coloring_cars2, "Cars 2", ColoringCategory.CARS),

    // Space
    SPACE_1(R.drawable.coloring_space1, "Space 1", ColoringCategory.SPACE),
    SPACE_2(R.drawable.coloring_space2, "Space 2", ColoringCategory.SPACE);

    companion object {
        fun fromId(id: String): ColoringImage? = entries.find { it.name == id }

        fun forCategory(category: ColoringCategory): List<ColoringImage> =
            entries.filter { it.category == category }

        fun defaultForCategory(category: ColoringCategory): ColoringImage =
            forCategory(category).first()
    }
}

/**
 * Color palette for kids - bright, fun colors
 */
object ColorPalette {
    val colors = listOf(
        // Reds & Pinks
        Color(0xFFE53935), // Red
        Color(0xFFFF5252), // Light Red
        Color(0xFFE91E63), // Pink
        Color(0xFFF48FB1), // Light Pink

        // Oranges & Yellows
        Color(0xFFFF9800), // Orange
        Color(0xFFFFB74D), // Light Orange
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFFF176), // Light Yellow

        // Greens
        Color(0xFF4CAF50), // Green
        Color(0xFF8BC34A), // Light Green
        Color(0xFF00E676), // Bright Green
        Color(0xFF1B5E20), // Dark Green

        // Blues & Cyans
        Color(0xFF2196F3), // Blue
        Color(0xFF64B5F6), // Light Blue
        Color(0xFF00BCD4), // Cyan
        Color(0xFF0D47A1), // Dark Blue

        // Purples
        Color(0xFF9C27B0), // Purple
        Color(0xFFBA68C8), // Light Purple
        Color(0xFF673AB7), // Deep Purple

        // Browns & Neutrals
        Color(0xFF795548), // Brown
        Color(0xFFA1887F), // Light Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFF9E9E9E), // Grey

        // Black & White
        Color(0xFF000000), // Black
        Color(0xFFFFFFFF), // White
    )
}

/**
 * Utility class for loading coloring images
 */
object ColoringImageLoader {

    /**
     * Load a coloring image as a mutable Bitmap for editing
     */
    fun loadMutableBitmap(context: Context, coloringImage: ColoringImage): Bitmap {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return BitmapFactory.decodeResource(
            context.resources,
            coloringImage.resourceId,
            options
        ).copy(Bitmap.Config.ARGB_8888, true)
    }

    /**
     * Load the image as ImageBitmap (for preview)
     */
    fun loadPreview(context: Context, coloringImage: ColoringImage): ImageBitmap {
        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            coloringImage.resourceId
        )
        return bitmap.asImageBitmap()
    }
}

/**
 * Flood fill algorithm for coloring
 * Uses a queue-based approach for better performance
 */
object FloodFill {

    /**
     * Fill an area with the target color starting from (x, y)
     * @param bitmap The mutable bitmap to fill
     * @param x Starting x coordinate
     * @param y Starting y coordinate
     * @param fillColor The color to fill with (as Android Color Int)
     * @param tolerance How similar colors need to be to be filled (0-255)
     */
    fun fill(bitmap: Bitmap, x: Int, y: Int, fillColor: Int, tolerance: Int = 30) {
        if (x < 0 || x >= bitmap.width || y < 0 || y >= bitmap.height) return

        val targetColor = bitmap.getPixel(x, y)

        // Don't fill if clicking on a black line or already the fill color
        if (isBlackLine(targetColor) || targetColor == fillColor) return

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val visited = BooleanArray(width * height)
        val queue = ArrayDeque<Int>()
        queue.add(y * width + x)

        while (queue.isNotEmpty()) {
            val pos = queue.removeFirst()
            val px = pos % width
            val py = pos / width

            if (px < 0 || px >= width || py < 0 || py >= height) continue
            if (visited[pos]) continue

            val currentColor = pixels[pos]
            if (!isSimilarColor(currentColor, targetColor, tolerance) || isBlackLine(currentColor)) continue

            visited[pos] = true
            pixels[pos] = fillColor

            // Add neighbors
            if (px > 0) queue.add(pos - 1)
            if (px < width - 1) queue.add(pos + 1)
            if (py > 0) queue.add(pos - width)
            if (py < height - 1) queue.add(pos + width)
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    private fun isBlackLine(color: Int): Boolean {
        val r = android.graphics.Color.red(color)
        val g = android.graphics.Color.green(color)
        val b = android.graphics.Color.blue(color)
        // Consider it a black line if it's very dark
        return r < 50 && g < 50 && b < 50
    }

    private fun isSimilarColor(color1: Int, color2: Int, tolerance: Int): Boolean {
        val r1 = android.graphics.Color.red(color1)
        val g1 = android.graphics.Color.green(color1)
        val b1 = android.graphics.Color.blue(color1)
        val r2 = android.graphics.Color.red(color2)
        val g2 = android.graphics.Color.green(color2)
        val b2 = android.graphics.Color.blue(color2)

        return kotlin.math.abs(r1 - r2) <= tolerance &&
                kotlin.math.abs(g1 - g2) <= tolerance &&
                kotlin.math.abs(b1 - b2) <= tolerance
    }
}
