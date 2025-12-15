package com.kidplayer.app.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidplayer.app.domain.model.AccessSchedule
import com.kidplayer.app.domain.model.ParentalControls
import com.kidplayer.app.domain.model.ScreenTimeConfig
import com.kidplayer.app.presentation.settings.components.PinEntryDialog
import com.kidplayer.app.presentation.theme.KidPlayerTheme
import java.time.LocalTime

/**
 * Complete Settings Screen with PIN protection and parental controls
 * This replaces the placeholder SettingsScreen.kt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenNew(
    onNavigateBack: () -> Unit = {},
    onNavigateToSetup: () -> Unit = {},
    viewModel: ParentalControlsViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    // Handle logout navigation
    LaunchedEffect(settingsUiState.shouldNavigateToSetup) {
        if (settingsUiState.shouldNavigateToSetup) {
            onNavigateToSetup()
            settingsViewModel.resetNavigationFlag()
        }
    }

    // Show PIN dialog when parentalControls is loaded and not authenticated
    LaunchedEffect(uiState.parentalControls, uiState.isAuthenticated) {
        val controls = uiState.parentalControls ?: return@LaunchedEffect

        if (!uiState.isAuthenticated && !uiState.pinDialogState.isVisible) {
            if (controls.isPinSet) {
                viewModel.showPinDialog(PinDialogMode.VERIFY)
            } else {
                viewModel.showPinDialog(PinDialogMode.SETUP)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Parent Settings",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    if (uiState.isAuthenticated) {
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Settings"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        // Debug logging
        timber.log.Timber.d("SettingsScreen: isAuthenticated=${uiState.isAuthenticated}, parentalControls=${uiState.parentalControls != null}, isPinSet=${uiState.parentalControls?.isPinSet}")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isAuthenticated && uiState.parentalControls != null) {
                // Show settings content
                SettingsContent(
                    parentalControls = uiState.parentalControls!!,
                    onScreenTimeLimitChanged = viewModel::updateScreenTimeLimitEnabled,
                    onDailyTimeLimitChanged = viewModel::updateDailyTimeLimit,
                    onAccessScheduleChanged = viewModel::updateAccessSchedule,
                    onChangePinClick = {
                        viewModel.showPinDialog(PinDialogMode.CHANGE)
                    },
                    serverUrl = settingsUiState.serverUrl,
                    username = settingsUiState.username,
                    onLogoutClick = settingsViewModel::logout
                )
            } else {
                // Show locked state
                LockedSettingsPlaceholder()
            }

            // PIN entry dialog
            PinEntryDialog(
                isVisible = uiState.pinDialogState.isVisible,
                title = when (uiState.pinDialogState.mode) {
                    PinDialogMode.SETUP -> "Set Parent PIN"
                    PinDialogMode.VERIFY -> "Enter Parent PIN"
                    PinDialogMode.CHANGE -> "Change PIN"
                },
                subtitle = when (uiState.pinDialogState.mode) {
                    PinDialogMode.SETUP -> "Create a 4-digit PIN to protect settings"
                    PinDialogMode.VERIFY -> "Enter your 4-digit PIN to access settings"
                    PinDialogMode.CHANGE -> "Enter a new 4-digit PIN"
                },
                isSetupMode = uiState.pinDialogState.mode != PinDialogMode.VERIFY,
                onPinEntered = viewModel::onPinEntered,
                onDismiss = {
                    viewModel.dismissPinDialog()
                    if (!uiState.isAuthenticated) {
                        onNavigateBack()
                    }
                },
                isError = uiState.pinDialogState.isError,
                errorMessage = uiState.pinDialogState.errorMessage
            )
        }
    }
}

@Composable
private fun SettingsContent(
    parentalControls: ParentalControls,
    onScreenTimeLimitChanged: (Boolean) -> Unit,
    onDailyTimeLimitChanged: (Int) -> Unit,
    onAccessScheduleChanged: (AccessSchedule?) -> Unit,
    onChangePinClick: () -> Unit,
    serverUrl: String = "",
    username: String = "",
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Screen Time Section
        ScreenTimeSection(
            config = parentalControls.screenTimeConfig,
            onEnabledChanged = onScreenTimeLimitChanged,
            onLimitChanged = onDailyTimeLimitChanged
        )

        Divider()

        // Access Schedule Section
        AccessScheduleSection(
            schedule = parentalControls.accessSchedule,
            onScheduleChanged = onAccessScheduleChanged
        )

        Divider()

        // PIN Management Section
        PinManagementSection(
            onChangePinClick = onChangePinClick
        )

        Divider()

        // Server Settings Section
        ServerSettingsSection(
            serverUrl = serverUrl,
            username = username,
            onLogoutClick = onLogoutClick
        )

        Divider()

        // About Section
        AboutSection()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTimeSection(
    config: ScreenTimeConfig,
    onEnabledChanged: (Boolean) -> Unit,
    onLimitChanged: (Int) -> Unit
) {
    var showLimitPicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Screen Time Limits",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Enable/Disable Switch
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.clickable {
                timber.log.Timber.d("Card clicked, toggling from ${config.isEnabled} to ${!config.isEnabled}")
                onEnabledChanged(!config.isEnabled)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Time Limit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Limit daily screen time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = config.isEnabled,
                    onCheckedChange = { enabled ->
                        timber.log.Timber.d("Switch onCheckedChange called: $enabled")
                        onEnabledChanged(enabled)
                    }
                )
            }
        }

        // Daily limit setting
        if (config.isEnabled) {
            Card(
                onClick = { showLimitPicker = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Time Limit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tap to change",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${config.dailyLimitMinutes} min",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Usage info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Used Today",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${config.usedTodayMinutes} min",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${config.remainingMinutes} min",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (config.remainingMinutes < 10) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            }
                        )
                    }
                }
            }
        }
    }

    // Time limit picker dialog
    if (showLimitPicker) {
        TimeLimitPickerDialog(
            currentLimit = config.dailyLimitMinutes,
            onLimitSelected = { newLimit ->
                onLimitChanged(newLimit)
                showLimitPicker = false
            },
            onDismiss = { showLimitPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccessScheduleSection(
    schedule: AccessSchedule?,
    onScheduleChanged: (AccessSchedule?) -> Unit
) {
    var isEnabled by remember(schedule) { mutableStateOf(schedule?.isEnabled ?: false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Access Schedule",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.clickable {
                timber.log.Timber.d("Access Schedule Card clicked, toggling from $isEnabled to ${!isEnabled}")
                val newEnabled = !isEnabled
                isEnabled = newEnabled
                if (newEnabled && schedule == null) {
                    onScheduleChanged(
                        AccessSchedule(
                            isEnabled = true,
                            startTime = LocalTime.of(9, 0),
                            endTime = LocalTime.of(19, 0)
                        )
                    )
                } else if (schedule != null) {
                    onScheduleChanged(schedule.copy(isEnabled = newEnabled))
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Schedule",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Set allowed hours",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { enabled ->
                        timber.log.Timber.d("Access Schedule Switch changed: $enabled")
                        isEnabled = enabled
                        if (enabled && schedule == null) {
                            // Create default schedule 9am-7pm
                            onScheduleChanged(
                                AccessSchedule(
                                    isEnabled = true,
                                    startTime = LocalTime.of(9, 0),
                                    endTime = LocalTime.of(19, 0)
                                )
                            )
                        } else if (schedule != null) {
                            onScheduleChanged(schedule.copy(isEnabled = enabled))
                        }
                    }
                )
            }
        }

        if (isEnabled && schedule != null) {
            Card(
                onClick = { showTimePicker = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Allowed Hours",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tap to change",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${schedule.startTime} - ${schedule.endTime}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinManagementSection(
    onChangePinClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "PIN Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            onClick = onChangePinClick,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Change PIN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Update your parent PIN",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun AboutSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "About",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Kid Player",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Version 1.0.0 (Phase 5)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "A kid-friendly video player with parental controls",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LockedSettingsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Settings Locked",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter PIN to access settings",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TimeLimitPickerDialog(
    currentLimit: Int,
    onLimitSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val limits = listOf(15, 30, 45, 60, 90, 120, 180, 240)
    var selectedLimit by remember { mutableStateOf(currentLimit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Time Limit") },
        text = {
            Column {
                limits.forEach { limit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLimit == limit,
                            onClick = { selectedLimit = limit }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$limit minutes (${limit / 60}h ${limit % 60}m)",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onLimitSelected(selectedLimit) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServerSettingsSection(
    serverUrl: String,
    username: String,
    onLogoutClick: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Server Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Server URL
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Server URL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = serverUrl,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Username
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Username",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Logout Button
        Card(
            onClick = { showLogoutDialog = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Sign out and return to setup screen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Logout Confirmation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout? You will need to enter server credentials again on next launch.",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(
    name = "Settings Screen - Locked",
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun SettingsScreenLockedPreview() {
    KidPlayerTheme {
        LockedSettingsPlaceholder()
    }
}
