package com.kidplayer.app.presentation.games.hangman

/**
 * Word categories with emoji hints for young children
 * Expanded with many more words including longer ones for variety
 */
object HangmanWords {
    data class WordWithHint(
        val word: String,
        val hint: String,  // Emoji hint
        val category: String
    )

    val animals = listOf(
        // 3-letter
        WordWithHint("CAT", "🐱", "Animals"),
        WordWithHint("DOG", "🐶", "Animals"),
        WordWithHint("COW", "🐮", "Animals"),
        WordWithHint("PIG", "🐷", "Animals"),
        WordWithHint("HEN", "🐔", "Animals"),
        WordWithHint("BEE", "🐝", "Animals"),
        WordWithHint("ANT", "🐜", "Animals"),
        WordWithHint("BAT", "🦇", "Animals"),
        WordWithHint("OWL", "🦉", "Animals"),
        WordWithHint("FOX", "🦊", "Animals"),
        WordWithHint("BUG", "🐛", "Animals"),
        WordWithHint("FLY", "🪰", "Animals"),
        // 4-letter
        WordWithHint("FISH", "🐟", "Animals"),
        WordWithHint("FROG", "🐸", "Animals"),
        WordWithHint("BEAR", "🐻", "Animals"),
        WordWithHint("DUCK", "🦆", "Animals"),
        WordWithHint("LION", "🦁", "Animals"),
        WordWithHint("BIRD", "🐦", "Animals"),
        WordWithHint("DEER", "🦌", "Animals"),
        WordWithHint("GOAT", "🐐", "Animals"),
        WordWithHint("CRAB", "🦀", "Animals"),
        WordWithHint("WOLF", "🐺", "Animals"),
        WordWithHint("SEAL", "🦭", "Animals"),
        WordWithHint("MOTH", "🦋", "Animals"),
        WordWithHint("WORM", "🪱", "Animals"),
        // 5-letter
        WordWithHint("MOUSE", "🐭", "Animals"),
        WordWithHint("HORSE", "🐴", "Animals"),
        WordWithHint("SHEEP", "🐑", "Animals"),
        WordWithHint("TIGER", "🐯", "Animals"),
        WordWithHint("ZEBRA", "🦓", "Animals"),
        WordWithHint("PANDA", "🐼", "Animals"),
        WordWithHint("KOALA", "🐨", "Animals"),
        WordWithHint("SNAKE", "🐍", "Animals"),
        WordWithHint("WHALE", "🐋", "Animals"),
        WordWithHint("SHARK", "🦈", "Animals"),
        WordWithHint("SNAIL", "🐌", "Animals"),
        WordWithHint("CAMEL", "🐫", "Animals"),
        // 6-letter
        WordWithHint("RABBIT", "🐰", "Animals"),
        WordWithHint("MONKEY", "🐵", "Animals"),
        WordWithHint("TURTLE", "🐢", "Animals"),
        WordWithHint("PARROT", "🦜", "Animals"),
        WordWithHint("SPIDER", "🕷️", "Animals"),
        WordWithHint("PENGUIN", "🐧", "Animals"),
        // 7-letter
        WordWithHint("GIRAFFE", "🦒", "Animals"),
        WordWithHint("DOLPHIN", "🐬", "Animals"),
        WordWithHint("GORILLA", "🦍", "Animals"),
        WordWithHint("CHICKEN", "🐔", "Animals"),
        WordWithHint("OCTOPUS", "🐙", "Animals"),
        WordWithHint("HAMSTER", "🐹", "Animals"),
        // 8-letter
        WordWithHint("ELEPHANT", "🐘", "Animals"),
        WordWithHint("SQUIRREL", "🐿️", "Animals"),
        WordWithHint("BUTTERFLY", "🦋", "Animals"),
        WordWithHint("KANGAROO", "🦘", "Animals"),
        WordWithHint("DINOSAUR", "🦕", "Animals"),
        WordWithHint("FLAMINGO", "🦩", "Animals")
    )

