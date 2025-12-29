package com.kidplayer.app.presentation.games.hangman

/**
 * Word categories with emoji hints for young children
 * Bilingual support for English and Romanian
 */
object HangmanWords {
    data class WordWithHint(
        val wordEn: String,
        val wordRo: String,
        val hint: String,  // Emoji hint
        val categoryEn: String,
        val categoryRo: String
    ) {
        fun getWord(isRomanian: Boolean): String = if (isRomanian) wordRo else wordEn
        fun getCategory(isRomanian: Boolean): String = if (isRomanian) categoryRo else categoryEn
    }

    val animals = listOf(
        // 3-letter (EN) / Various (RO)
        WordWithHint("CAT", "PISICĂ", "🐱", "Animals", "Animale"),
        WordWithHint("DOG", "CÂINE", "🐶", "Animals", "Animale"),
        WordWithHint("COW", "VACĂ", "🐮", "Animals", "Animale"),
        WordWithHint("PIG", "PORC", "🐷", "Animals", "Animale"),
        WordWithHint("HEN", "GĂINĂ", "🐔", "Animals", "Animale"),
        WordWithHint("BEE", "ALBINĂ", "🐝", "Animals", "Animale"),
        WordWithHint("ANT", "FURNICĂ", "🐜", "Animals", "Animale"),
        WordWithHint("BAT", "LILIAC", "🦇", "Animals", "Animale"),
        WordWithHint("OWL", "BUFNIȚĂ", "🦉", "Animals", "Animale"),
        WordWithHint("FOX", "VULPE", "🦊", "Animals", "Animale"),
        WordWithHint("BUG", "GÂNDAC", "🐛", "Animals", "Animale"),
        WordWithHint("FLY", "MUSCĂ", "🦟", "Animals", "Animale"),
        // 4-letter (EN)
        WordWithHint("FISH", "PEȘTE", "🐟", "Animals", "Animale"),
        WordWithHint("FROG", "BROASCĂ", "🐸", "Animals", "Animale"),
        WordWithHint("BEAR", "URS", "🐻", "Animals", "Animale"),
        WordWithHint("DUCK", "RAȚĂ", "🦆", "Animals", "Animale"),
        WordWithHint("LION", "LEU", "🦁", "Animals", "Animale"),
        WordWithHint("BIRD", "PASĂRE", "🐦", "Animals", "Animale"),
        WordWithHint("DEER", "CERB", "🦌", "Animals", "Animale"),
        WordWithHint("GOAT", "CAPRĂ", "🐐", "Animals", "Animale"),
        WordWithHint("CRAB", "CRAB", "🦀", "Animals", "Animale"),
        WordWithHint("WOLF", "LUP", "🐺", "Animals", "Animale"),
        WordWithHint("SEAL", "FOCĂ", "🦭", "Animals", "Animale"),
        WordWithHint("WORM", "VIERME", "🐛", "Animals", "Animale"),
        // 5-letter (EN)
        WordWithHint("MOUSE", "ȘOARECE", "🐭", "Animals", "Animale"),
        WordWithHint("HORSE", "CAL", "🐴", "Animals", "Animale"),
        WordWithHint("SHEEP", "OAIE", "🐑", "Animals", "Animale"),
        WordWithHint("TIGER", "TIGRU", "🐯", "Animals", "Animale"),
        WordWithHint("ZEBRA", "ZEBRĂ", "🦓", "Animals", "Animale"),
        WordWithHint("PANDA", "PANDA", "🐼", "Animals", "Animale"),
        WordWithHint("KOALA", "KOALA", "🐨", "Animals", "Animale"),
        WordWithHint("SNAKE", "ȘARPE", "🐍", "Animals", "Animale"),
        WordWithHint("WHALE", "BALENĂ", "🐋", "Animals", "Animale"),
        WordWithHint("SHARK", "RECHIN", "🦈", "Animals", "Animale"),
        WordWithHint("SNAIL", "MELC", "🐌", "Animals", "Animale"),
        WordWithHint("CAMEL", "CĂMILĂ", "🐫", "Animals", "Animale"),
        // 6-letter (EN)
        WordWithHint("RABBIT", "IEPURE", "🐰", "Animals", "Animale"),
        WordWithHint("MONKEY", "MAIMUȚĂ", "🐵", "Animals", "Animale"),
        WordWithHint("TURTLE", "ȚESTOASĂ", "🐢", "Animals", "Animale"),
        WordWithHint("PARROT", "PAPAGAL", "🦜", "Animals", "Animale"),
        WordWithHint("SPIDER", "PĂIANJEN", "🕷️", "Animals", "Animale"),
        WordWithHint("PENGUIN", "PINGUIN", "🐧", "Animals", "Animale"),
        // 7-letter (EN)
        WordWithHint("GIRAFFE", "GIRAFĂ", "🦒", "Animals", "Animale"),
        WordWithHint("DOLPHIN", "DELFIN", "🐬", "Animals", "Animale"),
        WordWithHint("GORILLA", "GORILĂ", "🦍", "Animals", "Animale"),
        WordWithHint("CHICKEN", "PUI", "🐔", "Animals", "Animale"),
        WordWithHint("OCTOPUS", "CARACATIȚĂ", "🐙", "Animals", "Animale"),
        WordWithHint("HAMSTER", "HAMSTER", "🐹", "Animals", "Animale"),
        // 8-letter (EN)
        WordWithHint("ELEPHANT", "ELEFANT", "🐘", "Animals", "Animale"),
        WordWithHint("SQUIRREL", "VEVERIȚĂ", "🐿️", "Animals", "Animale"),
        WordWithHint("BUTTERFLY", "FLUTURE", "🦋", "Animals", "Animale"),
        WordWithHint("KANGAROO", "CANGUR", "🦘", "Animals", "Animale"),
        WordWithHint("DINOSAUR", "DINOZAUR", "🦕", "Animals", "Animale"),
        WordWithHint("FLAMINGO", "FLAMINGO", "🦩", "Animals", "Animale")
    )

