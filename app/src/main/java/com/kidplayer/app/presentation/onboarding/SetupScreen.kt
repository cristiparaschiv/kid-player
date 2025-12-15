package com.kidplayer.app.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kidplayer.app.presentation.theme.KidPlayerTheme

/**
 * Setup/Onboarding Screen
 * Allows users to configure their Jellyfin server and authenticate
 */
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit = {},
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate to browse screen on successful authentication
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onSetupComplete()
            viewModel.resetAuthenticationState()
        }
    }

    SetupScreenContent(
        uiState = uiState,
        onServerUrlChanged = viewModel::onServerUrlChanged,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onTestConnection = viewModel::testConnection,
        onConnect = viewModel::authenticate,
        onDismissError = viewModel::clearErrorMessage,
        onDismissSuccess = viewModel::clearSuccessMessage
    )
}

@Composable
private fun SetupScreenContent(
    uiState: SetupUiState,
    onServerUrlChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTestConnection: () -> Unit,
    onConnect: () -> Unit,
    onDismissError: () -> Unit,
    onDismissSuccess: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Welcome to Kid Player",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Connect to your Jellyfin server to get started",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Server URL Input
            OutlinedTextField(
                value = uiState.serverUrl,
                onValueChange = onServerUrlChanged,
                label = { Text("Server URL") },
                placeholder = { Text("https://jellyfin.example.com") },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.connectionSuccess) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username Input
            OutlinedTextField(
                value = uiState.username,
                onValueChange = onUsernameChanged,
                label = { Text("Username") },
                placeholder = { Text("Enter your username") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.canAuthenticate) {
                            onConnect()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Connect Button
            Button(
                onClick = onConnect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.canAuthenticate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Connecting...",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Icon(
                        Icons.Default.Login,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Connect to Server",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Test Connection Button (Optional)
            OutlinedButton(
                onClick = onTestConnection,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.canConnect
            ) {
                Icon(
                    Icons.Default.NetworkCheck,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test Connection",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Error Message
            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissError) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Success Message
            uiState.successMessage?.let { success ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = success,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissSuccess) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Text
            Text(
                text = "Make sure your Jellyfin server is accessible from this device",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    name = "Setup Screen - Tablet Landscape",
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun SetupScreenPreview() {
    KidPlayerTheme {
        SetupScreenContent(
            uiState = SetupUiState(
                serverUrl = "https://jellyfin.example.com",
                username = "user",
                password = "password"
            ),
            onServerUrlChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onTestConnection = {},
            onConnect = {},
            onDismissError = {},
            onDismissSuccess = {}
        )
    }
}

@Preview(
    name = "Setup Screen - Loading",
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun SetupScreenLoadingPreview() {
    KidPlayerTheme {
        SetupScreenContent(
            uiState = SetupUiState(
                serverUrl = "https://jellyfin.example.com",
                username = "user",
                password = "password",
                isLoading = true
            ),
            onServerUrlChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onTestConnection = {},
            onConnect = {},
            onDismissError = {},
            onDismissSuccess = {}
        )
    }
}

@Preview(
    name = "Setup Screen - Error",
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun SetupScreenErrorPreview() {
    KidPlayerTheme {
        SetupScreenContent(
            uiState = SetupUiState(
                serverUrl = "https://jellyfin.example.com",
                username = "user",
                password = "password",
                errorMessage = "Unable to connect to server. Please check the URL and try again."
            ),
            onServerUrlChanged = {},
            onUsernameChanged = {},
            onPasswordChanged = {},
            onTestConnection = {},
            onConnect = {},
            onDismissError = {},
            onDismissSuccess = {}
        )
    }
}
