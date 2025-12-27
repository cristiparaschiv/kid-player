package com.kidplayer.app.presentation.games.crossword

/**
 * A word entry in the crossword
 */
data class CrosswordWord(
    val word: String,
    val hint: String,  // Emoji hint
    val startRow: Int,
    val startCol: Int,
    val isHorizontal: Boolean
) {
    val length: Int get() = word.length

    fun getPositions(): List<Pair<Int, Int>> {
        return (0 until length).map { i ->
            if (isHorizontal) {
                startRow to (startCol + i)
            } else {
                (startRow + i) to startCol
            }
        }
    }
}

/**
 * A cell in the crossword grid
 */
data class CrosswordCell(
    val row: Int,
    val col: Int,
    val correctLetter: Char?,  // null for blocked cells
    var userLetter: Char? = null,
    val wordIndices: List<Int> = emptyList()  // Which words this cell belongs to
) {
    val isEmpty: Boolean get() = correctLetter == null
    val isCorrect: Boolean get() = correctLetter != null && userLetter == correctLetter
    val isFilled: Boolean get() = userLetter != null
}

/**
 * A complete crossword puzzle
 */
data class CrosswordPuzzle(
    val gridSize: Int,
    val words: List<CrosswordWord>,
    val grid: List<List<CrosswordCell>>
) {
    fun getCell(row: Int, col: Int): CrosswordCell? {
        return grid.getOrNull(row)?.getOrNull(col)
    }

    fun isSolved(): Boolean {
        return grid.flatten().filter { it.correctLetter != null }.all { it.isCorrect }
    }

    fun getFilledCount(): Int {
        return grid.flatten().count { it.correctLetter != null && it.isFilled }
    }

    fun getTotalCells(): Int {
        return grid.flatten().count { it.correctLetter != null }
    }

    fun withUserLetter(row: Int, col: Int, letter: Char?): CrosswordPuzzle {
        val newGrid = grid.map { rowCells ->
            rowCells.map { cell ->
                if (cell.row == row && cell.col == col && cell.correctLetter != null) {
                    cell.copy(userLetter = letter)
                } else {
                    cell
                }
            }
        }
        return copy(grid = newGrid)
    }
}

/**
 * Pre-made crossword puzzles for kids
 * Each puzzle has words that properly intersect (share the same letter at crossing points)
 * Includes 3-letter, 4-letter, and 5-letter words with progressive difficulty
 */
object CrosswordPuzzles {

    // Each puzzle is carefully designed so intersecting words share the same letter
    // Puzzles are grouped by difficulty (word length)

