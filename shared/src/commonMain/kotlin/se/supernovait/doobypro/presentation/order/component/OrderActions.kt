package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.screen_Order_action_cancel_order
import doobypro.shared.generated.resources.screen_Order_action_delivery_failed
import doobypro.shared.generated.resources.screen_Order_action_next_status_in_progress
import doobypro.shared.generated.resources.screen_Order_action_next_status_new
import doobypro.shared.generated.resources.screen_Order_action_next_status_out_for_delivery
import doobypro.shared.generated.resources.screen_Order_action_next_status_out_for_pickup
import doobypro.shared.generated.resources.screen_Order_action_next_status_ready_delivery
import doobypro.shared.generated.resources.screen_Order_action_next_status_ready_pickup
import doobypro.shared.generated.resources.screen_Order_action_pickup_failed
import doobypro.shared.generated.resources.screen_Order_action_reissue_order
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.CollectionMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.presentation.app.theme.statusColor

@Composable
fun OrderActions(
    order: Order,
    onNextStatus: () -> Unit,
    onPickupFailed: () -> Unit = {},
    onDeliveryFailed: () -> Unit = {},
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onReissue: () -> Unit
) {
    val nextStatusLabel = when (order.status) {
        OrderStatus.NEW -> {
            if (order.collectionMethod == CollectionMethod.HOME_PICKUP) {
                Res.string.screen_Order_action_next_status_out_for_pickup
            } else {
                Res.string.screen_Order_action_next_status_new
            }
        }
        OrderStatus.OUT_FOR_PICKUP -> Res.string.screen_Order_action_next_status_in_progress
        OrderStatus.IN_PROGRESS -> Res.string.screen_Order_action_next_status_in_progress
        OrderStatus.READY -> {
            if (order.deliveryMethod == DeliveryMethod.IN_STORE_PICKUP) {
                Res.string.screen_Order_action_next_status_ready_pickup
            } else {
                Res.string.screen_Order_action_next_status_ready_delivery
            }
        }
        OrderStatus.OUT_FOR_DELIVERY -> Res.string.screen_Order_action_next_status_out_for_delivery
        else -> null
    }

    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
        if (nextStatusLabel != null) {
            SupernovaButton(
                label = stringResource(nextStatusLabel),
                onClick = onNextStatus,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (order.status == OrderStatus.OUT_FOR_PICKUP) {
            SupernovaOutlinedButton(
                label = stringResource(Res.string.screen_Order_action_pickup_failed),
                onClick = onPickupFailed,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (order.status == OrderStatus.OUT_FOR_DELIVERY) {
            SupernovaOutlinedButton(
                label = stringResource(Res.string.screen_Order_action_delivery_failed),
                onClick = onDeliveryFailed,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (order.status == OrderStatus.CANCELLED) {
            SupernovaButton(
                label = stringResource(Res.string.screen_Order_action_reissue_order),
                onClick = onReissue,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
            if (!order.status.isTerminal()) {
                SupernovaOutlinedButton(
                    label = stringResource(Res.string.screen_Order_action_cancel_order),
                    onClick = onCancel,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.weight(1f)
                )
            }

            if (order.status == OrderStatus.NEW) {
                SupernovaOutlinedButton(
                    label = stringResource(Res.string.label_delete),
                    onClick = onDelete,
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.statusColor.error),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
