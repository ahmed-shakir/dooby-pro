package se.supernovait.doobypro.presentation.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.fab.LocalFabState
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.presentation.order.component.OrderFormSheet
import se.supernovait.doobypro.presentation.order.component.OrderItem
import kotlin.time.Clock

@Composable
fun OrderManagementScreen(
    state: OrderState,
    onEvent: (OrderEvent) -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val fabState = LocalFabState.current
    val dialogState = LocalDialogState.current

    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Order_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Order_dialog_delete_message)

    DisposableEffect(Unit) {
        fabState.set(
            icon = Res.drawable.ic_add,
            contentDescription = Res.string.screen_Order_action_add_order,
            onClick = {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val defaultCustomer = state.customers.firstOrNull()
                val defaultService = state.services.firstOrNull()
                val defaultStorage = state.storageLocations.firstOrNull { it.isDefault } 
                    ?: state.storageLocations.firstOrNull()
                
                if (defaultCustomer != null && defaultService != null && defaultStorage != null) {
                    val newOrderTemplate = Order(
                        customer = defaultCustomer,
                        service = defaultService,
                        storageLocation = defaultStorage,
                        status = OrderStatus.NEW,
                        orderDatetime = now,
                        deliveryDatetime = now,
                        deliveryOption = DeliveryOption.STANDARD,
                        deliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
                        isPaymentDone = false,
                        notes = null
                    )
                    onEvent(OrderEvent.EditOrder(newOrderTemplate))
                    bottomSheetState.show {
                        OrderFormSheet(
                            order = newOrderTemplate,
                            customers = state.customers,
                            services = state.services,
                            storageLocations = state.storageLocations,
                            onSave = { 
                                onEvent(OrderEvent.SaveOrder(it))
                                bottomSheetState.hide()
                            },
                            onDelete = null
                        )
                    }
                }
            }
        )
        onDispose {}
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.orders.isEmpty() && !state.isLoading) {
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
                items(state.orders) { order ->
                    OrderItem(
                        order = order,
                        onEdit = {
                            onEvent(OrderEvent.EditOrder(order))
                            bottomSheetState.show {
                                OrderFormSheet(
                                    order = order,
                                    customers = state.customers,
                                    services = state.services,
                                    storageLocations = state.storageLocations,
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
