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

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController
) {
    // TODO: Update Notification settings to actual types that will be handled by Dooby Pro

    composable<Route.Settings> {
        SettingsMenuScreen(
            onNavigation = { event ->
                when (event) {
                    SettingsNavigationEvent.NavigateToCommon -> navController.navigate(Route.SettingsCommon)
                    SettingsNavigationEvent.NavigateToOrder -> navController.navigate(Route.SettingsOrder)
                    SettingsNavigationEvent.NavigateToReceipt -> navController.navigate(Route.SettingsReceipt)
                    SettingsNavigationEvent.NavigateToPrinter -> navController.navigate(Route.SettingsPrinter)
                    SettingsNavigationEvent.NavigateToNotifications -> navController.navigate(Route.SettingsNotifications)
                }
            }
        )
    }

    composable<Route.SettingsCommon> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        CommonSettingsScreen(state, viewModel::onEvent)
    }

    composable<Route.SettingsOrder> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        OrderSettingsScreen(state, viewModel::onEvent)
    }

    composable<Route.SettingsReceipt> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        ReceiptSettingsScreen(state, viewModel::onEvent)
    }

    composable<Route.SettingsPrinter> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        PrinterSettingsScreen(state, viewModel::onEvent)
    }

    composable<Route.SettingsNotifications> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        NotificationSettingsScreen(state, viewModel::onEvent)
    }
}
