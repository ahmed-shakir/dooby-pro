package se.supernovait.doobypro.presentation.order.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_payment_status_paid
import doobypro.shared.generated.resources.label_payment_status_pending
import doobypro.shared.generated.resources.screen_Order_dialog_cancel_message
import doobypro.shared.generated.resources.screen_Order_dialog_cancel_title
import doobypro.shared.generated.resources.screen_Order_dialog_delete_message
import doobypro.shared.generated.resources.screen_Order_dialog_delete_title
import doobypro.shared.generated.resources.screen_Order_field_customer
import doobypro.shared.generated.resources.screen_Order_field_delivery_date
import doobypro.shared.generated.resources.screen_Order_field_delivery_method
import doobypro.shared.generated.resources.screen_Order_field_delivery_option
import doobypro.shared.generated.resources.screen_Order_field_notes
import doobypro.shared.generated.resources.screen_Order_field_payment_status
import doobypro.shared.generated.resources.screen_Order_field_service
import doobypro.shared.generated.resources.screen_Order_field_storage
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.loading.SupernovaLoadingIndicator
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.app.core.ui.theme.statusColor
import se.supernovait.doobypro.presentation.order.component.DetailSection
import se.supernovait.doobypro.presentation.order.component.OrderActions
import se.supernovait.doobypro.presentation.order.component.OrderHeader

@Composable
fun OrderDetailsScreen(
    uiState: OrderDetailsState,
    onEvent: (OrderDetailsEvent) -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            SupernovaLoadingIndicator()
        }
        return
    }

    val order = uiState.order
    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            SupernovaLabel(text = uiState.error?.let { stringResource(it) } ?: "")
        }
        return
    }

    val dialogState = LocalDialogState.current
    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Order_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Order_dialog_delete_message)
    val cancelTitle = stringResource(Res.string.screen_Order_dialog_cancel_title)
    val cancelMessage = stringResource(Res.string.screen_Order_dialog_cancel_message)

    val statusColors = MaterialTheme.statusColor
    val paymentStatusText = if (order.isPaymentDone) stringResource(Res.string.label_payment_status_paid) else stringResource(Res.string.label_payment_status_pending)
    val paymentStatusColor = if (order.isPaymentDone) statusColors.success else statusColors.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        OrderHeader(order)
        
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        
        DetailSection(
            label = stringResource(Res.string.screen_Order_field_customer),
            value = "${order.customer.firstname} ${order.customer.lastname}"
        )
        DetailSection(
            label = stringResource(Res.string.screen_Order_field_service),
            value = order.service.title
        )
        DetailSection(
            label = stringResource(Res.string.screen_Order_field_storage),
            value = order.storageLocation.label
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium), color = MaterialTheme.colorScheme.outlineVariant)
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                DetailSection(
                    label = stringResource(Res.string.screen_Order_field_delivery_option),
                    value = stringResource(order.deliveryOption.label)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                DetailSection(
                    label = stringResource(Res.string.screen_Order_field_delivery_method),
                    value = stringResource(order.deliveryMethod.label)
                )
            }
        }

        DetailSection(
            label = stringResource(Res.string.screen_Order_field_delivery_date),
            value = order.deliveryDatetime.toString().replace("T", " ")
        )

        DetailSection(
            label = stringResource(Res.string.screen_Order_field_payment_status),
            value = paymentStatusText,
            valueColor = paymentStatusColor
        )
        
        if (!order.notes.isNullOrBlank()) {
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_notes),
                value = order.notes
            )
        }
        
        Spacer(Modifier.height(MaterialTheme.spacing.x2Large))
        
        OrderActions(
            order = order,
            onNextStatus = { onEvent(OrderDetailsEvent.TransitionToNextStatus) },
            onCancel = {
                dialogState.showConfirmation(
                    message = cancelMessage,
                    title = cancelTitle,
                    confirmLabel = Res.string.label_delete, 
                    dismissLabel = Res.string.label_cancel,
                    primaryActionColor = deleteColor,
                    onConfirm = { 
                        onEvent(OrderDetailsEvent.CancelOrder)
                        dialogState.hide()
                    },
                    onDismiss = { dialogState.hide() }
                )
            },
            onDelete = {
                dialogState.showConfirmation(
                    message = deleteMessage,
                    title = deleteTitle,
                    confirmLabel = Res.string.label_delete,
                    dismissLabel = Res.string.label_cancel,
                    primaryActionColor = deleteColor,
                    onConfirm = { 
                        onEvent(OrderDetailsEvent.DeleteOrder)
                        dialogState.hide()
                    },
                    onDismiss = { dialogState.hide() }
                )
            },
            onReissue = { onEvent(OrderDetailsEvent.ReissueOrder) }
        )
    }
}
