package com.kidplayer.app.presentation.games.hangman

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun HangmanScreen(
    onNavigateBack: () -> Unit,
    viewModel: HangmanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    val configuration = LocalConfiguration.current
    val isCompactHeight = configuration.screenHeightDp < 480

    GameScaffold(
        gameName = stringResource(R.string.game_hangman_name),
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        uiState.currentPuzzle?.let { puzzle ->
            if (isCompactHeight) {
                CompactHangmanLayout(
                    round = uiState.round,
                    totalRounds = HangmanConfig.TOTAL_ROUNDS,
                    wordsGuessed = uiState.wordsGuessed,
                    puzzle = puzzle,
                    showResult = uiState.showResult,
                    onLetterClick = { letter ->
                        haptic.performMedium()
                        viewModel.guessLetter(letter)
                    }
                )
            } else {
                // Standard vertical layout for larger screens
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RoundInfo(
                        round = uiState.round,
                        totalRounds = HangmanConfig.TOTAL_ROUNDS,
                        wordsGuessed = uiState.wordsGuessed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.35f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HangmanFigure(
                            wrongGuesses = puzzle.wrongGuesses,
                            modifier = Modifier.size(100.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = puzzle.hint, fontSize = 56.sp)
                            Text(
                                text = puzzle.category.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    WordDisplay(
                        displayWord = puzzle.displayWord,
                        isWon = puzzle.isWon,
                        isLost = puzzle.isLost,
                        actualWord = puzzle.word,
                        isCompact = false
                    )

                    AnimatedVisibility(
                        visible = uiState.showResult,
                        enter = scaleIn() + fadeIn()
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (puzzle.isWon) Color(0xFF4CAF50) else Color(0xFFFF9800)
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = if (puzzle.isWon) "GREAT JOB!" else "THE WORD WAS: ${puzzle.word.uppercase()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LetterKeyboard(
                        guessedLetters = puzzle.guessedLetters,
                        correctLetters = puzzle.correctLetters,
                        incorrectLetters = puzzle.incorrectLetters,
                        enabled = !puzzle.isGameOver && !uiState.showResult,
                        onLetterClick = { letter ->
                            haptic.performMedium()
                            viewModel.guessLetter(letter)
                        },
                        modifier = Modifier.weight(0.5f),
                        isCompact = false,
                        isRomanian = puzzle.isRomanian
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactHangmanLayout(
    round: Int,
    totalRounds: Int,
    wordsGuessed: Int,
    puzzle: HangmanPuzzle,
    showResult: Boolean,
    onLetterClick: (Char) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Hangman figure, hint, word display
        Column(
            modifier = Modifier.weight(0.35f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Compact round indicator
            CompactRoundIndicator(round = round, totalRounds = totalRounds, wordsGuessed = wordsGuessed)

            Spacer(modifier = Modifier.height(4.dp))

            // Hangman figure and hint side by side
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HangmanFigure(
                    wrongGuesses = puzzle.wrongGuesses,
                    modifier = Modifier.size(60.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = puzzle.hint, fontSize = 32.sp)
                    Text(
                        text = puzzle.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Word display
            WordDisplay(
                displayWord = puzzle.displayWord,
                isWon = puzzle.isWon,
                isLost = puzzle.isLost,
                actualWord = puzzle.word,
                isCompact = true
            )

            // Result message (compact)
            AnimatedVisibility(
                visible = showResult,
                enter = scaleIn() + fadeIn()
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (puzzle.isWon) Color(0xFF4CAF50) else Color(0xFFFF9800)
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = if (puzzle.isWon) "GREAT!" else puzzle.word.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Right side: Keyboard (fills remaining space)
        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            LetterKeyboard(
                guessedLetters = puzzle.guessedLetters,
                correctLetters = puzzle.correctLetters,
                incorrectLetters = puzzle.incorrectLetters,
                enabled = !puzzle.isGameOver && !showResult,
                onLetterClick = onLetterClick,
                isCompact = true,
                isRomanian = puzzle.isRomanian
            )
        }
    }
}

@Composable
private fun CompactRoundIndicator(round: Int, totalRounds: Int, wordsGuessed: Int) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "$round/$totalRounds", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(text = "✓$wordsGuessed", style = MaterialTheme.typography.labelLarge, color = Color(0xFF4CAF50))
        }
    }
}

@Composable
private fun RoundInfo(
    round: Int,
    totalRounds: Int,
    wordsGuessed: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUND",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$round/$totalRounds",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WORDS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$wordsGuessed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun HangmanFigure(
    wrongGuesses: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val figureColor = Color(0xFF5D4037)
        val gallowsColor = Color(0xFF795548)

        // Gallows base
        drawLine(
            color = gallowsColor,
            start = Offset(size.width * 0.1f, size.height * 0.95f),
            end = Offset(size.width * 0.9f, size.height * 0.95f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Gallows pole
        drawLine(
            color = gallowsColor,
            start = Offset(size.width * 0.25f, size.height * 0.95f),
            end = Offset(size.width * 0.25f, size.height * 0.1f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Gallows top
        drawLine(
            color = gallowsColor,
            start = Offset(size.width * 0.25f, size.height * 0.1f),
            end = Offset(size.width * 0.65f, size.height * 0.1f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Rope
        drawLine(
            color = gallowsColor,
            start = Offset(size.width * 0.65f, size.height * 0.1f),
            end = Offset(size.width * 0.65f, size.height * 0.2f),
            strokeWidth = strokeWidth * 0.7f,
            cap = StrokeCap.Round
        )

        // Draw figure parts based on wrong guesses
        val centerX = size.width * 0.65f
        val headRadius = size.width * 0.1f
        val headCenterY = size.height * 0.2f + headRadius

        // Head (1 wrong guess)
        if (wrongGuesses >= 1) {
            drawCircle(
                color = figureColor,
                radius = headRadius,
                center = Offset(centerX, headCenterY),
                style = Stroke(width = strokeWidth)
            )
            // Simple face
            val eyeY = headCenterY - headRadius * 0.2f
            val eyeOffset = headRadius * 0.35f
            // Eyes
            drawCircle(color = figureColor, radius = 3.dp.toPx(), center = Offset(centerX - eyeOffset, eyeY))
            drawCircle(color = figureColor, radius = 3.dp.toPx(), center = Offset(centerX + eyeOffset, eyeY))
        }

        // Body (2 wrong guesses)
        if (wrongGuesses >= 2) {
            val bodyTop = headCenterY + headRadius
            val bodyBottom = size.height * 0.6f
            drawLine(
                color = figureColor,
                start = Offset(centerX, bodyTop),
                end = Offset(centerX, bodyBottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Left arm (3 wrong guesses)
        if (wrongGuesses >= 3) {
            val armY = size.height * 0.4f
            drawLine(
                color = figureColor,
                start = Offset(centerX, armY),
                end = Offset(centerX - size.width * 0.15f, armY + size.height * 0.1f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Right arm (4 wrong guesses)
        if (wrongGuesses >= 4) {
            val armY = size.height * 0.4f
            drawLine(
                color = figureColor,
                start = Offset(centerX, armY),
                end = Offset(centerX + size.width * 0.15f, armY + size.height * 0.1f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Left leg (5 wrong guesses)
        if (wrongGuesses >= 5) {
            val legTop = size.height * 0.6f
            drawLine(
                color = figureColor,
                start = Offset(centerX, legTop),
                end = Offset(centerX - size.width * 0.12f, size.height * 0.8f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Right leg (6 wrong guesses - game over)
        if (wrongGuesses >= 6) {
            val legTop = size.height * 0.6f
            drawLine(
                color = figureColor,
                start = Offset(centerX, legTop),
                end = Offset(centerX + size.width * 0.12f, size.height * 0.8f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun WordDisplay(
    displayWord: String,
    isWon: Boolean,
    isLost: Boolean,
    actualWord: String,
    isCompact: Boolean = false
) {
    val color = when {
        isWon -> Color(0xFF4CAF50)
        isLost -> Color(0xFFE53935)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = if (isLost) actualWord.map { "$it " }.joinToString("") else displayWord,
            fontSize = if (isCompact) 24.sp else 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = if (isCompact) 2.sp else 4.sp,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = if (isCompact) 16.dp else 24.dp,
                vertical = if (isCompact) 8.dp else 12.dp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LetterKeyboard(
    guessedLetters: Set<Char>,
    correctLetters: Set<Char>,
    incorrectLetters: Set<Char>,
    enabled: Boolean,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    isRomanian: Boolean = false
) {
    // Romanian keyboard includes special characters: Ă, Â, Î, Ș, Ț
    val rows = if (isRomanian) {
        listOf(
            "QWERTYUIOPĂÂ",
            "ASDFGHJKLÎȘ",
            "ZXCVBNMȚ"
        )
    } else {
        listOf(
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM"
        )
    }

    BoxWithConstraints(modifier = modifier) {
        // Calculate key size based on available width
        // Romanian has 12 keys in first row, English has 10
        val keysInRow = if (isRomanian) 12 else 10
        val maxKeyWidth = (maxWidth - 36.dp) / keysInRow
        val maxKeySize = if (isCompact) 40.dp else 56.dp
        val keySize = minOf(maxKeyWidth, maxKeySize)
        val fontSize = (keySize.value * 0.45f).sp
        val spacing = if (isCompact) 2.dp else minOf(4.dp, (maxWidth.value * 0.005f).dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    row.forEach { letter ->
                        val isGuessed = guessedLetters.contains(letter)
                        val isCorrect = correctLetters.contains(letter)
                        val isIncorrect = incorrectLetters.contains(letter)

                        val backgroundColor = when {
                            isCorrect -> Color(0xFF4CAF50)
                            isIncorrect -> Color(0xFFE57373)
                            !enabled -> Color(0xFFE0E0E0)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }

                        val scale by animateFloatAsState(
                            targetValue = if (isGuessed) 0.9f else 1f,
                            animationSpec = spring(dampingRatio = 0.6f),
                            label = "letterScale"
                        )

                        Card(
                            onClick = { if (enabled && !isGuessed) onLetterClick(letter) },
                            modifier = Modifier
                                .size(keySize)
                                .scale(scale),
                            shape = RoundedCornerShape(if (isCompact) 6.dp else 8.dp),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isGuessed) 0.dp else 2.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$letter",
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isCorrect || isIncorrect -> Color.White
                                        !enabled -> Color(0xFF9E9E9E)
                                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