    val food = listOf(
        // 3-letter (EN)
        WordWithHint("PIE", "PLĂCINTĂ", "🥧", "Food", "Mâncare"),
        WordWithHint("EGG", "OU", "🥚", "Food", "Mâncare"),
        WordWithHint("HAM", "ȘUNCĂ", "🍖", "Food", "Mâncare"),
        WordWithHint("JAM", "GEM", "🍓", "Food", "Mâncare"),
        WordWithHint("NUT", "NUCĂ", "🥜", "Food", "Mâncare"),
        WordWithHint("PEA", "MAZĂRE", "🥬", "Food", "Mâncare"),
        // 4-letter (EN)
        WordWithHint("CAKE", "TORT", "🎂", "Food", "Mâncare"),
        WordWithHint("CORN", "PORUMB", "🌽", "Food", "Mâncare"),
        WordWithHint("MILK", "LAPTE", "🥛", "Food", "Mâncare"),
        WordWithHint("RICE", "OREZ", "🍚", "Food", "Mâncare"),
        WordWithHint("SOUP", "SUPĂ", "🍲", "Food", "Mâncare"),
        WordWithHint("MEAT", "CARNE", "🥩", "Food", "Mâncare"),
        WordWithHint("PEAR", "PARĂ", "🍐", "Food", "Mâncare"),
        WordWithHint("PLUM", "PRUNĂ", "🫐", "Food", "Mâncare"),
        WordWithHint("BEAN", "FASOLE", "🥜", "Food", "Mâncare"),
        // 5-letter (EN)
        WordWithHint("PIZZA", "PIZZA", "🍕", "Food", "Mâncare"),
        WordWithHint("APPLE", "MĂR", "🍎", "Food", "Mâncare"),
        WordWithHint("BREAD", "PÂINE", "🍞", "Food", "Mâncare"),
        WordWithHint("CANDY", "BOMBOANĂ", "🍬", "Food", "Mâncare"),
        WordWithHint("GRAPE", "STRUGURE", "🍇", "Food", "Mâncare"),
        WordWithHint("LEMON", "LĂMÂIE", "🍋", "Food", "Mâncare"),
        WordWithHint("MELON", "PEPENE", "🍈", "Food", "Mâncare"),
        WordWithHint("PEACH", "PIERSICĂ", "🍑", "Food", "Mâncare"),
        WordWithHint("SALAD", "SALATĂ", "🥗", "Food", "Mâncare"),
        WordWithHint("HONEY", "MIERE", "🍯", "Food", "Mâncare"),
        WordWithHint("DONUT", "GOGOAȘĂ", "🍩", "Food", "Mâncare"),
        WordWithHint("PASTA", "PASTE", "🍝", "Food", "Mâncare"),
        WordWithHint("JUICE", "SUC", "🧃", "Food", "Mâncare"),
        // 6-letter (EN)
        WordWithHint("BANANA", "BANANĂ", "🍌", "Food", "Mâncare"),
        WordWithHint("ORANGE", "PORTOCALĂ", "🍊", "Food", "Mâncare"),
        WordWithHint("CHERRY", "CIREAȘĂ", "🍒", "Food", "Mâncare"),
        WordWithHint("COOKIE", "BISCUIT", "🍪", "Food", "Mâncare"),
        WordWithHint("CARROT", "MORCOV", "🥕", "Food", "Mâncare"),
        WordWithHint("CHEESE", "BRÂNZĂ", "🧀", "Food", "Mâncare"),
        WordWithHint("BUTTER", "UNT", "🧈", "Food", "Mâncare"),
        WordWithHint("POTATO", "CARTOF", "🥔", "Food", "Mâncare"),
        WordWithHint("TOMATO", "ROȘIE", "🍅", "Food", "Mâncare"),
        WordWithHint("BURGER", "BURGER", "🍔", "Food", "Mâncare"),
        WordWithHint("MUFFIN", "BRIOȘĂ", "🧁", "Food", "Mâncare"),
        // 7-letter (EN)
        WordWithHint("POPCORN", "FLORICELE", "🍿", "Food", "Mâncare"),
        WordWithHint("PANCAKE", "CLĂTITĂ", "🥞", "Food", "Mâncare"),
        WordWithHint("PRETZEL", "COVRIG", "🥨", "Food", "Mâncare"),
        WordWithHint("CUPCAKE", "BRIOSCĂ", "🧁", "Food", "Mâncare"),
        WordWithHint("AVOCADO", "AVOCADO", "🥑", "Food", "Mâncare"),
        WordWithHint("COCONUT", "NUCĂ DE COCOS", "🥥", "Food", "Mâncare"),
        // 8-letter (EN)
        WordWithHint("SANDWICH", "SANDVIȘ", "🥪", "Food", "Mâncare"),
        WordWithHint("BROCCOLI", "BROCCOLI", "🥦", "Food", "Mâncare"),
        WordWithHint("MUSHROOM", "CIUPERCĂ", "🍄", "Food", "Mâncare"),
        WordWithHint("ICECREAM", "ÎNGHEȚATĂ", "🍦", "Food", "Mâncare")
    )

