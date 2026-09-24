package se.supernovait.doobypro.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.app.AppEventHandler
import se.supernovait.doobypro.presentation.info.AppInfoScreen
import se.supernovait.doobypro.presentation.notification.NotificationScreen
import se.supernovait.doobypro.presentation.notification.NotificationViewModel
import se.supernovait.doobypro.presentation.welcome.SignInBottomSheet
import se.supernovait.doobypro.presentation.welcome.WelcomeScreen
import se.supernovait.doobypro.presentation.welcome.WelcomeScreenEvent
import se.supernovait.doobypro.presentation.welcome.WelcomeViewModel
import se.supernovait.doobypro.presentation.welcome.account_setup.AccountSetupWizardScreen
import se.supernovait.doobypro.presentation.welcome.account_setup.AccountSetupWizardViewModel

/**
 * Extension for NavGraphBuilder to add introductory navigation.
 */
fun NavGraphBuilder.introGraph(
    navController: NavHostController,
) {
    composable<Route.Welcome> {
        val viewModel = koinViewModel<WelcomeViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AppEventHandler(events = viewModel.events)

        SignInBottomSheet(
            showSignInForm = uiState.showSignInForm,
            isSigningIn = uiState.isSigningIn,
            signInError = uiState.signInError,
            isUsernameEmpty = uiState.isUsernameEmpty,
            onSignIn = { username -> viewModel.onEvent(WelcomeScreenEvent.SignIn(username)) },
            onDismiss = { viewModel.onEvent(WelcomeScreenEvent.HideSignInForm) }
        )

        WelcomeScreen(
            onEvent = { event ->
                when (event) {
                    WelcomeScreenEvent.NavigateToAccountSetupWizard -> {
                        navController.navigateWithRules(Route.AccountSetup)
                    }
                    WelcomeScreenEvent.NavigateToAppInfo -> {
                        navController.navigateWithRules(Route.AppInfo)
                    }
                    else -> viewModel.onEvent(event)
                }
            }
        )
    }

    composable<Route.AccountSetup> {
        val viewModel = koinViewModel<AccountSetupWizardViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AppEventHandler(events = viewModel.events)

        AccountSetupWizardScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.AppInfo> {
        AppInfoScreen(onBack = { navController.popBackStack() })
    }

    composable<Route.Support> {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.mediumLarge)
        ) {
            SupernovaTitle(text = "Support center")
        }
    }

    composable<Route.Notifications> {
        val viewModel = koinViewModel<NotificationViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        NotificationScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }
}
