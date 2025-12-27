package com.kidplayer.app.presentation.games.shapes

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Available shapes in the game
 */
enum class Shape(
    val displayName: String,
    val sides: Int,
    val emoji: String // Fallback for display
) {
    CIRCLE("Circle", 0, "⭕"),
    SQUARE("Square", 4, "⬛"),
    TRIANGLE("Triangle", 3, "🔺"),
    RECTANGLE("Rectangle", 4, "📏"),
    STAR("Star", 5, "⭐"),
    HEART("Heart", 0, "❤️"),
    DIAMOND("Diamond", 4, "💎"),
    OVAL("Oval", 0, "🔵"),
    PENTAGON("Pentagon", 5, "⬠"),
    HEXAGON("Hexagon", 6, "⬡")
}

/**
 * Available colors for shapes
 */
enum class ShapeColor(
    val displayName: String,
    val color: Color
) {
    RED("Red", Color(0xFFE53935)),
    BLUE("Blue", Color(0xFF2196F3)),
    GREEN("Green", Color(0xFF4CAF50)),
    YELLOW("Yellow", Color(0xFFFFEB3B)),
    ORANGE("Orange", Color(0xFFFF9800)),
    PURPLE("Purple", Color(0xFF9C27B0)),
    PINK("Pink", Color(0xFFE91E63)),
    CYAN("Cyan", Color(0xFF00BCD4))
}

/**
 * Types of challenges
 */
enum class ChallengeType {
    IDENTIFY_SHAPE,  // "What shape is this?"
    FIND_SHAPE,      // "Find the circle"
    IDENTIFY_COLOR,  // "What color is this shape?"
    COUNT_SIDES      // "How many sides does this shape have?"
}

/**
 * Game configuration
 */
object ShapesConfig {
    const val TOTAL_ROUNDS = 10
    const val POINTS_CORRECT = 100
    const val POINTS_WRONG = -25
    const val OPTIONS_COUNT = 4

    // Shapes available at each level
    fun getShapesForLevel(level: Int): List<Shape> = when (level) {
        1 -> listOf(Shape.CIRCLE, Shape.SQUARE, Shape.TRIANGLE)
        2 -> listOf(Shape.CIRCLE, Shape.SQUARE, Shape.TRIANGLE, Shape.RECTANGLE, Shape.STAR, Shape.HEART)
        else -> Shape.entries.toList()
    }

    // Challenge types available at each level
    fun getChallengeTypesForLevel(level: Int): List<ChallengeType> = when (level) {
        1 -> listOf(ChallengeType.IDENTIFY_SHAPE, ChallengeType.FIND_SHAPE)
        2 -> listOf(ChallengeType.IDENTIFY_SHAPE, ChallengeType.FIND_SHAPE, ChallengeType.IDENTIFY_COLOR)
        else -> ChallengeType.entries.toList()
    }
}

/**
 * A shape challenge
 */
data class ShapeChallenge(
    val type: ChallengeType,
    val targetShape: Shape,
    val targetColor: ShapeColor,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val displayShapes: List<DisplayShape> // For FIND_SHAPE type, multiple shapes shown
)

/**
 * A shape to display on screen
 */
data class DisplayShape(
    val shape: Shape,
    val color: ShapeColor,
    val isTarget: Boolean = false
)

/**
 * Challenge generator
 */
object ShapesGenerator {

    fun generateChallenge(level: Int): ShapeChallenge {
        val availableShapes = ShapesConfig.getShapesForLevel(level)
        val challengeTypes = ShapesConfig.getChallengeTypesForLevel(level)

        val challengeType = challengeTypes.random()
        val targetShape = availableShapes.random()
        val targetColor = ShapeColor.entries.random()

        return when (challengeType) {
            ChallengeType.IDENTIFY_SHAPE -> createIdentifyShapeChallenge(targetShape, targetColor, availableShapes)
            ChallengeType.FIND_SHAPE -> createFindShapeChallenge(targetShape, availableShapes)
            ChallengeType.IDENTIFY_COLOR -> createIdentifyColorChallenge(targetShape, targetColor)
            ChallengeType.COUNT_SIDES -> createCountSidesChallenge(targetShape, targetColor, availableShapes)
        }
    }

