package se.supernovait.doobypro.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import se.supernovait.doobypro.presentation.account.AccountViewModel
import se.supernovait.doobypro.presentation.account.event.AccountScreenEvent
import se.supernovait.doobypro.presentation.account.screen.AccountScreen
import se.supernovait.doobypro.presentation.account.screen.AgreementScreen
import se.supernovait.doobypro.presentation.account.screen.CompanyProfileScreen
import se.supernovait.doobypro.presentation.account.screen.LicenseScreen
import se.supernovait.doobypro.presentation.account.screen.UserProfileScreen

fun NavGraphBuilder.accountGraph(
    navController: NavHostController
) {
    composable<Route.Account> {
        AccountScreen(
            onEvent = { event ->
                when (event) {
                    AccountScreenEvent.NavigateToUserProfile -> navController.navigateWithRules(Route.UserProfile)
                    AccountScreenEvent.NavigateToCompanyProfile -> navController.navigateWithRules(Route.CompanyProfile)
                    AccountScreenEvent.NavigateToLicense -> navController.navigateWithRules(Route.License)
                    AccountScreenEvent.NavigateToAgreements -> navController.navigateWithRules(Route.Agreement)
                }
            }
        )
    }

    composable<Route.UserProfile> {
        val viewModel = koinViewModel<AccountViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        UserProfileScreen(state = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.CompanyProfile> {
        val viewModel = koinViewModel<AccountViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        CompanyProfileScreen(state = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.License> {
        val viewModel = koinViewModel<AccountViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LicenseScreen(state = uiState)
    }

    composable<Route.Agreement> {
        val viewModel = koinViewModel<AccountViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AgreementScreen(state = uiState)
    }
}