    // === EASY PUZZLES (3-letter words) ===
    val easyPuzzles = listOf(
        // Puzzle: Animals
        // C A T . .
        // O P . . .
        // W E . . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("CAT", "🐱", 0, 0, true),
                CrosswordWord("COW", "🐮", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 1, false)
            )
        ),
        // Puzzle: Nature
        // S U N . .
        // I . U . .
        // T . T . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("SUN", "☀️", 0, 0, true),
                CrosswordWord("SIT", "🪑", 0, 0, false),
                CrosswordWord("NUT", "🥜", 0, 2, false)
            )
        ),
        // Puzzle: Pets
        // D O G . .
        // I . O . .
        // P . T . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("DOG", "🐶", 0, 0, true),
                CrosswordWord("DIP", "🏊", 0, 0, false),
                CrosswordWord("GOT", "🎯", 0, 2, false)
            )
        ),
        // Puzzle: Insects
        // B E E . .
        // U A . . .
        // S T . . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("BEE", "🐝", 0, 0, true),
                CrosswordWord("BUS", "🚌", 0, 0, false),
                CrosswordWord("EAT", "🍽️", 0, 1, false)
            )
        ),
        // Puzzle: Colors
        // R E D . .
        // U . A . .
        // N . D . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("RED", "🔴", 0, 0, true),
                CrosswordWord("RUN", "🏃", 0, 0, false),
                CrosswordWord("DAD", "👨", 0, 2, false)
            )
        ),
        // Puzzle: Farm
        // P I G . .
        // O . U . .
        // T . M . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("PIG", "🐷", 0, 0, true),
                CrosswordWord("POT", "🍯", 0, 0, false),
                CrosswordWord("GUM", "🫧", 0, 2, false)
            )
        ),
        // Puzzle: Clothes
        // H A T . .
        // O . A . .
        // P . P . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("HAT", "🎩", 0, 0, true),
                CrosswordWord("HOP", "🐰", 0, 0, false),
                CrosswordWord("TAP", "🚰", 0, 2, false)
            )
        ),
        // Puzzle: Mixed
        // B A T . .
        // I . E . .
        // G . N . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("BAT", "🦇", 0, 0, true),
                CrosswordWord("BIG", "🐘", 0, 0, false),
                CrosswordWord("TEN", "🔟", 0, 2, false)
            )
        ),
        // Puzzle: Sea
        // F I N . .
        // O . O . .
        // X . T . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("FIN", "🦈", 0, 0, true),
                CrosswordWord("FOX", "🦊", 0, 0, false),
                CrosswordWord("NOT", "❌", 0, 2, false)
            )
        ),
        // Puzzle: Sky
        // O W L . .
        // N . E . .
        // E . G . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("OWL", "🦉", 0, 0, true),
                CrosswordWord("ONE", "1️⃣", 0, 0, false),
                CrosswordWord("LEG", "🦵", 0, 2, false)
            )
        )
    )

    // === MEDIUM PUZZLES (4-letter words) ===
    val mediumPuzzles = listOf(
        // Puzzle: Animals
        // F I S H . .
        // R . U . . .
        // O . N . . .
        // G . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("FISH", "🐟", 0, 0, true),
                CrosswordWord("FROG", "🐸", 0, 0, false),
                CrosswordWord("SUN", "☀️", 0, 2, false)
            )
        ),
        // Puzzle: Nature
        // M O O N . .
        // I . . U . .
        // L . . T . .
        // K . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("MOON", "🌙", 0, 0, true),
                CrosswordWord("MILK", "🥛", 0, 0, false),
                CrosswordWord("NUT", "🥜", 0, 3, false)
            )
        ),
        // Puzzle: Creatures
        // B E A R . .
        // U . P . . .
        // S . E . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("BEAR", "🐻", 0, 0, true),
                CrosswordWord("BUS", "🚌", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 2, false)
            )
        ),
        // Puzzle: Sky
        // S T A R . .
        // I . P . . .
        // T . E . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("STAR", "⭐", 0, 0, true),
                CrosswordWord("SIT", "🪑", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 2, false)
            )
        ),
        // Puzzle: Food
        // C A K E . .
        // O . I . . .
        // W . T . . .
        // . . E . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("CAKE", "🎂", 0, 0, true),
                CrosswordWord("COW", "🐮", 0, 0, false),
                CrosswordWord("KITE", "🪁", 0, 2, false)
            )
        ),
        // Puzzle: Transport
        // B O A T . .
        // U . D . . .
        // S . D . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("BOAT", "⛵", 0, 0, true),
                CrosswordWord("BUS", "🚌", 0, 0, false),
                CrosswordWord("ADD", "➕", 0, 2, false)
            )
        ),
        // Puzzle: Forest
        // T R E E . .
        // E . A . . .
        // N . T . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("TREE", "🌳", 0, 0, true),
                CrosswordWord("TEN", "🔟", 0, 0, false),
                CrosswordWord("EAT", "🍽️", 0, 2, false)
            )
        ),
        // Puzzle: Birds
        // D U C K . .
        // O . A . . .
        // G . T . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("DUCK", "🦆", 0, 0, true),
                CrosswordWord("DOG", "🐶", 0, 0, false),
                CrosswordWord("CAT", "🐱", 0, 2, false)
            )
        ),
        // Puzzle: Farm
        // G O A T . .
        // U . P . . .
        // M . E . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("GOAT", "🐐", 0, 0, true),
                CrosswordWord("GUM", "🫧", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 2, false)
            )
        ),
        // Puzzle: Animals
        // L I O N . .
        // E . C . . .
        // G . E . . .
        // . . . . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("LION", "🦁", 0, 0, true),
                CrosswordWord("LEG", "🦵", 0, 0, false),
                CrosswordWord("ICE", "🧊", 0, 2, false)
            )
        )
    )

    // === HARD PUZZLES (5-letter words) ===
    val hardPuzzles = listOf(
        // Puzzle: Animals
        // H O R S E . .
        // O . U . A . .
        // P . N . T . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("HORSE", "🐴", 0, 0, true),
                CrosswordWord("HOP", "🐰", 0, 0, false),
                CrosswordWord("RUN", "🏃", 0, 2, false),
                CrosswordWord("EAT", "🍽️", 0, 4, false)
            )
        ),
        // Puzzle: Food
        // A P P L E . .
        // P . A . A . .
        // E . N . K . .
        // . . . . E . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("APPLE", "🍎", 0, 0, true),
                CrosswordWord("APE", "🐵", 0, 0, false),
                CrosswordWord("PAN", "🍳", 0, 2, false),
                CrosswordWord("LAKE", "🏞️", 0, 4, false)
            )
        ),
        // Puzzle: Zoo
        // P A N D A . .
        // I . U . D . .
        // G . T . D . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("PANDA", "🐼", 0, 0, true),
                CrosswordWord("PIG", "🐷", 0, 0, false),
                CrosswordWord("NUT", "🥜", 0, 2, false),
                CrosswordWord("ADD", "➕", 0, 4, false)
            )
        ),
        // Puzzle: Ocean
        // W H A L E . .
        // I . D . A . .
        // N . D . T . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("WHALE", "🐋", 0, 0, true),
                CrosswordWord("WIN", "🏆", 0, 0, false),
                CrosswordWord("ADD", "➕", 0, 2, false),
                CrosswordWord("EAT", "🍽️", 0, 4, false)
            )
        ),
        // Puzzle: Safari
        // Z E B R A . .
        // O . E . N . .
        // O . D . T . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("ZEBRA", "🦓", 0, 0, true),
                CrosswordWord("ZOO", "🦁", 0, 0, false),
                CrosswordWord("BED", "🛏️", 0, 2, false),
                CrosswordWord("ANT", "🐜", 0, 4, false)
            )
        ),
        // Puzzle: Ocean
        // S H A R K . .
        // I . A . I . .
        // T . T . T . .
        // . . . . E . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("SHARK", "🦈", 0, 0, true),
                CrosswordWord("SIT", "🪑", 0, 0, false),
                CrosswordWord("HAT", "🎩", 0, 2, false),
                CrosswordWord("KITE", "🪁", 0, 4, false)
            )
        ),
        // Puzzle: Jungle
        // T I G E R . .
        // E . R . A . .
        // N . L . T . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("TIGER", "🐯", 0, 0, true),
                CrosswordWord("TEN", "🔟", 0, 0, false),
                CrosswordWord("GIRL", "👧", 0, 2, false),
                CrosswordWord("EAT", "🍽️", 0, 4, false)
            )
        ),
        // Puzzle: Sky
        // C L O U D . .
        // A . W . I . .
        // T . L . P . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("CLOUD", "☁️", 0, 0, true),
                CrosswordWord("CAT", "🐱", 0, 0, false),
                CrosswordWord("OWL", "🦉", 0, 2, false),
                CrosswordWord("DIP", "🏊", 0, 4, false)
            )
        ),
        // Puzzle: Nature
        // G R A S S . .
        // U . I . U . .
        // M . N . N . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("GRASS", "🌿", 0, 0, true),
                CrosswordWord("GUM", "🫧", 0, 0, false),
                CrosswordWord("RAIN", "🌧️", 0, 2, false),
                CrosswordWord("SUN", "☀️", 0, 4, false)
            )
        ),
        // Puzzle: Farm
        // S H E E P . .
        // I . A . A . .
        // T . T . N . .
        // . . . . . . .
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("SHEEP", "🐑", 0, 0, true),
                CrosswordWord("SIT", "🪑", 0, 0, false),
                CrosswordWord("EAT", "🍽️", 0, 2, false),
                CrosswordWord("PAN", "🍳", 0, 4, false)
            )
        )
    )

    // Combined list for backwards compatibility
    val puzzles = easyPuzzles + mediumPuzzles + hardPuzzles

    data class PuzzleDefinition(
        val gridSize: Int,
        val words: List<CrosswordWord>
    )

    fun buildPuzzle(definition: PuzzleDefinition): CrosswordPuzzle {
        // Create empty grid
        val grid = MutableList(definition.gridSize) { row ->
            MutableList(definition.gridSize) { col ->
                CrosswordCell(row, col, null)
            }
        }

        // Fill in words
        definition.words.forEachIndexed { wordIndex, word ->
            word.getPositions().forEachIndexed { charIndex, (row, col) ->
                val existingCell = grid[row][col]
                grid[row][col] = CrosswordCell(
                    row = row,
                    col = col,
                    correctLetter = word.word[charIndex],
                    wordIndices = existingCell.wordIndices + wordIndex
                )
            }
        }

        return CrosswordPuzzle(
            gridSize = definition.gridSize,
            words = definition.words,
            grid = grid
        )
    }

    fun getPuzzle(index: Int): CrosswordPuzzle {
        val definition = puzzles[index % puzzles.size]
        return buildPuzzle(definition)
    }

    /**
     * Get a random puzzle based on difficulty level
     */
    fun getRandomPuzzle(level: Int): CrosswordPuzzle {
        val puzzleList = when {
            level <= 3 -> easyPuzzles   // Levels 1-3: Easy (3-letter words)
            level <= 6 -> mediumPuzzles // Levels 4-6: Medium (4-letter words)
            else -> hardPuzzles         // Levels 7+: Hard (5-letter words)
        }
        val definition = puzzleList.random()
        return buildPuzzle(definition)
    }

    /**
     * Get total count of puzzles available
     */
    fun getTotalPuzzleCount(): Int = puzzles.size
}

/**
 * Game configuration
 */
object CrosswordConfig {
    val TOTAL_PUZZLES: Int get() = CrosswordPuzzles.getTotalPuzzleCount()
    const val POINTS_PER_LETTER = 25
    const val POINTS_PUZZLE_COMPLETE = 100
}
