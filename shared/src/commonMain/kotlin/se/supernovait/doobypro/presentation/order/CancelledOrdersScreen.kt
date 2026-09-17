package se.supernovait.doobypro.presentation.order

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import se.supernovait.doobypro.presentation.order.component.OrderListContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelledOrdersScreen(
    uiState: OrderState,
    onEvent: (OrderEvent) -> Unit
) {
    // Manage archive mode lifecycle
    DisposableEffect(Unit) {
        onEvent(OrderEvent.ToggleArchive(isArchive = true))
        onDispose {
            onEvent(OrderEvent.ToggleArchive(isArchive = false))
        }
    }

    OrderListContent(
        orders = uiState.orders,
        isLoading = uiState.isLoading,
        searchQuery = uiState.searchQuery,
        onSearchChange = { onEvent(OrderEvent.SearchOrders(it)) },
        onOrderClick = { onEvent(OrderEvent.ViewOrderDetails(it)) },
        onEditOrder = null, // Cancelled orders are read-only
        onReissueOrder = { onEvent(OrderEvent.ReissueOrder(it)) }
    )
}
