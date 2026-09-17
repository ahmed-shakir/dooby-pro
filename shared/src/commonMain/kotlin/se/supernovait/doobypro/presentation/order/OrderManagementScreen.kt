package se.supernovait.doobypro.presentation.order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.ic_archive
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.navigation_item_cancelled_orders_label
import doobypro.shared.generated.resources.screen_Order_action_add_order
import doobypro.shared.generated.resources.screen_Order_dialog_delete_message
import doobypro.shared.generated.resources.screen_Order_dialog_delete_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.fab.LocalFabState
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.topbar.LocalTopBarState
import se.supernovait.app.core.ui.component.topbar.TopBarAction
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.presentation.order.component.CustomerFormSheet
import se.supernovait.doobypro.presentation.order.component.CustomerSearchSheet
import se.supernovait.doobypro.presentation.order.component.OrderFormSheet
import se.supernovait.doobypro.presentation.order.component.OrderListContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    uiState: OrderState,
    onEvent: (OrderEvent) -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val fabState = LocalFabState.current
    val dialogState = LocalDialogState.current
    val topBarState = LocalTopBarState.current

    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Order_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Order_dialog_delete_message)

    val archiveLabel = stringResource(Res.string.navigation_item_cancelled_orders_label)
    val tabLabels = OrderTab.entries.associateWith { stringResource(it.label) }

    var showCustomerSheet by remember { mutableStateOf(false) }

    // Track the latest uiState in a State object so that the bottom sheet lambdas
    // can reactively update without needing to call show() again.
    val currentOrderUiState = remember { mutableStateOf(uiState) }
    currentOrderUiState.value = uiState

    DisposableEffect(Unit) {
        fabState.set(
            icon = Res.drawable.ic_add,
            contentDescription = Res.string.screen_Order_action_add_order,
            onClick = {
                onEvent(OrderEvent.CreateNewOrder)
                showCustomerSheet = true
            }
        )
        
        topBarState.actions(
            listOf(
                TopBarAction(
                    icon = Res.drawable.ic_archive,
                    label = archiveLabel,
                    contentDescription = archiveLabel,
                    onClick = { onEvent(OrderEvent.ViewCancelledOrders) }
                )
            )
        )

        onDispose {
            topBarState.actions(emptyList())
        }
    }

    LaunchedEffect(showCustomerSheet) {
        if (showCustomerSheet) {
            bottomSheetState.show {
                val state = currentOrderUiState.value
                
                if (state.isAddingCustomer) {
                    CustomerFormSheet(
                        onSave = { onEvent(OrderEvent.SaveNewCustomer(it)) },
                        onCancel = { onEvent(OrderEvent.CreateNewOrder) }
                    )
                } else {
                    CustomerSearchSheet(
                        customers = state.customers,
                        searchQuery = state.customerSearchQuery,
                        onSearch = { onEvent(OrderEvent.SearchCustomers(it)) },
                        onSelect = {
                            onEvent(OrderEvent.SelectCustomer(it))
                            showCustomerSheet = false
                            bottomSheetState.hide()
                        },
                        onAddClick = { onEvent(OrderEvent.StartAddingCustomer) },
                        onCancel = {
                            showCustomerSheet = false
                            bottomSheetState.hide()
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(uiState.editingOrder) {
        if (uiState.editingOrder != null && uiState.editingOrder.id == null) {
            bottomSheetState.show {
                val state = currentOrderUiState.value
                val editingOrder = state.editingOrder ?: return@show

                OrderFormSheet(
                    order = editingOrder,
                    customers = state.customers,
                    services = state.services,
                    storageLocations = state.storageLocations,
                    isManualStorageMode = state.isManualStorageMode,
                    onSave = { 
                        onEvent(OrderEvent.SaveOrder(it))
                        bottomSheetState.hide()
                    },
                    onDelete = null
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = OrderTab.entries.indexOf(uiState.activeTab),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            divider = {}
        ) {
            OrderTab.entries.forEach { tab ->
                val lateCount = uiState.lateOrderCountPerTab[tab] ?: 0
                
                Tab(
                    selected = uiState.activeTab == tab,
                    onClick = { onEvent(OrderEvent.SelectTab(tab)) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (lateCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = lateCount.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            SupernovaLabel(
                                text = tabLabels[tab] ?: tab.name,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                )
            }
        }

        OrderListContent(
            orders = uiState.orders,
            isLoading = uiState.isLoading,
            searchQuery = uiState.searchQuery,
            onSearchChange = { onEvent(OrderEvent.SearchOrders(it)) },
            onOrderClick = { onEvent(OrderEvent.ViewOrderDetails(it)) },
            onEditOrder = { order ->
                onEvent(OrderEvent.EditOrder(order))
                bottomSheetState.show {
                    val state = currentOrderUiState.value
                    OrderFormSheet(
                        order = order,
                        customers = state.customers,
                        services = state.services,
                        storageLocations = state.storageLocations,
                        isManualStorageMode = state.isManualStorageMode,
                        onSave = { 
                            onEvent(OrderEvent.SaveOrder(it))
                            bottomSheetState.hide()
                        },
                        onDelete = {
                            dialogState.showConfirmation(
                                title = deleteTitle,
                                message = deleteMessage,
                                confirmLabel = Res.string.label_delete,
                                dismissLabel = Res.string.label_cancel,
                                primaryActionColor = deleteColor,
                                onConfirm = {
                                    onEvent(OrderEvent.DeleteOrder(order))
                                    bottomSheetState.hide()
                                    dialogState.hide()
                                },
                                onDismiss = { dialogState.hide() }
                            )
                        }
                    )
                }
            }
        )
    }
}
