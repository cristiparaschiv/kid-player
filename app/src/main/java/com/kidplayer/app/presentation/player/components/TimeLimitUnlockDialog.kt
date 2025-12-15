package com.kidplayer.app.presentation.player.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Dialog shown to parents after PIN verification
 * Allows them to:
 * - Add 15/30/60 minutes
 * - Reset daily counter
 * - Cancel and keep limit
 */
@Composable
fun TimeLimitUnlockDialog(
    isVisible: Boolean,
    currentUsedMinutes: Int,
    dailyLimitMinutes: Int,
    onAddMinutes: (Int) -> Unit,
    onResetDaily: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Screen Time Options",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current usage info
                Text(
                    text = "Used today: $currentUsedMinutes of $dailyLimitMinutes minutes",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Add time options
                Text(
                    text = "Add More Time:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onAddMinutes(15)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+15 min")
                    }

                    Button(
                        onClick = {
                            onAddMinutes(30)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+30 min")
                    }

                    Button(
                        onClick = {
                            onAddMinutes(60)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+60 min")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                Spacer(modifier = Modifier.height(24.dp))

                // Reset daily counter
                Button(
                    onClick = {
                        onResetDaily()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Reset Daily Counter")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keep Limit")
                }
            }
        }
    }
}
