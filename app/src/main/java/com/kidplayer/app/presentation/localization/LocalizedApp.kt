package com.kidplayer.app.presentation.localization

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.kidplayer.app.data.local.AppLanguage
import com.kidplayer.app.data.local.LanguageManager
import java.util.Locale

/**
 * Wraps the app content with the selected locale configuration
 * This enables runtime language switching without restarting the app
 */
@Composable
fun LocalizedApp(
    languageManager: LanguageManager,
    content: @Composable () -> Unit
) {
    val currentLanguage by languageManager.currentLanguage.collectAsState()

    LocalizedContent(
        language = currentLanguage,
        content = content
    )
}

/**
 * Provides localized configuration to child composables
 *
 * NOTE: We only override LocalConfiguration, NOT LocalContext.
 * Overriding LocalContext with createConfigurationContext() breaks Hilt's
 * ViewModel creation because it returns an application context instead of
 * an Activity context.
 */
@Composable
private fun LocalizedContent(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current

    val locale = remember(language) {
        Locale(language.code)
    }

    val localizedConfiguration = remember(locale, configuration) {
        Configuration(configuration).apply {
            setLocale(locale)
            // Also update the locale list for proper resource resolution
            setLocales(android.os.LocaleList(locale))
        }
    }

    // Only update the configuration, NOT the context
    // This preserves the Activity context needed by Hilt
    CompositionLocalProvider(
        LocalConfiguration provides localizedConfiguration
    ) {
        content()
    }
}

/**
 * Helper function to get localized string based on current language
 * Used for game content that isn't in string resources
 */
@Composable
fun localizedString(
    en: String,
    ro: String
): String {
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    return if (locale.language == "ro") ro else en
}

/**
 * Helper function to get localized string from a map
 */
@Composable
fun localizedString(
    translations: Map<String, String>,
    default: String = ""
): String {
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    return translations[locale.language] ?: translations["en"] ?: default
}
