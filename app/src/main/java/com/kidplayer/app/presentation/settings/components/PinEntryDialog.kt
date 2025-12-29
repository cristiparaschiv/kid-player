package com.kidplayer.app.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kidplayer.app.R
import com.kidplayer.app.presentation.theme.KidPlayerTheme

/**
 * PIN entry dialog with kid-friendly large buttons
 * Used for both setting and verifying parent PIN
 */
@Composable
fun PinEntryDialog(
    isVisible: Boolean,
    title: String,
    subtitle: String? = null,
    isSetupMode: Boolean = false,
    onPinEntered: (String) -> Unit,
    onDismiss: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    if (!isVisible) return

    var pinInput by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirmPhase by remember { mutableStateOf(false) }

    // Reset state when dialog opens
    LaunchedEffect(isVisible) {
        if (isVisible) {
            pinInput = ""
            confirmPin = ""
            isConfirmPhase = false
        }
    }

    // Auto-submit when 4 digits entered
    LaunchedEffect(pinInput) {
        if (pinInput.length == 4) {
            if (isSetupMode) {
                if (!isConfirmPhase) {
                    // First entry complete, move to confirm
                    isConfirmPhase = true
                    confirmPin = pinInput
                    pinInput = ""
                } else {
                    // Confirm phase complete
                    if (pinInput == confirmPin) {
                        onPinEntered(pinInput)
                        pinInput = ""
                        confirmPin = ""
                        isConfirmPhase = false
                    } else {
                        // PINs don't match, reset
                        pinInput = ""
                        isConfirmPhase = false
                        confirmPin = ""
                    }
                }
            } else {
                // Verify mode - submit immediately
                onPinEntered(pinInput)
                pinInput = ""
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lock icon
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                val confirmPinText = stringResource(R.string.pin_confirm)
                Text(
                    text = if (isSetupMode && isConfirmPhase) confirmPinText else title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Subtitle
                if (subtitle != null && !isConfirmPhase) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PIN dots
                PinDotsIndicator(
                    pinLength = pinInput.length,
                    isError = isError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                if (isError && errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (isSetupMode && isConfirmPhase) {
                    Text(
                        text = stringResource(R.string.pin_reenter),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Number pad
                PinPad(
                    onNumberClick = { number ->
                        if (pinInput.length < 4) {
                            pinInput += number
                        }
                    },
                    onBackspaceClick = {
                        if (pinInput.isNotEmpty()) {
                            pinInput = pinInput.dropLast(1)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Visual indicator showing how many PIN digits have been entered
 */
@Composable
private fun PinDotsIndicator(
    pinLength: Int,
    isError: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isError -> MaterialTheme.colorScheme.error
                            index < pinLength -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
            )
        }
    }
}

/**
 * Kid-friendly number pad with large buttons
 * Fixed button size to prevent overlap
 */
@Composable
private fun PinPad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit
) {
    val numbers = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫")
    )

    // Fixed button size - large enough for kids, but won't overlap
    val buttonSize = 64.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        numbers.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { digit ->
                    if (digit.isEmpty()) {
                        // Empty spacer with same size as buttons
                        Spacer(modifier = Modifier.size(buttonSize))
                    } else if (digit == "⌫") {
                        // Backspace button
                        FilledIconButton(
                            onClick = onBackspaceClick,
                            modifier = Modifier.size(buttonSize),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = stringResource(R.string.pin_backspace),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        // Number button
                        FilledTonalButton(
                            onClick = { onNumberClick(digit) },
                            modifier = Modifier.size(buttonSize),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = digit,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "PIN Entry Dialog - Tablet",
    widthDp = 800,
    heightDp = 600
)
@Composable
private fun PinEntryDialogPreview() {
    KidPlayerTheme {
        PinEntryDialog(
            isVisible = true,
            title = "Enter Parent PIN",
            subtitle = "Enter your 4-digit PIN to access settings",
            onPinEntered = {},
            onDismiss = {}
        )
    }
}

@Preview(
    name = "PIN Entry Dialog - Error",
    widthDp = 800,
    heightDp = 600
)
@Composable
private fun PinEntryDialogErrorPreview() {
    KidPlayerTheme {
        PinEntryDialog(
            isVisible = true,
            title = "Enter Parent PIN",
            subtitle = "Enter your 4-digit PIN to access settings",
            onPinEntered = {},
            onDismiss = {},
            isError = true,
            errorMessage = "Incorrect PIN. Please try again."
        )
    }
}

@Preview(
    name = "PIN Setup Dialog",
    widthDp = 800,
    heightDp = 600
)
@Composable
private fun PinSetupDialogPreview() {
    KidPlayerTheme {
        PinEntryDialog(
            isVisible = true,
            title = "Set Parent PIN",
            subtitle = "Create a 4-digit PIN to protect settings",
            isSetupMode = true,
            onPinEntered = {},
            onDismiss = {}
        )
    }
}