    private fun createIdentifyShapeChallenge(
        targetShape: Shape,
        targetColor: ShapeColor,
        availableShapes: List<Shape>
    ): ShapeChallenge {
        val options = generateShapeOptions(targetShape, availableShapes)

        return ShapeChallenge(
            type = ChallengeType.IDENTIFY_SHAPE,
            targetShape = targetShape,
            targetColor = targetColor,
            question = "What shape is this?",
            options = options.map { it.displayName },
            correctAnswer = targetShape.displayName,
            displayShapes = listOf(DisplayShape(targetShape, targetColor, true))
        )
    }

    private fun createFindShapeChallenge(
        targetShape: Shape,
        availableShapes: List<Shape>
    ): ShapeChallenge {
        // Create 4 shapes, one of which is the target
        val otherShapes = availableShapes.filter { it != targetShape }.shuffled().take(3)
        val allShapes = (otherShapes + targetShape).shuffled()

        val displayShapes = allShapes.map { shape ->
            DisplayShape(
                shape = shape,
                color = ShapeColor.entries.random(),
                isTarget = shape == targetShape
            )
        }

        return ShapeChallenge(
            type = ChallengeType.FIND_SHAPE,
            targetShape = targetShape,
            targetColor = displayShapes.first { it.isTarget }.color,
            question = "Tap the ${targetShape.displayName}!",
            options = allShapes.map { it.displayName },
            correctAnswer = targetShape.displayName,
            displayShapes = displayShapes
        )
    }

    private fun createIdentifyColorChallenge(
        targetShape: Shape,
        targetColor: ShapeColor
    ): ShapeChallenge {
        val options = generateColorOptions(targetColor)

        return ShapeChallenge(
            type = ChallengeType.IDENTIFY_COLOR,
            targetShape = targetShape,
            targetColor = targetColor,
            question = "What color is this ${targetShape.displayName.lowercase()}?",
            options = options.map { it.displayName },
            correctAnswer = targetColor.displayName,
            displayShapes = listOf(DisplayShape(targetShape, targetColor, true))
        )
    }

    private fun createCountSidesChallenge(
        targetShape: Shape,
        targetColor: ShapeColor,
        availableShapes: List<Shape>
    ): ShapeChallenge {
        // Only use shapes with sides (not circles/hearts/ovals)
        val shapesWithSides = availableShapes.filter { it.sides > 0 }
        val shape = if (shapesWithSides.isNotEmpty()) shapesWithSides.random() else Shape.TRIANGLE

        val options = generateSidesOptions(shape.sides)

        return ShapeChallenge(
            type = ChallengeType.COUNT_SIDES,
            targetShape = shape,
            targetColor = targetColor,
            question = "How many sides does this ${shape.displayName.lowercase()} have?",
            options = options.map { it.toString() },
            correctAnswer = shape.sides.toString(),
            displayShapes = listOf(DisplayShape(shape, targetColor, true))
        )
    }

    private fun generateShapeOptions(correct: Shape, available: List<Shape>): List<Shape> {
        val options = mutableSetOf(correct)
        val others = available.filter { it != correct }.shuffled()

        for (shape in others) {
            if (options.size >= ShapesConfig.OPTIONS_COUNT) break
            options.add(shape)
        }

        return options.toList().shuffled()
    }

    private fun generateColorOptions(correct: ShapeColor): List<ShapeColor> {
        val options = mutableSetOf(correct)
        val others = ShapeColor.entries.filter { it != correct }.shuffled()

        for (color in others) {
            if (options.size >= ShapesConfig.OPTIONS_COUNT) break
            options.add(color)
        }

        return options.toList().shuffled()
    }

    private fun generateSidesOptions(correct: Int): List<Int> {
        val options = mutableSetOf(correct)

        while (options.size < ShapesConfig.OPTIONS_COUNT) {
            val wrong = when {
                Random.nextBoolean() && correct < 8 -> correct + Random.nextInt(1, 3)
                correct > 2 -> correct - Random.nextInt(1, minOf(2, correct - 1))
                else -> correct + Random.nextInt(1, 3)
            }
            if (wrong > 0) {
                options.add(wrong)
            }
        }

        return options.toList().shuffled()
    }
}
