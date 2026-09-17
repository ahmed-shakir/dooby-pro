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
import se.supernovait.doobypro.presentation.order.CancelledOrdersScreen
import se.supernovait.doobypro.presentation.order.OrderEvent
import se.supernovait.doobypro.presentation.order.OrderManagementScreen
import se.supernovait.doobypro.presentation.order.OrderViewModel
import se.supernovait.doobypro.presentation.order.details.OrderDetailsEvent
import se.supernovait.doobypro.presentation.order.details.OrderDetailsScreen
import se.supernovait.doobypro.presentation.order.details.OrderDetailsViewModel
import se.supernovait.doobypro.presentation.service.ServiceManagementScreen
import se.supernovait.doobypro.presentation.service.ServiceViewModel
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
        val viewModel = koinViewModel<OrderViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        OrderManagementScreen(
            uiState = uiState,
            onEvent = { event ->
                when (event) {
                    OrderEvent.ViewCancelledOrders -> navController.navigate(Route.CancelledOrders)
                    is OrderEvent.ViewOrderDetails -> navController.navigate(Route.OrderDetails(event.id))
                    else -> viewModel.onEvent(event)
                }
            }
        )
    }

    composable<Route.CancelledOrders> {
        val viewModel = koinViewModel<OrderViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        CancelledOrdersScreen(
            uiState = uiState,
            onEvent = { event ->
                when (event) {
                    is OrderEvent.ViewOrderDetails -> navController.navigate(Route.OrderDetails(event.id))
                    is OrderEvent.ReissueOrder -> {
                        viewModel.onEvent(event)
                        navController.popBackStack() // Go back to Hub to show the form
                    }
                    else -> viewModel.onEvent(event)
                }
            }
        )
    }

    composable<Route.OrderDetails> {
        val viewModel = koinViewModel<OrderDetailsViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        OrderDetailsScreen(
            uiState = uiState, 
            onEvent = { event ->
                when (event) {
                    OrderDetailsEvent.ReissueOrder -> {
                        val order = uiState.order ?: return@OrderDetailsScreen
                        // We can't directly trigger the hub VM here easily without a shared VM or Result pattern.
                        // But we can use the savedStateHandle of the PREVIOUS entry.
                        navController.previousBackStackEntry?.savedStateHandle?.set("reissue_order", order)
                        navController.popBackStack()
                    }
                    else -> viewModel.onEvent(event)
                }
            }
        )
    }

    composable<Route.Services> {
        val viewModel = koinViewModel<ServiceViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        ServiceManagementScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }

    composable<Route.StorageManagement> {
        val viewModel = koinViewModel<StorageViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        StorageManagementScreen(uiState = uiState, onEvent = viewModel::onEvent)
    }
}