    val nature = listOf(
        // 3-letter (EN)
        WordWithHint("SUN", "SOARE", "☀️", "Nature", "Natură"),
        WordWithHint("SKY", "CER", "🌤️", "Nature", "Natură"),
        WordWithHint("SEA", "MARE", "🌊", "Nature", "Natură"),
        // 4-letter (EN)
        WordWithHint("MOON", "LUNĂ", "🌙", "Nature", "Natură"),
        WordWithHint("STAR", "STEA", "⭐", "Nature", "Natură"),
        WordWithHint("TREE", "COPAC", "🌳", "Nature", "Natură"),
        WordWithHint("LEAF", "FRUNZĂ", "🍃", "Nature", "Natură"),
        WordWithHint("RAIN", "PLOAIE", "🌧️", "Nature", "Natură"),
        WordWithHint("SNOW", "ZĂPADĂ", "❄️", "Nature", "Natură"),
        WordWithHint("WIND", "VÂNT", "💨", "Nature", "Natură"),
        WordWithHint("ROSE", "TRANDAFIR", "🌹", "Nature", "Natură"),
        WordWithHint("ROCK", "PIATRĂ", "⛰️", "Nature", "Natură"),
        WordWithHint("POND", "IAZPOND", "🏞️", "Nature", "Natură"),
        WordWithHint("LAKE", "LAC", "🏞️", "Nature", "Natură"),
        WordWithHint("CAVE", "PEȘTERĂ", "🕳️", "Nature", "Natură"),
        WordWithHint("HILL", "DEAL", "⛰️", "Nature", "Natură"),
        // 5-letter (EN)
        WordWithHint("CLOUD", "NOR", "☁️", "Nature", "Natură"),
        WordWithHint("STORM", "FURTUNĂ", "⛈️", "Nature", "Natură"),
        WordWithHint("BEACH", "PLAJĂ", "🏖️", "Nature", "Natură"),
        WordWithHint("OCEAN", "OCEAN", "🌊", "Nature", "Natură"),
        WordWithHint("RIVER", "RÂU", "🏞️", "Nature", "Natură"),
        WordWithHint("GRASS", "IARBĂ", "🌿", "Nature", "Natură"),
        WordWithHint("PLANT", "PLANTĂ", "🌱", "Nature", "Natură"),
        WordWithHint("EARTH", "PĂMÂNT", "🌍", "Nature", "Natură"),
        WordWithHint("STONE", "PIATRĂ", "💎", "Nature", "Natură"),
        // 6-letter (EN)
        WordWithHint("FLOWER", "FLOARE", "🌸", "Nature", "Natură"),
        WordWithHint("GARDEN", "GRĂDINĂ", "🌷", "Nature", "Natură"),
        WordWithHint("FOREST", "PĂDURE", "🌲", "Nature", "Natură"),
        WordWithHint("DESERT", "DEȘERT", "🏜️", "Nature", "Natură"),
        WordWithHint("ISLAND", "INSULĂ", "🏝️", "Nature", "Natură"),
        WordWithHint("SUNSET", "APUS", "🌅", "Nature", "Natură"),
        WordWithHint("JUNGLE", "JUNGLĂ", "🌴", "Nature", "Natură"),
        // 7-letter (EN)
        WordWithHint("RAINBOW", "CURCUBEU", "🌈", "Nature", "Natură"),
        WordWithHint("VOLCANO", "VULCAN", "🌋", "Nature", "Natură"),
        WordWithHint("THUNDER", "TUNET", "⚡", "Nature", "Natură"),
        // 8-letter (EN)
        WordWithHint("MOUNTAIN", "MUNTE", "⛰️", "Nature", "Natură"),
        WordWithHint("SUNSHINE", "LUMINĂ", "☀️", "Nature", "Natură"),
        WordWithHint("SNOWFLAKE", "FULG DE ZĂPADĂ", "❄️", "Nature", "Natură")
    )

