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
 * Supports both English and Romanian languages
 */
object CrosswordPuzzles {

    data class PuzzleDefinition(
        val gridSize: Int,
        val words: List<CrosswordWord>
    )

    // === ENGLISH PUZZLES ===

    val englishEasyPuzzles = listOf(
        // Puzzle: Animals
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("CAT", "🐱", 0, 0, true),
                CrosswordWord("COW", "🐮", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 1, false)
            )
        ),
        // Puzzle: Nature
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("SUN", "☀️", 0, 0, true),
                CrosswordWord("SIT", "🪑", 0, 0, false),
                CrosswordWord("NUT", "🥜", 0, 2, false)
            )
        ),
        // Puzzle: Pets
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("DOG", "🐶", 0, 0, true),
                CrosswordWord("DIP", "🏊", 0, 0, false),
                CrosswordWord("GOT", "🎯", 0, 2, false)
            )
        ),
        // Puzzle: Insects
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("BEE", "🐝", 0, 0, true),
                CrosswordWord("BUS", "🚌", 0, 0, false),
                CrosswordWord("EAT", "🍽️", 0, 1, false)
            )
        ),
        // Puzzle: Colors
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("RED", "🔴", 0, 0, true),
                CrosswordWord("RUN", "🏃", 0, 0, false),
                CrosswordWord("DAD", "👨", 0, 2, false)
            )
        ),
        // Puzzle: Farm
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("PIG", "🐷", 0, 0, true),
                CrosswordWord("POT", "🍯", 0, 0, false),
                CrosswordWord("GUM", "🫧", 0, 2, false)
            )
        ),
        // Puzzle: Clothes
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("HAT", "🎩", 0, 0, true),
                CrosswordWord("HOP", "🐰", 0, 0, false),
                CrosswordWord("TAP", "🚰", 0, 2, false)
            )
        ),
        // Puzzle: Mixed
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("BAT", "🦇", 0, 0, true),
                CrosswordWord("BIG", "🐘", 0, 0, false),
                CrosswordWord("TEN", "🔟", 0, 2, false)
            )
        )
    )

    val englishMediumPuzzles = listOf(
        // Puzzle: Animals
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("FISH", "🐟", 0, 0, true),
                CrosswordWord("FROG", "🐸", 0, 0, false),
                CrosswordWord("SUN", "☀️", 0, 2, false)
            )
        ),
        // Puzzle: Nature
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("MOON", "🌙", 0, 0, true),
                CrosswordWord("MILK", "🥛", 0, 0, false),
                CrosswordWord("NUT", "🥜", 0, 3, false)
            )
        ),
        // Puzzle: Creatures
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("BEAR", "🐻", 0, 0, true),
                CrosswordWord("BUS", "🚌", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 2, false)
            )
        ),
        // Puzzle: Sky
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("STAR", "⭐", 0, 0, true),
                CrosswordWord("SIT", "🪑", 0, 0, false),
                CrosswordWord("APE", "🐵", 0, 2, false)
            )
        ),
        // Puzzle: Food
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("CAKE", "🎂", 0, 0, true),
                CrosswordWord("COW", "🐮", 0, 0, false),
                CrosswordWord("KITE", "🪁", 0, 2, false)
            )
        ),
        // Puzzle: Birds
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("DUCK", "🦆", 0, 0, true),
                CrosswordWord("DOG", "🐶", 0, 0, false),
                CrosswordWord("CAT", "🐱", 0, 2, false)
            )
        ),
        // Puzzle: Animals
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("LION", "🦁", 0, 0, true),
                CrosswordWord("LEG", "🦵", 0, 0, false),
                CrosswordWord("ICE", "🧊", 0, 2, false)
            )
        )
    )

    val englishHardPuzzles = listOf(
        // Puzzle: Animals
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
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("ZEBRA", "🦓", 0, 0, true),
                CrosswordWord("ZOO", "🦁", 0, 0, false),
                CrosswordWord("BED", "🛏️", 0, 2, false),
                CrosswordWord("ANT", "🐜", 0, 4, false)
            )
        ),
        // Puzzle: Jungle
        PuzzleDefinition(
            gridSize = 7,
            words = listOf(
                CrosswordWord("TIGER", "🐯", 0, 0, true),
                CrosswordWord("TEN", "🔟", 0, 0, false),
                CrosswordWord("GUM", "🫧", 0, 2, false),
                CrosswordWord("EAT", "🍽️", 0, 4, false)
            )
        ),
        // Puzzle: Farm
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

    // === ROMANIAN PUZZLES ===
    // Carefully designed with Romanian words that properly intersect

    val romanianEasyPuzzles = listOf(
        // Puzzle: Animale (Animals)
        // U R S . .
        // N . O . .
        // . . C . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("URS", "🐻", 0, 0, true),     // Bear
                CrosswordWord("UN", "1️⃣", 0, 0, false),     // One
                CrosswordWord("SOC", "🌳", 0, 2, false)     // Elder tree
            )
        ),
        // Puzzle: Mâncare (Food)
        // O U . . .
        // R . . . .
        // Z . . . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("OU", "🥚", 0, 0, true),      // Egg
                CrosswordWord("ORZ", "🌾", 0, 0, false)     // Barley
            )
        ),
        // Puzzle: Natură (Nature)
        // C E R . .
        // A . Â . .
        // S . U . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("CER", "🌤️", 0, 0, true),    // Sky
                CrosswordWord("CAS", "🏠", 0, 0, false),    // House (informal)
                CrosswordWord("RÂU", "🌊", 0, 2, false)     // River
            )
        ),
        // Puzzle: Animale mici
        // P U I . .
        // A . A . .
        // S . R . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("PUI", "🐔", 0, 0, true),     // Chicken
                CrosswordWord("PAS", "👣", 0, 0, false),    // Step
                CrosswordWord("IAR", "🔄", 0, 2, false)     // Again
            )
        ),
        // Puzzle: Corpul
        // N A S . .
        // O . O . .
        // U . C . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("NAS", "👃", 0, 0, true),     // Nose
                CrosswordWord("NOU", "✨", 0, 0, false),    // New
                CrosswordWord("SOC", "🌳", 0, 2, false)     // Elder
            )
        ),
        // Puzzle: Obiecte
        // C O Ș . .
        // A . A . .
        // S . C . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("COȘ", "🧺", 0, 0, true),    // Basket
                CrosswordWord("CAS", "🏠", 0, 0, false),   // House
                CrosswordWord("ȘAC", "♟️", 0, 2, false)    // Chess
            )
        ),
        // Puzzle: Natură 2
        // N O R . .
        // U . O . .
        // C . C . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("NOR", "☁️", 0, 0, true),    // Cloud
                CrosswordWord("NUC", "🌰", 0, 0, false),   // Walnut
                CrosswordWord("ROC", "🪨", 0, 2, false)    // Rock
            )
        ),
        // Puzzle: Fructe
        // M Ă R . .
        // A . O . .
        // I . S . .
        PuzzleDefinition(
            gridSize = 5,
            words = listOf(
                CrosswordWord("MĂR", "🍎", 0, 0, true),    // Apple
                CrosswordWord("MAI", "🌸", 0, 0, false),   // May
                CrosswordWord("ROS", "🔴", 0, 2, false)    // Red (verb)
            )
        )
    )

    val romanianMediumPuzzles = listOf(
        // Puzzle: Animale
        // L E U . . .
        // A . R . . .
        // C . S . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("LEU", "🦁", 0, 0, true),     // Lion
                CrosswordWord("LAC", "🏞️", 0, 0, false),   // Lake
                CrosswordWord("URS", "🐻", 0, 2, false)    // Bear
            )
        ),
        // Puzzle: Natură
        // S T E A . .
        // O . R . . .
        // C . E . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("STEA", "⭐", 0, 0, true),   // Star
                CrosswordWord("SOC", "🌳", 0, 0, false),   // Elder
                CrosswordWord("ERE", "⏰", 0, 2, false)    // Eras (hours)
            )
        ),
        // Puzzle: Mâncare
        // T O R T . .
        // O . A . . .
        // C . I . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("TORT", "🎂", 0, 0, true),   // Cake
                CrosswordWord("TOC", "👠", 0, 0, false),   // Heel
                CrosswordWord("RAI", "😇", 0, 2, false)    // Heaven
            )
        ),
        // Puzzle: Animale 2
        // R A Ț Ă . .
        // A . O . . .
        // C . C . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("RAȚĂ", "🦆", 0, 0, true),   // Duck
                CrosswordWord("RAC", "🦀", 0, 0, false),   // Crab
                CrosswordWord("ȚOC", "🧵", 0, 2, false)    // Spindle
            )
        ),
        // Puzzle: Obiecte
        // C A S Ă . .
        // E . O . . .
        // R . C . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("CASĂ", "🏠", 0, 0, true),   // House
                CrosswordWord("CER", "🌤️", 0, 0, false),  // Sky
                CrosswordWord("SOC", "🌳", 0, 2, false)   // Elder
            )
        ),
        // Puzzle: Natură 2
        // L U N Ă . .
        // A . O . . .
        // C . R . . .
        PuzzleDefinition(
            gridSize = 6,
            words = listOf(
                CrosswordWord("LUNĂ", "🌙", 0, 0, true),   // Moon
                CrosswordWord("LAC", "🏞️", 0, 0, false),  // Lake
                CrosswordWord("NOR", "☁️", 0, 2, false)   // Cloud
            )
        )
    )

    val romanianHardPuzzles = listOf(
        // Puzzle: Animale mari
        // E L E F A N T
        // . A . . . . .
        // . C . . . . .
        PuzzleDefinition(
            gridSize = 8,
            words = listOf(
                CrosswordWord("ELEFANT", "🐘", 0, 0, true),   // Elephant
                CrosswordWord("LAC", "🏞️", 0, 1, false)       // Lake
            )
        ),
        // Puzzle: Fructe
        // B A N A N Ă .
        // U . . . . . .
        // N . . . . . .
        PuzzleDefinition(
            gridSize = 8,
            words = listOf(
                CrosswordWord("BANANĂ", "🍌", 0, 0, true),   // Banana
                CrosswordWord("BUN", "👍", 0, 0, false)      // Good
            )
        ),
        // Puzzle: Natură mare
        // C O P A C . . .
        // O . I . . . . .
        // S . A . . . . .
        PuzzleDefinition(
            gridSize = 8,
            words = listOf(
                CrosswordWord("COPAC", "🌳", 0, 0, true),    // Tree
                CrosswordWord("COS", "🧺", 0, 0, false),     // Basket
                CrosswordWord("PIA", "🔵", 0, 2, false)      // Marble (stone)
            )
        ),
        // Puzzle: Animale de curte
        // C A P R Ă . . .
        // A . A . . . . .
        // S . S . . . . .
        PuzzleDefinition(
            gridSize = 8,
            words = listOf(
                CrosswordWord("CAPRĂ", "🐐", 0, 0, true),    // Goat
                CrosswordWord("CAS", "🏠", 0, 0, false),     // House
                CrosswordWord("PAS", "👣", 0, 2, false)      // Step
            )
        ),
        // Puzzle: Legume
        // M O R C O V . .
        // A . O . . . . .
        // I . C . . . . .
        PuzzleDefinition(
            gridSize = 8,
            words = listOf(
                CrosswordWord("MORCOV", "🥕", 0, 0, true),   // Carrot
                CrosswordWord("MAI", "🌸", 0, 0, false),     // May
                CrosswordWord("ROC", "🪨", 0, 2, false)      // Rock
            )
        )
    )

    // Combined lists for each language
    val englishPuzzles = englishEasyPuzzles + englishMediumPuzzles + englishHardPuzzles
    val romanianPuzzles = romanianEasyPuzzles + romanianMediumPuzzles + romanianHardPuzzles

    // Default puzzles (backwards compatibility)
    val easyPuzzles = englishEasyPuzzles
    val mediumPuzzles = englishMediumPuzzles
    val hardPuzzles = englishHardPuzzles
    val puzzles = englishPuzzles

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

    fun getPuzzle(index: Int, isRomanian: Boolean = false): CrosswordPuzzle {
        val puzzleList = if (isRomanian) romanianPuzzles else englishPuzzles
        val definition = puzzleList[index % puzzleList.size]
        return buildPuzzle(definition)
    }

    /**
     * Get a random puzzle based on difficulty level and language
     */
    fun getRandomPuzzle(level: Int, isRomanian: Boolean = false): CrosswordPuzzle {
        val (easyList, mediumList, hardList) = if (isRomanian) {
            Triple(romanianEasyPuzzles, romanianMediumPuzzles, romanianHardPuzzles)
        } else {
            Triple(englishEasyPuzzles, englishMediumPuzzles, englishHardPuzzles)
        }

        val puzzleList = when {
            level <= 3 -> easyList   // Levels 1-3: Easy (3-letter words)
            level <= 6 -> mediumList // Levels 4-6: Medium (4-letter words)
            else -> hardList         // Levels 7+: Hard (5-letter words)
        }
        val definition = puzzleList.random()
        return buildPuzzle(definition)
    }

    /**
     * Get total count of puzzles available for a language
     */
    fun getTotalPuzzleCount(isRomanian: Boolean = false): Int {
        return if (isRomanian) romanianPuzzles.size else englishPuzzles.size
    }
}

/**
 * Game configuration
 */
object CrosswordConfig {
    fun getTotalPuzzles(isRomanian: Boolean = false): Int = CrosswordPuzzles.getTotalPuzzleCount(isRomanian)
    val TOTAL_PUZZLES: Int get() = CrosswordPuzzles.getTotalPuzzleCount()
    const val POINTS_PER_LETTER = 25
    const val POINTS_PUZZLE_COMPLETE = 100
}
