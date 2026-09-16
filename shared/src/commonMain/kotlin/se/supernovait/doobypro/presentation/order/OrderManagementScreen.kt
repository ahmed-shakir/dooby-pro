package se.supernovait.doobypro.presentation.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.screen_Order_action_add_order
import doobypro.shared.generated.resources.screen_Order_dialog_delete_message
import doobypro.shared.generated.resources.screen_Order_dialog_delete_title
import doobypro.shared.generated.resources.screen_Order_empty_state
import doobypro.shared.generated.resources.screen_Order_search_hint
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.fab.LocalFabState
import se.supernovait.app.core.ui.component.input.SupernovaSearchField
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.presentation.order.component.CustomerFormSheet
import se.supernovait.doobypro.presentation.order.component.CustomerSearchSheet
import se.supernovait.doobypro.presentation.order.component.OrderFormSheet
import se.supernovait.doobypro.presentation.order.component.OrderItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    uiState: OrderState,
    onEvent: (OrderEvent) -> Unit,
    onOrderClick: (String) -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val fabState = LocalFabState.current
    val dialogState = LocalDialogState.current

    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Order_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Order_dialog_delete_message)

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
        onDispose {}
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
                    settings = state.settings,
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
        SupernovaSearchField(
            value = uiState.searchQuery,
            onValueChange = { onEvent(OrderEvent.SearchOrders(it)) },
            onSearch = { onEvent(OrderEvent.SearchOrders(it)) },
            placeholder = stringResource(Res.string.screen_Order_search_hint),
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium)
        )

        PrimaryTabRow(
            selectedTabIndex = OrderTab.entries.indexOf(uiState.activeTab),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            OrderTab.entries.forEach { tab ->
                val count = uiState.orderCountPerTab[tab] ?: 0
                Tab(
                    selected = uiState.activeTab == tab,
                    onClick = { onEvent(OrderEvent.SelectTab(tab)) },
                    text = {
                        BadgedBox(
                            badge = {
                                if (count > 0) {
                                    Badge { SupernovaLabel(text = count.toString()) }
                                }
                            }
                        ) {
                            SupernovaLabel(
                                text = tabLabels[tab] ?: tab.name,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.orders.isEmpty() && !uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.large), contentAlignment = Alignment.Center) {
                    SupernovaLabel(
                        text = Res.string.screen_Order_empty_state,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.medium)
                ) {
                    items(uiState.orders) { order ->
                        OrderItem(
                            order = order,
                            onClick = { onOrderClick(order.id!!) },
                            onEdit = {
                                onEvent(OrderEvent.EditOrder(order))
                                bottomSheetState.show {
                                    val state = currentOrderUiState.value
                                    OrderFormSheet(
                                        order = order,
                                        customers = state.customers,
                                        services = state.services,
                                        storageLocations = state.storageLocations,
                                        settings = state.settings,
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
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    item { Spacer(Modifier.height(MaterialTheme.spacing.x5Large)) }
                }
            }
        }
    }
}
