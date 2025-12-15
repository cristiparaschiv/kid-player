package com.kidplayer.app.presentation.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.local.ScreenTimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for tracking screen time status across the app
 * Used to prevent navigation to video playback when limit is reached
 */
@HiltViewModel
class ScreenTimeNavigationViewModel @Inject constructor(
    private val screenTimeManager: ScreenTimeManager
) : ViewModel() {

    private val _isTimeLimitReached = MutableStateFlow(false)
    val isTimeLimitReached: StateFlow<Boolean> = _isTimeLimitReached.asStateFlow()

    init {
        checkScreenTimeStatus()
    }

    /**
     * Check current screen time status
     */
    fun checkScreenTimeStatus() {
        viewModelScope.launch {
            val config = screenTimeManager.getScreenTimeConfig()
            _isTimeLimitReached.value = config.isLimitReached
            Timber.d("Screen time status checked: isLimitReached=${config.isLimitReached}")
        }
    }
}

/**
 * Composable function to guard navigation to video playback
 * Returns true if navigation should be allowed, false if blocked due to time limit
 *
 * Usage:
 * ```
 * val canNavigate = rememberScreenTimeNavigationGuard()
 * if (canNavigate) {
 *     navController.navigate(...)
 * }
 * ```
 */
@Composable
fun rememberScreenTimeNavigationGuard(
    viewModel: ScreenTimeNavigationViewModel = hiltViewModel()
): Boolean {
    val isTimeLimitReached by viewModel.isTimeLimitReached.collectAsState()

    // Refresh status on every composition
    LaunchedEffect(Unit) {
        viewModel.checkScreenTimeStatus()
    }

    return !isTimeLimitReached
}