    val things = listOf(
        // 3-letter (EN)
        WordWithHint("BUS", "AUTOBUZ", "🚌", "Things", "Obiecte"),
        WordWithHint("CAR", "MAȘINĂ", "🚗", "Things", "Obiecte"),
        WordWithHint("HAT", "PĂLĂRIE", "🎩", "Things", "Obiecte"),
        WordWithHint("BAG", "GEANTĂ", "👜", "Things", "Obiecte"),
        WordWithHint("BED", "PAT", "🛏️", "Things", "Obiecte"),
        WordWithHint("CUP", "CANĂ", "☕", "Things", "Obiecte"),
        WordWithHint("KEY", "CHEIE", "🔑", "Things", "Obiecte"),
        WordWithHint("BOX", "CUTIE", "📦", "Things", "Obiecte"),
        WordWithHint("PEN", "STILOU", "🖊️", "Things", "Obiecte"),
        WordWithHint("TOY", "JUCĂRIE", "🧸", "Things", "Obiecte"),
        // 4-letter (EN)
        WordWithHint("BALL", "MINGE", "⚽", "Things", "Obiecte"),
        WordWithHint("BELL", "CLOPOȚEL", "🔔", "Things", "Obiecte"),
        WordWithHint("BOOK", "CARTE", "📖", "Things", "Obiecte"),
        WordWithHint("BOAT", "BARCĂ", "⛵", "Things", "Obiecte"),
        WordWithHint("BIKE", "BICICLETĂ", "🚲", "Things", "Obiecte"),
        WordWithHint("DOOR", "UȘĂ", "🚪", "Things", "Obiecte"),
        WordWithHint("GIFT", "CADOU", "🎁", "Things", "Obiecte"),
        WordWithHint("KITE", "ZMEU", "🎐", "Things", "Obiecte"),
        WordWithHint("LAMP", "LAMPĂ", "💡", "Things", "Obiecte"),
        WordWithHint("DRUM", "TOBĂ", "🥁", "Things", "Obiecte"),
        WordWithHint("SHOE", "PANTOF", "👟", "Things", "Obiecte"),
        WordWithHint("SOCK", "ȘOSETĂ", "🧦", "Things", "Obiecte"),
        WordWithHint("RING", "INEL", "💍", "Things", "Obiecte"),
        WordWithHint("COIN", "MONEDĂ", "💰", "Things", "Obiecte"),
        // 5-letter (EN)
        WordWithHint("HOUSE", "CASĂ", "🏠", "Things", "Obiecte"),
        WordWithHint("PHONE", "TELEFON", "📱", "Things", "Obiecte"),
        WordWithHint("CHAIR", "SCAUN", "🪑", "Things", "Obiecte"),
        WordWithHint("TABLE", "MASĂ", "🪑", "Things", "Obiecte"),
        WordWithHint("PIANO", "PIAN", "🎹", "Things", "Obiecte"),
        WordWithHint("CLOCK", "CEAS", "🕐", "Things", "Obiecte"),
        WordWithHint("PLANE", "AVION", "✈️", "Things", "Obiecte"),
        WordWithHint("TRAIN", "TREN", "🚂", "Things", "Obiecte"),
        WordWithHint("TRUCK", "CAMION", "🚚", "Things", "Obiecte"),
        WordWithHint("CROWN", "COROANĂ", "👑", "Things", "Obiecte"),
        WordWithHint("BRUSH", "PENSULĂ", "🖌️", "Things", "Obiecte"),
        WordWithHint("SPOON", "LINGURĂ", "🥄", "Things", "Obiecte"),
        WordWithHint("WATCH", "CEAS", "⌚", "Things", "Obiecte"),
        WordWithHint("BROOM", "MĂTURĂ", "🧹", "Things", "Obiecte"),
        // 6-letter (EN)
        WordWithHint("BOTTLE", "STICLĂ", "🍼", "Things", "Obiecte"),
        WordWithHint("BASKET", "COȘ", "🧺", "Things", "Obiecte"),
        WordWithHint("CAMERA", "CAMERĂ", "📷", "Things", "Obiecte"),
        WordWithHint("MIRROR", "OGLINDĂ", "✨", "Things", "Obiecte"),
        WordWithHint("CANDLE", "LUMÂNARE", "🕯️", "Things", "Obiecte"),
        WordWithHint("PENCIL", "CREION", "✏️", "Things", "Obiecte"),
        WordWithHint("GUITAR", "CHITARĂ", "🎸", "Things", "Obiecte"),
        WordWithHint("ROCKET", "RACHETĂ", "🚀", "Things", "Obiecte"),
        WordWithHint("WINDOW", "FEREASTRĂ", "🏠", "Things", "Obiecte"),
        WordWithHint("PILLOW", "PERNĂ", "🛋️", "Things", "Obiecte"),
        WordWithHint("BUCKET", "GĂLEATĂ", "🧺", "Things", "Obiecte"),
        // 7-letter (EN)
        WordWithHint("BALLOON", "BALON", "🎈", "Things", "Obiecte"),
        WordWithHint("BICYCLE", "BICICLETĂ", "🚲", "Things", "Obiecte"),
        WordWithHint("PRESENT", "CADOU", "🎁", "Things", "Obiecte"),
        WordWithHint("PICTURE", "POZĂ", "🖼️", "Things", "Obiecte"),
        WordWithHint("COMPASS", "BUSOLĂ", "🧭", "Things", "Obiecte"),
        WordWithHint("BLANKET", "PĂTURĂ", "🛏️", "Things", "Obiecte"),
        WordWithHint("TRUMPET", "TROMPETĂ", "🎺", "Things", "Obiecte"),
        // 8-letter (EN)
        WordWithHint("UMBRELLA", "UMBRELĂ", "☂️", "Things", "Obiecte"),
        WordWithHint("SCISSORS", "FOARFECE", "✂️", "Things", "Obiecte"),
        WordWithHint("COMPUTER", "CALCULATOR", "💻", "Things", "Obiecte"),
        WordWithHint("BACKPACK", "RUCSAC", "🎒", "Things", "Obiecte"),
        WordWithHint("KEYBOARD", "TASTATURĂ", "⌨️", "Things", "Obiecte"),
        WordWithHint("TREASURE", "COMOARĂ", "💎", "Things", "Obiecte"),
        WordWithHint("AIRPLANE", "AVION", "✈️", "Things", "Obiecte")
    )