    val food = listOf(
        // 3-letter
        WordWithHint("PIE", "🥧", "Food"),
        WordWithHint("EGG", "🥚", "Food"),
        WordWithHint("HAM", "🍖", "Food"),
        WordWithHint("JAM", "🍓", "Food"),
        WordWithHint("NUT", "🥜", "Food"),
        WordWithHint("PEA", "🫛", "Food"),
        // 4-letter
        WordWithHint("CAKE", "🎂", "Food"),
        WordWithHint("CORN", "🌽", "Food"),
        WordWithHint("MILK", "🥛", "Food"),
        WordWithHint("RICE", "🍚", "Food"),
        WordWithHint("SOUP", "🍲", "Food"),
        WordWithHint("TACO", "🌮", "Food"),
        WordWithHint("MEAT", "🥩", "Food"),
        WordWithHint("FISH", "🐟", "Food"),
        WordWithHint("PEAR", "🍐", "Food"),
        WordWithHint("PLUM", "🫐", "Food"),
        WordWithHint("BEAN", "🫘", "Food"),
        // 5-letter
        WordWithHint("PIZZA", "🍕", "Food"),
        WordWithHint("APPLE", "🍎", "Food"),
        WordWithHint("BREAD", "🍞", "Food"),
        WordWithHint("CANDY", "🍬", "Food"),
        WordWithHint("GRAPE", "🍇", "Food"),
        WordWithHint("LEMON", "🍋", "Food"),
        WordWithHint("MELON", "🍈", "Food"),
        WordWithHint("PEACH", "🍑", "Food"),
        WordWithHint("SALAD", "🥗", "Food"),
        WordWithHint("HONEY", "🍯", "Food"),
        WordWithHint("DONUT", "🍩", "Food"),
        WordWithHint("PASTA", "🍝", "Food"),
        WordWithHint("JUICE", "🧃", "Food"),
        // 6-letter
        WordWithHint("BANANA", "🍌", "Food"),
        WordWithHint("ORANGE", "🍊", "Food"),
        WordWithHint("CHERRY", "🍒", "Food"),
        WordWithHint("COOKIE", "🍪", "Food"),
        WordWithHint("CARROT", "🥕", "Food"),
        WordWithHint("CHEESE", "🧀", "Food"),
        WordWithHint("BUTTER", "🧈", "Food"),
        WordWithHint("POTATO", "🥔", "Food"),
        WordWithHint("TOMATO", "🍅", "Food"),
        WordWithHint("BURGER", "🍔", "Food"),
        WordWithHint("HOTDOG", "🌭", "Food"),
        WordWithHint("MUFFIN", "🧁", "Food"),
        // 7-letter
        WordWithHint("POPCORN", "🍿", "Food"),
        WordWithHint("PANCAKE", "🥞", "Food"),
        WordWithHint("PRETZEL", "🥨", "Food"),
        WordWithHint("CUPCAKE", "🧁", "Food"),
        WordWithHint("AVOCADO", "🥑", "Food"),
        WordWithHint("COCONUT", "🥥", "Food"),
        // 8-letter
        WordWithHint("SANDWICH", "🥪", "Food"),
        WordWithHint("NOODLES", "🍜", "Food"),
        WordWithHint("BROCCOLI", "🥦", "Food"),
        WordWithHint("MUSHROOM", "🍄", "Food"),
        WordWithHint("ICECREAM", "🍦", "Food"),
        WordWithHint("LOLLIPOP", "🍭", "Food"),
        WordWithHint("BIRTHDAY", "🎂", "Food"),
        WordWithHint("DUMPLING", "🥟", "Food")
    )

