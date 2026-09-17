package se.supernovait.doobypro.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import se.supernovait.doobypro.presentation.settings.SettingsViewModel
import se.supernovait.doobypro.presentation.settings.event.SettingsNavigationEvent
import se.supernovait.doobypro.presentation.settings.screen.CommonSettingsScreen
import se.supernovait.doobypro.presentation.settings.screen.NotificationSettingsScreen
import se.supernovait.doobypro.presentation.settings.screen.OrderSettingsScreen
import se.supernovait.doobypro.presentation.settings.screen.PrinterSettingsScreen
import se.supernovait.doobypro.presentation.settings.screen.ReceiptSettingsScreen
import se.supernovait.doobypro.presentation.settings.screen.SettingsMenuScreen
import se.supernovait.doobypro.presentation.settings.screen.StorageSettingsScreen

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController
) {
    composable<Route.Settings> {
        SettingsMenuScreen(
            onNavigation = { event ->
                when (event) {
                    SettingsNavigationEvent.NavigateToCommon -> navController.navigate(Route.SettingsCommon)
                    SettingsNavigationEvent.NavigateToOrder -> navController.navigate(Route.SettingsOrder)
                    SettingsNavigationEvent.NavigateToStorage -> navController.navigate(Route.SettingsStorage)
                    SettingsNavigationEvent.NavigateToReceipt -> navController.navigate(Route.SettingsReceipt)
                    SettingsNavigationEvent.NavigateToPrinter -> navController.navigate(Route.SettingsPrinter)
                    SettingsNavigationEvent.NavigateToNotifications -> navController.navigate(Route.SettingsNotification)
                }
            }
        )
    }

    composable<Route.SettingsCommon> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        CommonSettingsScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.SettingsOrder> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        OrderSettingsScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.SettingsStorage> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StorageSettingsScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.SettingsReceipt> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ReceiptSettingsScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.SettingsPrinter> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        PrinterSettingsScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.SettingsNotification> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        NotificationSettingsScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }
}