    val body = listOf(
        // 3-letter (EN)
        WordWithHint("EAR", "URECHE", "👂", "Body", "Corpul"),
        WordWithHint("EYE", "OCHI", "👁️", "Body", "Corpul"),
        WordWithHint("ARM", "BRAȚ", "💪", "Body", "Corpul"),
        WordWithHint("LEG", "PICIOR", "🦵", "Body", "Corpul"),
        WordWithHint("TOE", "DEGET", "🦶", "Body", "Corpul"),
        WordWithHint("LIP", "BUZĂ", "👄", "Body", "Corpul"),
        // 4-letter (EN)
        WordWithHint("NOSE", "NAS", "👃", "Body", "Corpul"),
        WordWithHint("HAND", "MÂNĂ", "✋", "Body", "Corpul"),
        WordWithHint("FOOT", "PICIOR", "🦶", "Body", "Corpul"),
        WordWithHint("HEAD", "CAP", "🗣️", "Body", "Corpul"),
        WordWithHint("FACE", "FAȚĂ", "😊", "Body", "Corpul"),
        WordWithHint("BACK", "SPATE", "🔙", "Body", "Corpul"),
        WordWithHint("NECK", "GÂT", "🦒", "Body", "Corpul"),
        WordWithHint("KNEE", "GENUNCHI", "🦵", "Body", "Corpul"),
        WordWithHint("HAIR", "PĂR", "💇", "Body", "Corpul"),
        WordWithHint("CHIN", "BĂRBIE", "🧔", "Body", "Corpul"),
        // 5-letter (EN)
        WordWithHint("HEART", "INIMĂ", "❤️", "Body", "Corpul"),
        WordWithHint("BRAIN", "CREIER", "🧠", "Body", "Corpul"),
        WordWithHint("ELBOW", "COT", "💪", "Body", "Corpul"),
        WordWithHint("THUMB", "DEGET MARE", "👍", "Body", "Corpul"),
        WordWithHint("TEETH", "DINȚI", "🦷", "Body", "Corpul"),
        WordWithHint("MOUTH", "GURĂ", "👄", "Body", "Corpul"),
        WordWithHint("CHEEK", "OBRAZ", "😊", "Body", "Corpul"),
        WordWithHint("BELLY", "BURTICĂ", "😊", "Body", "Corpul"),
        // 6-letter (EN)
        WordWithHint("FINGER", "DEGET", "👆", "Body", "Corpul"),
        WordWithHint("TONGUE", "LIMBĂ", "👅", "Body", "Corpul"),
        // 7-letter (EN)
        WordWithHint("EYEBROW", "SPRÂNCEANĂ", "🤨", "Body", "Corpul"),
        WordWithHint("STOMACH", "STOMAC", "😊", "Body", "Corpul"),
        // 8-letter (EN)
        WordWithHint("SHOULDER", "UMĂR", "💪", "Body", "Corpul"),
        WordWithHint("FOREHEAD", "FRUNTE", "🧠", "Body", "Corpul")
    )