    val nature = listOf(
        // 3-letter
        WordWithHint("SUN", "☀️", "Nature"),
        WordWithHint("SKY", "🌤️", "Nature"),
        WordWithHint("SEA", "🌊", "Nature"),
        // 4-letter
        WordWithHint("MOON", "🌙", "Nature"),
        WordWithHint("STAR", "⭐", "Nature"),
        WordWithHint("TREE", "🌳", "Nature"),
        WordWithHint("LEAF", "🍃", "Nature"),
        WordWithHint("RAIN", "🌧️", "Nature"),
        WordWithHint("SNOW", "❄️", "Nature"),
        WordWithHint("WIND", "💨", "Nature"),
        WordWithHint("ROSE", "🌹", "Nature"),
        WordWithHint("ROCK", "🪨", "Nature"),
        WordWithHint("POND", "🏞️", "Nature"),
        WordWithHint("LAKE", "🏞️", "Nature"),
        WordWithHint("CAVE", "🕳️", "Nature"),
        WordWithHint("HILL", "⛰️", "Nature"),
        // 5-letter
        WordWithHint("CLOUD", "☁️", "Nature"),
        WordWithHint("STORM", "⛈️", "Nature"),
        WordWithHint("BEACH", "🏖️", "Nature"),
        WordWithHint("OCEAN", "🌊", "Nature"),
        WordWithHint("RIVER", "🏞️", "Nature"),
        WordWithHint("GRASS", "🌿", "Nature"),
        WordWithHint("PLANT", "🌱", "Nature"),
        WordWithHint("EARTH", "🌍", "Nature"),
        WordWithHint("STONE", "🪨", "Nature"),
        WordWithHint("PEARL", "🦪", "Nature"),
        // 6-letter
        WordWithHint("FLOWER", "🌸", "Nature"),
        WordWithHint("GARDEN", "🌷", "Nature"),
        WordWithHint("FOREST", "🌲", "Nature"),
        WordWithHint("DESERT", "🏜️", "Nature"),
        WordWithHint("ISLAND", "🏝️", "Nature"),
        WordWithHint("SUNSET", "🌅", "Nature"),
        WordWithHint("JUNGLE", "🌴", "Nature"),
        WordWithHint("STREAM", "💧", "Nature"),
        // 7-letter
        WordWithHint("RAINBOW", "🌈", "Nature"),
        WordWithHint("VOLCANO", "🌋", "Nature"),
        WordWithHint("THUNDER", "⚡", "Nature"),
        WordWithHint("WEATHER", "🌤️", "Nature"),
        WordWithHint("GLACIER", "🧊", "Nature"),
        // 8-letter
        WordWithHint("MOUNTAIN", "⛰️", "Nature"),
        WordWithHint("SUNSHINE", "☀️", "Nature"),
        WordWithHint("WATERFALL", "💧", "Nature"),
        WordWithHint("SNOWFLAKE", "❄️", "Nature")
    )

