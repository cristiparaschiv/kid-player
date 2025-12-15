package com.kidplayer.app.presentation.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidplayer.app.presentation.settings.components.PinEntryDialog

/**
 * Full-screen blocking overlay shown when screen time limit is reached
 * Cannot be dismissed by the child - only via parent PIN verification
 */
@Composable
fun TimeLimitReachedOverlay(
    isVisible: Boolean,
    limitMinutes: Int,
    currentUsedMinutes: Int,
    onPinEntered: (String) -> Unit,
    onAddMinutes: (Int) -> Unit,
    onResetDaily: () -> Unit,
    pinError: String?,
    onDismissPinError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var showUnlockDialog by remember { mutableStateOf(false) }
    var lastPinError by remember { mutableStateOf<String?>(null) }

    // Monitor PIN verification success
    // When pinError goes from non-null to null, verification succeeded
    LaunchedEffect(pinError) {
        if (lastPinError != null && pinError == null && showPinDialog) {
            // PIN verification succeeded
            showPinDialog = false
            showUnlockDialog = true
        }
        lastPinError = pinError
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Timer icon
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Main message
                Text(
                    text = "Time's Up!",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Explanation
                Text(
                    text = "You've reached your screen time limit of $limitMinutes minutes for today.",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Come back tomorrow to watch more videos!",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Parent unlock button
                OutlinedButton(
                    onClick = { showPinDialog = true },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Parent Options",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    // PIN entry dialog for parent unlock
    if (showPinDialog) {
        PinEntryDialog(
            isVisible = true,
            title = "Parent PIN",
            subtitle = "Enter your PIN to manage screen time",
            isError = pinError != null,
            errorMessage = pinError,
            onPinEntered = { pin ->
                onPinEntered(pin)
                // Don't close immediately - wait for verification result via LaunchedEffect
            },
            onDismiss = {
                showPinDialog = false
                onDismissPinError()
            }
        )
    }

    // Parent unlock options dialog
    if (showUnlockDialog) {
        TimeLimitUnlockDialog(
            isVisible = true,
            currentUsedMinutes = currentUsedMinutes,
            dailyLimitMinutes = limitMinutes,
            onAddMinutes = { minutes ->
                onAddMinutes(minutes)
                showUnlockDialog = false
                showPinDialog = false
            },
            onResetDaily = {
                onResetDaily()
                showUnlockDialog = false
                showPinDialog = false
            },
            onDismiss = {
                showUnlockDialog = false
                showPinDialog = false
            }
        )
    }
}