    val places = listOf(
        // 4-letter (EN)
        WordWithHint("HOME", "ACASĂ", "🏠", "Places", "Locuri"),
        WordWithHint("PARK", "PARC", "🏞️", "Places", "Locuri"),
        WordWithHint("FARM", "FERMĂ", "🚜", "Places", "Locuri"),
        WordWithHint("CITY", "ORAȘ", "🏙️", "Places", "Locuri"),
        WordWithHint("SHOP", "MAGAZIN", "🏪", "Places", "Locuri"),
        WordWithHint("MALL", "MALL", "🛒", "Places", "Locuri"),
        WordWithHint("BANK", "BANCĂ", "🏦", "Places", "Locuri"),
        WordWithHint("POOL", "PISCINĂ", "🏊", "Places", "Locuri"),
        // 5-letter (EN)
        WordWithHint("BEACH", "PLAJĂ", "🏖️", "Places", "Locuri"),
        WordWithHint("HOTEL", "HOTEL", "🏨", "Places", "Locuri"),
        WordWithHint("STORE", "MAGAZIN", "🏬", "Places", "Locuri"),
        WordWithHint("TOWER", "TURN", "🗼", "Places", "Locuri"),
        WordWithHint("SPACE", "SPAȚIU", "🚀", "Places", "Locuri"),
        // 6-letter (EN)
        WordWithHint("SCHOOL", "ȘCOALĂ", "🏫", "Places", "Locuri"),
        WordWithHint("CASTLE", "CASTEL", "🏰", "Places", "Locuri"),
        WordWithHint("MUSEUM", "MUZEU", "🏛️", "Places", "Locuri"),
        WordWithHint("CHURCH", "BISERICĂ", "⛪", "Places", "Locuri"),
        WordWithHint("JUNGLE", "JUNGLĂ", "🌴", "Places", "Locuri"),
        WordWithHint("CIRCUS", "CIRC", "🎪", "Places", "Locuri"),
        WordWithHint("OFFICE", "BIROU", "🏢", "Places", "Locuri"),
        // 7-letter (EN)
        WordWithHint("AIRPORT", "AEROPORT", "✈️", "Places", "Locuri"),
        WordWithHint("LIBRARY", "BIBLIOTECĂ", "📚", "Places", "Locuri"),
        WordWithHint("THEATER", "TEATRU", "🎭", "Places", "Locuri"),
        WordWithHint("STADIUM", "STADION", "🏟️", "Places", "Locuri"),
        WordWithHint("KITCHEN", "BUCĂTĂRIE", "🍳", "Places", "Locuri"),
        WordWithHint("BEDROOM", "DORMITOR", "🛏️", "Places", "Locuri"),
        // 8-letter (EN)
        WordWithHint("HOSPITAL", "SPITAL", "🏥", "Places", "Locuri"),
        WordWithHint("BATHROOM", "BAIE", "🚿", "Places", "Locuri"),
        WordWithHint("BACKYARD", "CURTE", "🏡", "Places", "Locuri"),
        WordWithHint("BUILDING", "CLĂDIRE", "🏗️", "Places", "Locuri"),
        WordWithHint("MOUNTAIN", "MUNTE", "⛰️", "Places", "Locuri"),
        WordWithHint("PLAYROOM", "CAMERĂ DE JOACĂ", "🧸", "Places", "Locuri")
    )