    val things = listOf(
        // 3-letter
        WordWithHint("BUS", "🚌", "Things"),
        WordWithHint("CAR", "🚗", "Things"),
        WordWithHint("HAT", "🎩", "Things"),
        WordWithHint("BAG", "👜", "Things"),
        WordWithHint("BED", "🛏️", "Things"),
        WordWithHint("CUP", "☕", "Things"),
        WordWithHint("KEY", "🔑", "Things"),
        WordWithHint("BOX", "📦", "Things"),
        WordWithHint("PEN", "🖊️", "Things"),
        WordWithHint("TOY", "🧸", "Things"),
        // 4-letter
        WordWithHint("BALL", "⚽", "Things"),
        WordWithHint("BELL", "🔔", "Things"),
        WordWithHint("BOOK", "📖", "Things"),
        WordWithHint("BOAT", "⛵", "Things"),
        WordWithHint("BIKE", "🚲", "Things"),
        WordWithHint("DOOR", "🚪", "Things"),
        WordWithHint("GIFT", "🎁", "Things"),
        WordWithHint("KITE", "🪁", "Things"),
        WordWithHint("LAMP", "💡", "Things"),
        WordWithHint("DRUM", "🥁", "Things"),
        WordWithHint("SHOE", "👟", "Things"),
        WordWithHint("SOCK", "🧦", "Things"),
        WordWithHint("RING", "💍", "Things"),
        WordWithHint("COIN", "🪙", "Things"),
        // 5-letter
        WordWithHint("HOUSE", "🏠", "Things"),
        WordWithHint("PHONE", "📱", "Things"),
        WordWithHint("CHAIR", "🪑", "Things"),
        WordWithHint("TABLE", "🪵", "Things"),
        WordWithHint("PIANO", "🎹", "Things"),
        WordWithHint("CLOCK", "🕐", "Things"),
        WordWithHint("PLANE", "✈️", "Things"),
        WordWithHint("TRAIN", "🚂", "Things"),
        WordWithHint("TRUCK", "🚚", "Things"),
        WordWithHint("CROWN", "👑", "Things"),
        WordWithHint("BRUSH", "🖌️", "Things"),
        WordWithHint("SPOON", "🥄", "Things"),
        WordWithHint("WATCH", "⌚", "Things"),
        WordWithHint("BROOM", "🧹", "Things"),
        // 6-letter
        WordWithHint("BOTTLE", "🍼", "Things"),
        WordWithHint("BASKET", "🧺", "Things"),
        WordWithHint("CAMERA", "📷", "Things"),
        WordWithHint("MIRROR", "🪞", "Things"),
        WordWithHint("CANDLE", "🕯️", "Things"),
        WordWithHint("PENCIL", "✏️", "Things"),
        WordWithHint("GUITAR", "🎸", "Things"),
        WordWithHint("ROCKET", "🚀", "Things"),
        WordWithHint("WINDOW", "🪟", "Things"),
        WordWithHint("PILLOW", "🛋️", "Things"),
        WordWithHint("BUCKET", "🪣", "Things"),
        WordWithHint("LADDER", "🪜", "Things"),
        // 7-letter
        WordWithHint("BALLOON", "🎈", "Things"),
        WordWithHint("BICYCLE", "🚲", "Things"),
        WordWithHint("PRESENT", "🎁", "Things"),
        WordWithHint("PICTURE", "🖼️", "Things"),
        WordWithHint("COMPASS", "🧭", "Things"),
        WordWithHint("BLANKET", "🛏️", "Things"),
        WordWithHint("TRUMPET", "🎺", "Things"),
        // 8-letter
        WordWithHint("UMBRELLA", "☂️", "Things"),
        WordWithHint("SCISSORS", "✂️", "Things"),
        WordWithHint("COMPUTER", "💻", "Things"),
        WordWithHint("BACKPACK", "🎒", "Things"),
        WordWithHint("KEYBOARD", "⌨️", "Things"),
        WordWithHint("TREASURE", "💎", "Things"),
        WordWithHint("AIRPLANE", "✈️", "Things"),
        WordWithHint("SANDWICH", "🥪", "Things")
    )

    val body = listOf(
        // 3-letter
        WordWithHint("EAR", "👂", "Body"),
        WordWithHint("EYE", "👁️", "Body"),
        WordWithHint("ARM", "💪", "Body"),
        WordWithHint("LEG", "🦵", "Body"),
        WordWithHint("TOE", "🦶", "Body"),
        WordWithHint("LIP", "👄", "Body"),
        // 4-letter
        WordWithHint("NOSE", "👃", "Body"),
        WordWithHint("HAND", "✋", "Body"),
        WordWithHint("FOOT", "🦶", "Body"),
        WordWithHint("HEAD", "🗣️", "Body"),
        WordWithHint("FACE", "😊", "Body"),
        WordWithHint("BACK", "🔙", "Body"),
        WordWithHint("NECK", "🦒", "Body"),
        WordWithHint("KNEE", "🦵", "Body"),
        WordWithHint("HAIR", "💇", "Body"),
        WordWithHint("CHIN", "🧔", "Body"),
        // 5-letter
        WordWithHint("HEART", "❤️", "Body"),
        WordWithHint("BRAIN", "🧠", "Body"),
        WordWithHint("ELBOW", "💪", "Body"),
        WordWithHint("THUMB", "👍", "Body"),
        WordWithHint("TEETH", "🦷", "Body"),
        WordWithHint("MOUTH", "👄", "Body"),
        WordWithHint("CHEEK", "😊", "Body"),
        WordWithHint("BELLY", "🫃", "Body"),
        // 6-letter
        WordWithHint("FINGER", "👆", "Body"),
        WordWithHint("TONGUE", "👅", "Body"),
        WordWithHint("MUSCLE", "💪", "Body"),
        // 7-letter
        WordWithHint("EYEBROW", "🤨", "Body"),
        WordWithHint("EYELASH", "👁️", "Body"),
        WordWithHint("STOMACH", "🫃", "Body"),
        // 8-letter
        WordWithHint("SHOULDER", "💪", "Body"),
        WordWithHint("FOREHEAD", "🧠", "Body")
    )

