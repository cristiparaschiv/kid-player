package com.kidplayer.app.presentation.games.dots

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * A dot in the connect-the-dots puzzle
 */
data class Dot(
    val number: Int,
    val x: Float,  // Normalized 0-1
    val y: Float,  // Normalized 0-1
    var isConnected: Boolean = false
)

/**
 * A shape that can be drawn by connecting dots
 */
data class DotPuzzle(
    val name: String,
    val emoji: String,
    val dots: List<Dot>,
    val color: Color
)

/**
 * Predefined puzzles - coordinates normalized to 0-1
 */
object DotPuzzles {

    val star = DotPuzzle(
        name = "Star",
        emoji = "⭐",
        color = Color(0xFFFFD700),
        dots = listOf(
            Dot(1, 0.5f, 0.1f),   // Top point
            Dot(2, 0.62f, 0.38f), // Right upper
            Dot(3, 0.95f, 0.38f), // Right far
            Dot(4, 0.7f, 0.55f),  // Right middle
            Dot(5, 0.8f, 0.9f),   // Right bottom
            Dot(6, 0.5f, 0.7f),   // Bottom middle
            Dot(7, 0.2f, 0.9f),   // Left bottom
            Dot(8, 0.3f, 0.55f),  // Left middle
            Dot(9, 0.05f, 0.38f), // Left far
            Dot(10, 0.38f, 0.38f) // Left upper
        )
    )

    val house = DotPuzzle(
        name = "House",
        emoji = "🏠",
        color = Color(0xFF795548),
        dots = listOf(
            Dot(1, 0.5f, 0.1f),   // Roof top
            Dot(2, 0.85f, 0.4f),  // Roof right
            Dot(3, 0.85f, 0.9f),  // Bottom right
            Dot(4, 0.15f, 0.9f),  // Bottom left
            Dot(5, 0.15f, 0.4f)   // Roof left
        )
    )

    val heart = DotPuzzle(
        name = "Heart",
        emoji = "❤️",
        color = Color(0xFFE91E63),
        dots = listOf(
            Dot(1, 0.5f, 0.35f),   // Top center dip
            Dot(2, 0.25f, 0.2f),   // Left bump top
            Dot(3, 0.1f, 0.35f),   // Left side
            Dot(4, 0.15f, 0.55f),  // Left lower
            Dot(5, 0.35f, 0.75f),  // Left bottom
            Dot(6, 0.5f, 0.9f),    // Bottom point
            Dot(7, 0.65f, 0.75f),  // Right bottom
            Dot(8, 0.85f, 0.55f),  // Right lower
            Dot(9, 0.9f, 0.35f),   // Right side
            Dot(10, 0.75f, 0.2f)   // Right bump top
        )
    )

    val fish = DotPuzzle(
        name = "Fish",
        emoji = "🐟",
        color = Color(0xFF2196F3),
        dots = listOf(
            Dot(1, 0.15f, 0.5f),   // Tail point
            Dot(2, 0.3f, 0.3f),    // Tail top
            Dot(3, 0.5f, 0.25f),   // Body top
            Dot(4, 0.75f, 0.35f),  // Head top
            Dot(5, 0.9f, 0.5f),    // Nose
            Dot(6, 0.75f, 0.65f),  // Head bottom
            Dot(7, 0.5f, 0.75f),   // Body bottom
            Dot(8, 0.3f, 0.7f)     // Tail bottom
        )
    )

    val tree = DotPuzzle(
        name = "Tree",
        emoji = "🌲",
        color = Color(0xFF4CAF50),
        dots = listOf(
            Dot(1, 0.5f, 0.05f),   // Top point
            Dot(2, 0.7f, 0.25f),   // Right tier 1
            Dot(3, 0.55f, 0.25f),  // Right inner 1
            Dot(4, 0.75f, 0.45f),  // Right tier 2
            Dot(5, 0.6f, 0.45f),   // Right inner 2
            Dot(6, 0.8f, 0.65f),   // Right tier 3
            Dot(7, 0.55f, 0.65f),  // Trunk right
            Dot(8, 0.55f, 0.95f),  // Trunk bottom right
            Dot(9, 0.45f, 0.95f),  // Trunk bottom left
            Dot(10, 0.45f, 0.65f), // Trunk left
            Dot(11, 0.2f, 0.65f),  // Left tier 3
            Dot(12, 0.4f, 0.45f),  // Left inner 2
            Dot(13, 0.25f, 0.45f), // Left tier 2
            Dot(14, 0.45f, 0.25f), // Left inner 1
            Dot(15, 0.3f, 0.25f)   // Left tier 1
        )
    )

    val boat = DotPuzzle(
        name = "Boat",
        emoji = "⛵",
        color = Color(0xFF03A9F4),
        dots = listOf(
            Dot(1, 0.5f, 0.1f),    // Sail top
            Dot(2, 0.5f, 0.55f),   // Mast bottom
            Dot(3, 0.85f, 0.55f),  // Sail right
            Dot(4, 0.9f, 0.7f),    // Hull right top
            Dot(5, 0.75f, 0.85f),  // Hull right bottom
            Dot(6, 0.25f, 0.85f),  // Hull left bottom
            Dot(7, 0.1f, 0.7f),    // Hull left top
            Dot(8, 0.15f, 0.55f)   // Sail left
        )
    )

    val rocket = DotPuzzle(
        name = "Rocket",
        emoji = "🚀",
        color = Color(0xFFFF5722),
        dots = listOf(
            Dot(1, 0.5f, 0.05f),   // Nose tip
            Dot(2, 0.65f, 0.25f),  // Right upper body
            Dot(3, 0.65f, 0.6f),   // Right lower body
            Dot(4, 0.8f, 0.8f),    // Right fin tip
            Dot(5, 0.65f, 0.75f),  // Right fin inner
            Dot(6, 0.6f, 0.9f),    // Right flame
            Dot(7, 0.5f, 0.8f),    // Bottom center
            Dot(8, 0.4f, 0.9f),    // Left flame
            Dot(9, 0.35f, 0.75f),  // Left fin inner
            Dot(10, 0.2f, 0.8f),   // Left fin tip
            Dot(11, 0.35f, 0.6f),  // Left lower body
            Dot(12, 0.35f, 0.25f)  // Left upper body
        )
    )

    val cat = DotPuzzle(
        name = "Cat",
        emoji = "🐱",
        color = Color(0xFFFF9800),
        dots = listOf(
            Dot(1, 0.3f, 0.1f),    // Left ear tip
            Dot(2, 0.35f, 0.25f),  // Left ear inner
            Dot(3, 0.5f, 0.2f),    // Head top center
            Dot(4, 0.65f, 0.25f),  // Right ear inner
            Dot(5, 0.7f, 0.1f),    // Right ear tip
            Dot(6, 0.8f, 0.3f),    // Right head side
            Dot(7, 0.75f, 0.45f),  // Right cheek
            Dot(8, 0.5f, 0.55f),   // Chin
            Dot(9, 0.25f, 0.45f),  // Left cheek
            Dot(10, 0.2f, 0.3f)    // Left head side
        )
    )

    val all = listOf(star, house, heart, fish, tree, boat, rocket, cat)

    fun getByIndex(index: Int): DotPuzzle = all[index % all.size]

    fun random(): DotPuzzle = all.random()
}

/**
 * Game configuration
 */
object DotsConfig {
    const val TOTAL_PUZZLES = 8
    const val POINTS_PER_DOT = 10
    const val COMPLETION_BONUS = 50
    const val DOT_RADIUS = 24f
    const val LINE_WIDTH = 6f
}
