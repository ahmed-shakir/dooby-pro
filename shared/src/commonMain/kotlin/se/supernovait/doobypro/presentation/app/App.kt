package se.supernovait.doobypro.presentation.app

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_name
import doobypro.shared.generated.resources.ic_app_icon
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.AuthenticationState
import se.supernovait.app.core.domain.connectivity.ConnectivityManager
import se.supernovait.app.core.domain.extension.toErrorState
import se.supernovait.app.core.domain.initialization.AppInitializationState
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.app.core.domain.initialization.RecoveryOption
import se.supernovait.app.core.ui.component.error.SupernovaErrorEvent
import se.supernovait.app.core.ui.component.error.SupernovaErrorScreen
import se.supernovait.app.core.ui.component.scaffold.SupernovaScaffold
import se.supernovait.app.core.ui.component.topbar.LocalTopBarState
import se.supernovait.doobypro.presentation.app.theme.DoobyTheme
import se.supernovait.doobypro.presentation.common.preview.ScreenPreviewContainer
import se.supernovait.doobypro.presentation.navigation.Route
import se.supernovait.doobypro.presentation.navigation.welcomeGraph

/**
 * Displays either the main app or an error screen based on initialization state.
 *
 * - If initialization is successful: shows [AppContent] with full navigation
 * - If initialization fails: shows [AppError] with error-specific recovery options:
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

        when (initState) {
            is AppInitializationState.Success -> {
                AppContent()
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
private fun AppContent() {
    SupernovaScaffold(
        showDrawer = true,
        connectivityManager = koinInject<ConnectivityManager>()
    ) { innerPadding ->
        val authManager = koinInject<AuthenticationManager>()
        val authState by authManager.authState.collectAsStateWithLifecycle()
        val topBarState = LocalTopBarState.current

        val navController: NavHostController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val startScreen = Route.startScreen(authManager.isAuthenticated())
        val currentScreen = Route.parse(backStackEntry?.destination?.route, startScreen)

        LaunchedEffect(authState) {
            if (authState is AuthenticationState.NotAuthenticated && currentScreen != Route.Welcome) {
                navController.navigate(Route.Welcome) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        LaunchedEffect(topBarState) {
            topBarState.icon(Res.drawable.ic_app_icon)
            topBarState.title(Res.string.app_name)
            topBarState.actions(canNavigateBack = false)

            if (currentScreen.showTopBar) topBarState.show() else topBarState.hide()
        }

        NavHost(
            navController = navController,
            startDestination = startScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            welcomeGraph(navController)
            // TODO: add nav graphs
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

@PreviewLightDark
@Composable
private fun Preview() {
    ScreenPreviewContainer {
        AppContent()
    }
}
