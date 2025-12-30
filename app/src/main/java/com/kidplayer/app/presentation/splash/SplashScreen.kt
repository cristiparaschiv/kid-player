package com.kidplayer.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kidplayer.app.presentation.theme.KidPlayerTheme
import timber.log.Timber

/**
 * Splash Screen with Auto-Login
 * Displays while validating saved session
 * Always navigates to Home (with or without valid session for offline mode)
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit = {},
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle navigation based on validation result
    LaunchedEffect(uiState.validationResult) {
        when (uiState.validationResult) {
            ValidationResult.SUCCESS -> {
                Timber.d("Session valid, navigating to Home")
                onNavigateToHome()
                viewModel.resetValidationState()
            }
            ValidationResult.FAILED -> {
                Timber.d("Session invalid, navigating to Home in offline mode")
                onNavigateToHome()
                viewModel.resetValidationState()
            }
            ValidationResult.PENDING -> {
                // Still validating, stay on splash
            }
        }
    }

    SplashScreenContent(
        isValidating = uiState.isValidating,
        errorMessage = uiState.errorMessage
    )
}

@Composable
private fun SplashScreenContent(
    isValidating: Boolean,
    errorMessage: String?
) {
    // Breathing animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Fade in animation
    val alpha by animateFloatAsState(
        targetValue = if (isValidating) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(48.dp)
            ) {
                // Animated App Icon
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Kid Player Logo",
                    modifier = Modifier
                        .size(128.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        ),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // App Name
                Text(
                    text = "Kid Player",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tagline
                Text(
                    text = "Safe, Simple, Fun",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Loading indicator
                if (isValidating) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )

                        Text(
                            text = "Connecting...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Error message (if any)
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Splash Screen - Validating",
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun SplashScreenValidatingPreview() {
    KidPlayerTheme {
        SplashScreenContent(
            isValidating = true,
            errorMessage = null
        )
    }
}

@Preview(
    name = "Splash Screen - Error",
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun SplashScreenErrorPreview() {
    KidPlayerTheme {
        SplashScreenContent(
            isValidating = true,
            errorMessage = "Unable to connect to server. Please check your network connection."
        )
    }
}
