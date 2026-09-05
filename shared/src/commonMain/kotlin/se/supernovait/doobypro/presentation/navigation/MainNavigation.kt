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
import se.supernovait.doobypro.presentation.storage.StorageManagementScreen
import se.supernovait.doobypro.presentation.storage.StorageViewModel

fun NavGraphBuilder.mainGraph(
    navController: NavHostController
) {
    composable<Route.Dashboard> {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.mediumLarge)
        ) {
            SupernovaTitle(text = "My Dashboard")
        }
    }

    composable<Route.Orders> {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.mediumLarge)
        ) {
            SupernovaTitle(text = "Orders")
        }
    }

    composable<Route.Services> {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.mediumLarge)
        ) {
            SupernovaTitle(text = "Services")
        }
    }

    composable<Route.StorageManagement> {
        val viewModel = koinViewModel<StorageViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        StorageManagementScreen(
            state = uiState,
            onEvent = viewModel::onEvent
        )
    }
}
