package se.supernovait.doobypro.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import se.supernovait.doobypro.presentation.welcome.WelcomeScreen
import se.supernovait.doobypro.presentation.welcome.WelcomeScreenEvent
import se.supernovait.doobypro.presentation.welcome.account_setup.AccountSetupWizardScreen
import se.supernovait.doobypro.presentation.welcome.account_setup.AccountSetupWizardViewModel

fun NavGraphBuilder.welcomeGraph(navController: NavController) {
    composable<Route.Welcome> {
        WelcomeScreen(onEvent = { event ->
            when(event) {
                WelcomeScreenEvent.NavigateToAccountSetupWizard -> {
                    navController.navigate(Route.AccountSetup)
                }
                WelcomeScreenEvent.NavigateToAppInfo -> {
                    navController.navigate(Route.AppInfo)
                }
                else -> { /* TODO: handle other events */ }
            }
        })
    }

    composable<Route.AccountSetup> {
        val viewModel = koinViewModel<AccountSetupWizardViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AccountSetupWizardScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}
