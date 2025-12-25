package com.kidplayer.app.presentation.games.oddoneout

import kotlin.random.Random

/**
 * Categories for odd one out puzzles
 */
object OddOneOutCategories {
    // Category name to list of items in that category
    val categories = mapOf(
        "Fruits" to listOf("🍎", "🍊", "🍋", "🍇", "🍓", "🍌", "🍑", "🍒", "🥝", "🍍"),
        "Vegetables" to listOf("🥕", "🥦", "🥬", "🌽", "🥒", "🍆", "🌶️", "🧅", "🥔", "🍅"),
        "Animals" to listOf("🐶", "🐱", "🐰", "🐸", "🐵", "🐮", "🐷", "🐴", "🐑", "🐔"),
        "Sea Animals" to listOf("🐟", "🐠", "🐙", "🦀", "🐋", "🦈", "🐬", "🦑", "🦞", "🐚"),
        "Birds" to listOf("🐦", "🦅", "🦆", "🦉", "🐧", "🦜", "🕊️", "🦚", "🦢", "🐓"),
        "Vehicles" to listOf("🚗", "🚕", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🛻", "🚐"),
        "Flying" to listOf("✈️", "🚁", "🛩️", "🚀", "🎈", "🪂", "🛸", "🎏", "🪁", "🦅"),
        "Sports" to listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸"),
        "Weather" to listOf("☀️", "🌙", "⭐", "☁️", "🌧️", "⛈️", "🌈", "❄️", "💨", "🌪️"),
        "Shapes" to listOf("🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⬛", "⬜", "🟫", "💜"),
        "Food" to listOf("🍕", "🍔", "🌭", "🍟", "🌮", "🌯", "🥪", "🍿", "🥨", "🧀"),
        "Desserts" to listOf("🍰", "🎂", "🧁", "🍩", "🍪", "🍫", "🍬", "🍭", "🍮", "🍦"),
        "Music" to listOf("🎵", "🎶", "🎸", "🎹", "🎺", "🎻", "🥁", "🎷", "🪘", "🎤"),
        "Tools" to listOf("🔨", "🪛", "🔧", "🪚", "⛏️", "🔩", "⚙️", "🗜️", "📎", "✂️"),
        "Nature" to listOf("🌸", "🌺", "🌻", "🌹", "🌷", "💐", "🌼", "🪻", "🌵", "🌴")
    )

    fun getRandomCategories(count: Int): List<String> {
        return categories.keys.shuffled().take(count)
    }

    fun getItemsFromCategory(category: String, count: Int): List<String> {
        return categories[category]?.shuffled()?.take(count) ?: emptyList()
    }
}

/**
 * Game configuration
 */
object OddOneOutConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25

    // Items shown increases with level
    fun getItemCount(level: Int): Int = when (level) {
        1 -> 4  // 4 items (3 same category + 1 odd)
        2 -> 5  // 5 items
        else -> 6  // 6 items
    }
}

/**
 * An odd one out puzzle
 */
data class OddOneOutPuzzle(
    val items: List<OddOneOutItem>,
    val oddItemIndex: Int,
    val categoryName: String,
    val oddCategoryName: String
) {
    val oddItem: OddOneOutItem get() = items[oddItemIndex]
}

data class OddOneOutItem(
    val emoji: String,
    val isOdd: Boolean
)

/**
 * Puzzle generator
 */
object OddOneOutGenerator {

    fun generatePuzzle(level: Int): OddOneOutPuzzle {
        val itemCount = OddOneOutConfig.getItemCount(level)

        // Pick two different categories
        val categoryNames = OddOneOutCategories.getRandomCategories(2)
        val mainCategory = categoryNames[0]
        val oddCategory = categoryNames[1]

        // Get items from main category (itemCount - 1 items)
        val mainItems = OddOneOutCategories.getItemsFromCategory(mainCategory, itemCount - 1)
            .map { OddOneOutItem(emoji = it, isOdd = false) }

        // Get one item from odd category
        val oddItem = OddOneOutCategories.getItemsFromCategory(oddCategory, 1)
            .map { OddOneOutItem(emoji = it, isOdd = true) }
            .first()

        // Combine and shuffle
        val allItems = (mainItems + oddItem).shuffled()
        val oddIndex = allItems.indexOfFirst { it.isOdd }

        return OddOneOutPuzzle(
            items = allItems,
            oddItemIndex = oddIndex,
            categoryName = mainCategory,
            oddCategoryName = oddCategory
        )
    }
}
