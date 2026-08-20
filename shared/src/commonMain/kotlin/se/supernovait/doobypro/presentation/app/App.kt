package se.supernovait.doobypro.presentation.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import se.supernovait.app.core.domain.extension.toErrorState
import se.supernovait.app.core.domain.initialization.AppInitializationState
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.app.core.domain.initialization.RecoveryOption
import se.supernovait.app.core.ui.component.error.SupernovaErrorEvent
import se.supernovait.app.core.ui.component.error.SupernovaErrorScreen
import se.supernovait.doobypro.domain.model.settings.common.ThemeMode
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme

/**
 * Displays either the main app or an error screen based on initialization state.
 *
 * - If initialization is successful: shows [AppRoot] with full navigation
 * - If initialization fails: shows [AppError] with error-specific recovery options:
 *   - Network error: Retry or Go Offline
 *   - Database error: Retry or Clear App Data
 *   - Preferences error: Retry or Reset Preferences
 *   - Unknown error: Retry
 */
@Composable
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val settings by settingsRepository.settings.collectAsState(initial = null)

    val darkTheme = when (settings?.common?.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
    }

    DoobyTheme(darkTheme = darkTheme) {
        val appInitializer = koinInject<AppInitializer>()
        val initState by appInitializer.appInitState.collectAsState()

        when (initState) {
            is AppInitializationState.Success -> {
                AppRoot()
            }

            is AppInitializationState.Error -> {
                AppError(appInitializer, initState)
            }

            is AppInitializationState.Initializing -> {
                // Initialization still in progress
                // On Android: splash screen is kept visible via setKeepOnScreenCondition
                // On iOS: LaunchScreen is kept visible (no Compose content rendered)
                // This composable is not rendered, so the system splash stays visible
            }
        }
    }
}

@Composable
private fun AppError(appInitializer: AppInitializer, initState: AppInitializationState) {
    val initializationErrorState = initState as AppInitializationState.Error
    val errorState = initializationErrorState.toErrorState()
    val coroutineScope = rememberCoroutineScope()

    SupernovaErrorScreen(
        state = errorState,
        isRefreshing = initState.isInitializing(),
        onEvent = { event ->
            when (event) {
                SupernovaErrorEvent.OnPrimaryAction -> {
                    coroutineScope.launch {
                        appInitializer.retryWithRecovery(RecoveryOption.RETRY)
                    }
                }
                SupernovaErrorEvent.OnSecondaryAction -> {
                    coroutineScope.launch {
                        appInitializer.retryWithRecovery(initializationErrorState.errorType.recoveryOption)
                    }
                }
            }
        }
    )
}