    val places = listOf(
        // 4-letter
        WordWithHint("HOME", "🏠", "Places"),
        WordWithHint("PARK", "🏞️", "Places"),
        WordWithHint("FARM", "🚜", "Places"),
        WordWithHint("CITY", "🏙️", "Places"),
        WordWithHint("SHOP", "🏪", "Places"),
        WordWithHint("MALL", "🛒", "Places"),
        WordWithHint("BANK", "🏦", "Places"),
        WordWithHint("POOL", "🏊", "Places"),
        // 5-letter
        WordWithHint("BEACH", "🏖️", "Places"),
        WordWithHint("HOTEL", "🏨", "Places"),
        WordWithHint("STORE", "🏬", "Places"),
        WordWithHint("TOWER", "🗼", "Places"),
        WordWithHint("SPACE", "🚀", "Places"),
        // 6-letter
        WordWithHint("SCHOOL", "🏫", "Places"),
        WordWithHint("CASTLE", "🏰", "Places"),
        WordWithHint("MUSEUM", "🏛️", "Places"),
        WordWithHint("CHURCH", "⛪", "Places"),
        WordWithHint("JUNGLE", "🌴", "Places"),
        WordWithHint("CIRCUS", "🎪", "Places"),
        WordWithHint("OFFICE", "🏢", "Places"),
        // 7-letter
        WordWithHint("AIRPORT", "✈️", "Places"),
        WordWithHint("LIBRARY", "📚", "Places"),
        WordWithHint("THEATER", "🎭", "Places"),
        WordWithHint("STADIUM", "🏟️", "Places"),
        WordWithHint("KITCHEN", "🍳", "Places"),
        WordWithHint("BEDROOM", "🛏️", "Places"),
        // 8-letter
        WordWithHint("HOSPITAL", "🏥", "Places"),
        WordWithHint("BATHROOM", "🚿", "Places"),
        WordWithHint("BACKYARD", "🏡", "Places"),
        WordWithHint("BASEMENT", "🏠", "Places"),
        WordWithHint("BUILDING", "🏗️", "Places"),
        WordWithHint("MOUNTAIN", "⛰️", "Places"),
        WordWithHint("PLAYROOM", "🧸", "Places")
    )

    val activities = listOf(
        // 3-letter
        WordWithHint("RUN", "🏃", "Activities"),
        WordWithHint("EAT", "🍽️", "Activities"),
        WordWithHint("FLY", "✈️", "Activities"),
        WordWithHint("SIT", "🪑", "Activities"),
        // 4-letter
        WordWithHint("PLAY", "🎮", "Activities"),
        WordWithHint("SWIM", "🏊", "Activities"),
        WordWithHint("JUMP", "🦘", "Activities"),
        WordWithHint("RIDE", "🚴", "Activities"),
        WordWithHint("COOK", "👨‍🍳", "Activities"),
        WordWithHint("DRAW", "🎨", "Activities"),
        WordWithHint("READ", "📖", "Activities"),
        WordWithHint("SING", "🎤", "Activities"),
        WordWithHint("WALK", "🚶", "Activities"),
        // 5-letter
        WordWithHint("DANCE", "💃", "Activities"),
        WordWithHint("SLEEP", "😴", "Activities"),
        WordWithHint("CLIMB", "🧗", "Activities"),
        WordWithHint("PAINT", "🎨", "Activities"),
        WordWithHint("WRITE", "✍️", "Activities"),
        WordWithHint("SKATE", "⛸️", "Activities"),
        // 6-letter
        WordWithHint("BAKING", "🥧", "Activities"),
        WordWithHint("HIKING", "🥾", "Activities"),
        WordWithHint("RIDING", "🏇", "Activities"),
        WordWithHint("FLYING", "✈️", "Activities"),
        WordWithHint("GAMING", "🎮", "Activities"),
        // 7-letter
        WordWithHint("READING", "📚", "Activities"),
        WordWithHint("RUNNING", "🏃", "Activities"),
        WordWithHint("COOKING", "🍳", "Activities"),
        WordWithHint("DANCING", "💃", "Activities"),
        WordWithHint("CAMPING", "🏕️", "Activities"),
        WordWithHint("JUMPING", "🦘", "Activities"),
        WordWithHint("FISHING", "🎣", "Activities"),
        WordWithHint("SINGING", "🎤", "Activities"),
        WordWithHint("DRAWING", "✏️", "Activities"),
        // 8-letter
        WordWithHint("SWIMMING", "🏊", "Activities"),
        WordWithHint("PAINTING", "🎨", "Activities"),
        WordWithHint("SLEEPING", "😴", "Activities"),
        WordWithHint("SHOPPING", "🛍️", "Activities"),
        WordWithHint("BIRTHDAY", "🎂", "Activities")
    )