    val activities = listOf(
        // 3-letter (EN)
        WordWithHint("RUN", "ALEARGĂ", "🏃", "Activities", "Activități"),
        WordWithHint("EAT", "MĂNÂNCĂ", "🍽️", "Activities", "Activități"),
        WordWithHint("FLY", "ZBOARĂ", "✈️", "Activities", "Activități"),
        WordWithHint("SIT", "STAI", "🪑", "Activities", "Activități"),
        // 4-letter (EN)
        WordWithHint("PLAY", "JOACĂ", "🎮", "Activities", "Activități"),
        WordWithHint("SWIM", "ÎNOATĂ", "🏊", "Activities", "Activități"),
        WordWithHint("JUMP", "SARI", "🦘", "Activities", "Activități"),
        WordWithHint("RIDE", "CĂLĂREȘTE", "🚴", "Activities", "Activități"),
        WordWithHint("COOK", "GĂTEȘTE", "👨‍🍳", "Activities", "Activități"),
        WordWithHint("DRAW", "DESENEAZĂ", "🎨", "Activities", "Activități"),
        WordWithHint("READ", "CITEȘTE", "📖", "Activities", "Activități"),
        WordWithHint("SING", "CÂNTĂ", "🎤", "Activities", "Activități"),
        WordWithHint("WALK", "MERGE", "🚶", "Activities", "Activități"),
        // 5-letter (EN)
        WordWithHint("DANCE", "DANSEAZĂ", "💃", "Activities", "Activități"),
        WordWithHint("SLEEP", "DOARME", "😴", "Activities", "Activități"),
        WordWithHint("CLIMB", "URCĂ", "🧗", "Activities", "Activități"),
        WordWithHint("PAINT", "PICTEAZĂ", "🎨", "Activities", "Activități"),
        WordWithHint("WRITE", "SCRIE", "✍️", "Activities", "Activități"),
        WordWithHint("SKATE", "PATINEAZĂ", "⛸️", "Activities", "Activități")
    )

