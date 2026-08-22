package se.supernovait.doobypro.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import se.supernovait.doobypro.presentation.account.AccountScreen
import se.supernovait.doobypro.presentation.account.AccountViewModel

/**
 * Extension for NavGraphBuilder to add account-related navigation.
 */
fun NavGraphBuilder.accountGraph(
    navController: NavHostController
) {
    composable<Route.Account> {
        val viewModel = koinViewModel<AccountViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AccountScreen(state = uiState, onEvent = viewModel::onEvent)
    }
}
