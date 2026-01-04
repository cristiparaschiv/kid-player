package com.kidplayer.app.presentation.games.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.games.common.GameIconType
import com.kidplayer.app.presentation.games.common.GameInfo
import com.kidplayer.app.presentation.util.bouncyClickable
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Game selection card with drawable icon on white box with colored shadow
 * Uses bouncy animation for playful feedback
 */
@Composable
fun GameCard(
    gameInfo: GameInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()
    val accentColor = Color(gameInfo.backgroundColor)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
                ambientColor = accentColor.copy(alpha = 0.4f),
                spotColor = accentColor.copy(alpha = 0.6f)
            )
            .bouncyClickable(
                scaleOnPress = 0.95f,
                enabled = gameInfo.isAvailable,
                onClick = {
                    haptic.performLight()
                    onClick()
                }
            ),
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (gameInfo.isAvailable) {
                Color.White
            } else {
                Color.Gray.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon container with white background and colored shadow
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = accentColor.copy(alpha = 0.3f),
                        spotColor = accentColor.copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                GameIcon(
                    iconType = gameInfo.iconType,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Game name with accent color
            Text(
                text = stringResource(gameInfo.nameResId).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (gameInfo.isAvailable) accentColor else Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Description
            Text(
                text = stringResource(gameInfo.descriptionResId).uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            if (!gameInfo.isAvailable) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.game_coming_soon),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Game icon using drawable resources
 */
@Composable
fun GameIcon(
    iconType: GameIconType,
    modifier: Modifier = Modifier
) {
    val iconRes = when (iconType) {
        GameIconType.MEMORY -> R.drawable.ic_game_memory
        GameIconType.TICTACTOE -> R.drawable.ic_game_tictactoe
        GameIconType.PUZZLE -> R.drawable.ic_game_puzzle
        GameIconType.MATCH3 -> R.drawable.ic_game_match3
        GameIconType.COLORING -> R.drawable.ic_game_coloring
        GameIconType.SLIDING -> R.drawable.ic_game_sliding
        GameIconType.GRIDPUZZLE -> R.drawable.ic_game_gridpuzzle
        GameIconType.PATTERN -> R.drawable.ic_game_pattern
        GameIconType.COLORMIX -> R.drawable.ic_game_colormix
        GameIconType.LETTERMATCH -> R.drawable.ic_game_lettermatch
        GameIconType.MAZE -> R.drawable.ic_game_maze
        GameIconType.DOTS -> R.drawable.ic_game_dots
        GameIconType.ADDITION -> R.drawable.ic_game_addition
        GameIconType.SUBTRACTION -> R.drawable.ic_game_subtraction
        GameIconType.NUMBERBONDS -> R.drawable.ic_game_numberbonds
        GameIconType.COMPARE -> R.drawable.ic_game_compare
        GameIconType.ODDONEOUT -> R.drawable.ic_game_oddoneout
        GameIconType.SUDOKU -> R.drawable.ic_game_sudoku
        GameIconType.BALLSORT -> R.drawable.ic_game_ballsort
        GameIconType.HANGMAN -> R.drawable.ic_game_hangman
        GameIconType.CROSSWORD -> R.drawable.ic_game_crossword
        GameIconType.COUNTING -> R.drawable.ic_game_counting
        GameIconType.SHAPES -> R.drawable.ic_game_shapes
        GameIconType.SPELLING -> R.drawable.ic_game_spelling
        GameIconType.WORDSEARCH -> R.drawable.ic_game_wordsearch
        GameIconType.SPOTDIFF -> R.drawable.ic_game_spotdiff
        GameIconType.ANALOGCLOCK -> R.drawable.ic_game_analog_clock
    }

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
