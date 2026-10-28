package se.supernovait.doobypro.presentation.order.details

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_warning
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_late
import doobypro.shared.generated.resources.label_payment_status_paid
import doobypro.shared.generated.resources.label_payment_status_pending
import doobypro.shared.generated.resources.screen_Order_action_cancel_order
import doobypro.shared.generated.resources.screen_Order_action_keep_order
import doobypro.shared.generated.resources.screen_Order_dialog_cancel_message
import doobypro.shared.generated.resources.screen_Order_dialog_cancel_title
import doobypro.shared.generated.resources.screen_Order_dialog_delete_message
import doobypro.shared.generated.resources.screen_Order_dialog_delete_title
import doobypro.shared.generated.resources.screen_Order_error_not_found
import doobypro.shared.generated.resources.screen_Order_field_collection_method
import doobypro.shared.generated.resources.screen_Order_field_customer
import doobypro.shared.generated.resources.screen_Order_field_delivery_address
import doobypro.shared.generated.resources.screen_Order_field_delivery_date
import doobypro.shared.generated.resources.screen_Order_field_delivery_method
import doobypro.shared.generated.resources.screen_Order_field_delivery_option
import doobypro.shared.generated.resources.screen_Order_field_notes
import doobypro.shared.generated.resources.screen_Order_field_payment_status
import doobypro.shared.generated.resources.screen_Order_field_storage
import doobypro.shared.generated.resources.screen_Order_warning_missing_customer_address
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaEmptyState
import se.supernovait.app.core.ui.component.loading.SupernovaLoadingScreen
import se.supernovait.app.core.ui.component.message.NoticeVariant
import se.supernovait.app.core.ui.component.message.SupernovaNotice
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.presentation.app.theme.statusColor
import se.supernovait.doobypro.presentation.order.component.DetailSection
import se.supernovait.doobypro.presentation.order.component.OrderActions
import se.supernovait.doobypro.presentation.order.component.OrderHeader
import se.supernovait.doobypro.presentation.order.component.OrderServicesSection

/**
 * Screen displaying the detailed view of an [Order].
 *
 * Provides customer information, storage location, payment status, an itemized services breakdown with fees
 * and totals in a receipt layout, delivery configuration, and actions.
 *
 * @param uiState The current [OrderDetailsState] containing order details, loading, or error states.
 * @param onEvent Callback for handling [OrderDetailsEvent] actions.
 */
@Composable
fun OrderDetailsScreen(
    uiState: OrderDetailsState,
    onEvent: (OrderDetailsEvent) -> Unit
) {
    if (uiState.isLoading) {
        SupernovaLoadingScreen()
        return
    }

    val order = uiState.order
    if (order == null) {
        SupernovaEmptyState(
            title = uiState.error ?: Res.string.screen_Order_error_not_found,
            icon = Res.drawable.ic_warning
        )
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

        if (!order.customer.hasRegisteredAddress && order.deliveryMethod == DeliveryMethod.HOME_DELIVERY) {
            SupernovaNotice(
                textResource = Res.string.screen_Order_warning_missing_customer_address,
                variant = NoticeVariant.Error
            )
            Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
        }

        DetailSection(
            label = stringResource(Res.string.screen_Order_field_customer),
            value = "${order.customer.firstname} ${order.customer.lastname}"
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_storage),
                value = order.storageLocation.label,
                modifier = Modifier.weight(1f)
            )
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_payment_status),
                value = paymentStatusText,
                valueColor = paymentStatusColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        OrderServicesSection(
            order = order,
            expressFeeType = uiState.expressFeeType,
            expressFeeValue = uiState.expressFeeValue,
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.medium),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        Row(modifier = Modifier.fillMaxWidth()) {
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_delivery_option),
                value = stringResource(order.deliveryOption.label),
                modifier = Modifier.weight(1f)
            )
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_collection_method),
                value = stringResource(order.collectionMethod.label),
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_delivery_date),
                value = order.deliveryDatetime.toString().replace("T", " "),
                status = if (order.isLate()) stringResource(Res.string.label_late) else null,
                valueColor = if (order.isLate()) MaterialTheme.statusColor.error else Color.Unspecified,
                modifier = Modifier.weight(1f)
            )
            DetailSection(
                label = stringResource(Res.string.screen_Order_field_delivery_method),
                value = stringResource(order.deliveryMethod.label),
                modifier = Modifier.weight(1f)
            )
        }

        if (order.customer.hasRegisteredAddress) {
            order.customer.address?.let { addr ->
                val addressString = listOfNotNull(
                    addr.street.ifBlank { null },
                    addr.city.ifBlank { null },
                    addr.country.ifBlank { null }
                ).joinToString(", ")
                if (addressString.isNotBlank()) {
                    DetailSection(
                        label = stringResource(Res.string.screen_Order_field_delivery_address),
                        value = addressString
                    )
                }
            }
        }
        
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
            onDeliveryFailed = { onEvent(OrderDetailsEvent.DeliveryFailed) },
            onPickupFailed = { onEvent(OrderDetailsEvent.PickupFailed) },
            onCancel = {
                dialogState.showConfirmation(
                    message = cancelMessage,
                    title = cancelTitle,
                    confirmLabel = Res.string.screen_Order_action_cancel_order, 
                    dismissLabel = Res.string.screen_Order_action_keep_order,
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
