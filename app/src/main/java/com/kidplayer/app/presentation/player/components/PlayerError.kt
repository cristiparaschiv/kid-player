package com.kidplayer.app.presentation.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Kid-friendly error colors with good contrast
private val ErrorBackgroundColor = Color(0xFFE53935) // Bright red
private val ErrorTextColor = Color.White
private val ErrorIconBackgroundColor = Color.White
private val ErrorIconColor = Color(0xFFE53935)

/**
 * Error display for player screen
 * Shows kid-friendly error message with retry option
 * Uses high contrast colors for readability
 */
@Composable
fun PlayerError(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .background(
                    color = ErrorBackgroundColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(48.dp)
        ) {
            // Error icon with white background circle for visibility
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(ErrorIconBackgroundColor, RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = ErrorIconColor
                )
            }

            Text(
                text = "Oops! Something went wrong",
                color = ErrorTextColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                color = ErrorTextColor.copy(alpha = 0.95f),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 600.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Back button
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .height(64.dp)
                        .widthIn(min = 140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = ErrorBackgroundColor
                    )
                ) {
                    Text(
                        text = "Go Back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Retry button
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .height(64.dp)
                        .widthIn(min = 140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Try Again",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
