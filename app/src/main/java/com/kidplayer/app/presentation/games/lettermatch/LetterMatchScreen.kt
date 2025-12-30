package com.kidplayer.app.presentation.games.lettermatch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameScaffold
import com.kidplayer.app.presentation.games.common.GameState

@Composable
fun LetterMatchScreen(
    onNavigateBack: () -> Unit,
    viewModel: LetterMatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    GameScaffold(
        gameName = stringResource(R.string.game_lettermatch_name),
        gameId = "lettermatch",
        gameState = uiState.gameState,
        onBackClick = onNavigateBack,
        onPauseClick = { viewModel.pauseGame() },
        onRestartClick = { viewModel.startNewGame() },
        onResumeClick = { viewModel.resumeGame() },
        showScore = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Round indicator
            RoundIndicator(
                round = uiState.round,
                totalRounds = LetterMatchConfig.TOTAL_ROUNDS
            )

            uiState.currentPuzzle?.let { puzzle ->
                // Target display
                TargetDisplay(
                    puzzle = puzzle,
                    showResult = uiState.showResult,
                    isCorrect = uiState.isCorrect
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Instruction text
                Text(
                    text = when {
                        uiState.showResult && uiState.isCorrect -> "CORRECT!"
                        uiState.showResult -> "THE ANSWER WAS ${getCorrectAnswerText(puzzle).uppercase()}"
                        else -> getInstructionText(puzzle.mode)
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = when {
                        !uiState.showResult -> MaterialTheme.colorScheme.onSurface
                        uiState.isCorrect -> Color(0xFF4CAF50)
                        else -> Color(0xFFFF9800)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Options grid
                OptionsDisplay(
                    puzzle = puzzle,
                    selectedIndex = uiState.selectedIndex,
                    showResult = uiState.showResult,
                    onOptionClick = { index ->
                        haptic.performMedium()
                        viewModel.selectAnswer(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun RoundIndicator(
    round: Int,
    totalRounds: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "📚", fontSize = 24.sp)
            Text(
                text = "ROUND $round OF $totalRounds",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun TargetDisplay(
    puzzle: LetterMatchPuzzle,
    showResult: Boolean,
    isCorrect: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (showResult && isCorrect) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "targetScale"
    )

    Card(
        modifier = Modifier
            .scale(scale)
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LetterColors.forLetter(puzzle.targetLetter.letter)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (puzzle.mode) {
                LetterMatchMode.LETTER_TO_PICTURE -> {
                    // Show the letter
                    Text(
                        text = puzzle.targetLetter.letter.toString(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                LetterMatchMode.PICTURE_TO_LETTER -> {
                    // Show the picture (emoji)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = puzzle.targetLetter.emoji,
                            fontSize = 64.sp
                        )
                        Text(
                            text = puzzle.targetLetter.word,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                LetterMatchMode.UPPER_TO_LOWER -> {
                    // Show uppercase letter
                    Text(
                        text = puzzle.targetLetter.letter.toString(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionsDisplay(
    puzzle: LetterMatchPuzzle,
    selectedIndex: Int?,
    showResult: Boolean,
    onOptionClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        puzzle.options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            val isCorrect = index == puzzle.correctAnswerIndex

            val scale by animateFloatAsState(
                targetValue = when {
                    showResult && isCorrect -> 1.15f
                    showResult && isSelected && !isCorrect -> 0.85f
                    else -> 1f
                },
                animationSpec = spring(dampingRatio = 0.6f),
                label = "optionScale"
            )

            val borderColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFE53935)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> Color.Transparent
            }

            Card(
                onClick = { if (!showResult) onOptionClick(index) },
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 8.dp else 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (borderColor != Color.Transparent) {
                                Modifier.border(4.dp, borderColor, RoundedCornerShape(16.dp))
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (puzzle.mode) {
                        LetterMatchMode.LETTER_TO_PICTURE -> {
                            // Options are LetterPicture - show emojis
                            val letterPic = option as LetterPicture
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = letterPic.emoji,
                                    fontSize = 32.sp
                                )
                                Text(
                                    text = letterPic.word,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                        LetterMatchMode.PICTURE_TO_LETTER,
                        LetterMatchMode.UPPER_TO_LOWER -> {
                            // Options are Char - show letters
                            val letter = option as Char
                            Text(
                                text = letter.toString(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = LetterColors.forLetter(letter.uppercaseChar())
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getInstructionText(mode: LetterMatchMode): String {
    return when (mode) {
        LetterMatchMode.LETTER_TO_PICTURE -> "FIND THE PICTURE THAT STARTS WITH THIS LETTER"
        LetterMatchMode.PICTURE_TO_LETTER -> "WHAT LETTER DOES THIS START WITH?"
        LetterMatchMode.UPPER_TO_LOWER -> "FIND THE LOWERCASE LETTER"
    }
}

private fun getCorrectAnswerText(puzzle: LetterMatchPuzzle): String {
    val correct = puzzle.options[puzzle.correctAnswerIndex]
    return when (puzzle.mode) {
        LetterMatchMode.LETTER_TO_PICTURE -> (correct as LetterPicture).word
        LetterMatchMode.PICTURE_TO_LETTER -> (correct as Char).toString()
        LetterMatchMode.UPPER_TO_LOWER -> (correct as Char).toString()
    }
}