    val allWords: List<WordWithHint> = animals + food + nature + things + body + places + activities

    fun getRandomWord(level: Int, isRomanian: Boolean): WordWithHint {
        // Filter by word length based on level (progressive difficulty)
        // Use Romanian word length if Romanian, else English
        val maxLength = when (level) {
            1 -> 4  // Short words
            2 -> 5  // Up to 5-letter words
            3 -> 6  // Up to 6-letter words
            4 -> 6  // 6-letter words preferred
            5 -> 7  // Up to 7-letter words
            6 -> 7  // 7-letter words preferred
            7 -> 8  // Up to 8-letter words
            else -> 10  // Longer words
        }

        val minLength = when (level) {
            1 -> 2
            2 -> 3
            3 -> 4
            4 -> 5
            5 -> 5
            6 -> 6
            7 -> 6
            else -> 7
        }

        val eligibleWords = allWords.filter { word ->
            val wordToCheck = if (isRomanian) word.wordRo else word.wordEn
            wordToCheck.length in minLength..maxLength
        }

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
    val isRomanian: Boolean = false,
    val guessedLetters: Set<Char> = emptySet(),
    val wrongGuesses: Int = 0
) {
    val word: String get() = wordWithHint.getWord(isRomanian)
    val hint: String get() = wordWithHint.hint
    val category: String get() = wordWithHint.getCategory(isRomanian)

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
