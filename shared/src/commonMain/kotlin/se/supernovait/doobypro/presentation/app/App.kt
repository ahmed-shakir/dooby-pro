package se.supernovait.doobypro.presentation.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_icon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import se.supernovait.app.core.domain.extension.toErrorState
import se.supernovait.app.core.domain.initialization.AppInitializationState
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.app.core.domain.initialization.RecoveryOption
import se.supernovait.app.core.ui.component.error.SupernovaErrorEvent
import se.supernovait.app.core.ui.component.error.SupernovaErrorScreen
import se.supernovait.app.core.ui.component.preview.ScreenPreviewContainer
import se.supernovait.app.core.ui.component.scaffold.SupernovaScaffold
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme

/**
 * Displays either the main app or an error screen based on initialization state.
 *
 * - If initialization is successful: shows [AppContent] with full navigation
 * - If initialization fails: shows [SupernovaErrorScreen] with error-specific recovery options:
 *   - Network error: Retry or Go Offline
 *   - Database error: Retry or Clear App Data
 *   - Preferences error: Retry or Reset Preferences
 *   - Unknown error: Retry
 */
@Composable
fun App() {
    DoobyTheme {
        val appInitializer: AppInitializer = koinInject()
        val initState by appInitializer.appInitState.collectAsState()
        val coroutineScope = rememberCoroutineScope()

        when (initState) {
            is AppInitializationState.Success -> {
                AppContent()
            }

            is AppInitializationState.Error -> {
                val initializationErrorState = initState as AppInitializationState.Error
                val errorState = initializationErrorState.toErrorState()

                SupernovaErrorScreen(
                    state = errorState,
                    isRefreshing = initState.isInitializing(),
                    onEvent = { event ->
                        when(event) {
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
private fun AppContent() {
    SupernovaScaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("Welcome to Dooby Pro")
            Image(
                painter = painterResource(Res.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier.padding(MaterialTheme.spacing.large)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    ScreenPreviewContainer {
        App()
    }
}
