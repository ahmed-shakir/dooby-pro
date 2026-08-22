package se.supernovait.doobypro.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import se.supernovait.doobypro.presentation.app.AppEventHandler
import se.supernovait.doobypro.presentation.info.AppInfoScreen
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

        AccountSetupWizardScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }

    composable<Route.AppInfo> {
        AppInfoScreen(
            onBack = { navController.popBackStack() }
        )
    }
}