    val allWords: List<WordWithHint> = animals + food + nature + things + body + places + activities

    fun getRandomWord(level: Int): WordWithHint {
        // Filter by word length based on level (progressive difficulty)
        val maxLength = when (level) {
            1 -> 3  // 3-letter words only
            2 -> 4  // up to 4-letter words
            3 -> 5  // up to 5-letter words
            4 -> 5  // 5-letter words preferred
            5 -> 6  // up to 6-letter words
            6 -> 6  // 6-letter words preferred
            7 -> 7  // up to 7-letter words
            else -> 8  // up to 8-letter words
        }

        val minLength = when (level) {
            1 -> 3
            2 -> 3
            3 -> 4
            4 -> 5
            5 -> 5
            6 -> 6
            7 -> 6
            else -> 7
        }

        val eligibleWords = allWords.filter { it.word.length in minLength..maxLength }
        return if (eligibleWords.isNotEmpty()) {
            eligibleWords.random()
        } else {
            allWords.random()
        }
    }
}

/**
 * Game configuration
 */
object HangmanConfig {
    const val MAX_WRONG_GUESSES = 6  // Head, body, left arm, right arm, left leg, right leg
    const val TOTAL_ROUNDS = 8
    const val POINTS_PER_LETTER = 20
    const val POINTS_WIN_BONUS = 100
    const val POINTS_WRONG_GUESS = -10
}

/**
 * Hangman game state
 */
data class HangmanPuzzle(
    val wordWithHint: HangmanWords.WordWithHint,
    val guessedLetters: Set<Char> = emptySet(),
    val wrongGuesses: Int = 0
) {
    val word: String get() = wordWithHint.word
    val hint: String get() = wordWithHint.hint
    val category: String get() = wordWithHint.category

    val displayWord: String
        get() = word.map { char ->
            if (guessedLetters.contains(char)) char else '_'
        }.joinToString(" ")

    val isWon: Boolean
        get() = word.all { guessedLetters.contains(it) }

    val isLost: Boolean
        get() = wrongGuesses >= HangmanConfig.MAX_WRONG_GUESSES

    val isGameOver: Boolean
        get() = isWon || isLost

    val correctLetters: Set<Char>
        get() = guessedLetters.filter { word.contains(it) }.toSet()

    val incorrectLetters: Set<Char>
        get() = guessedLetters.filter { !word.contains(it) }.toSet()

    fun guessLetter(letter: Char): HangmanPuzzle {
        if (guessedLetters.contains(letter) || isGameOver) return this

        val newGuessedLetters = guessedLetters + letter
        val newWrongGuesses = if (word.contains(letter)) {
            wrongGuesses
        } else {
            wrongGuesses + 1
        }

        return copy(
            guessedLetters = newGuessedLetters,
            wrongGuesses = newWrongGuesses
        )
    }
}
