package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_edit_square
import doobypro.shared.generated.resources.ic_refresh
import doobypro.shared.generated.resources.ic_thunderbolt
import doobypro.shared.generated.resources.ic_warning
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.label_late
import doobypro.shared.generated.resources.screen_Order_action_reissue_order
import doobypro.shared.generated.resources.screen_Order_badge_missing_address
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.presentation.app.theme.statusColor

/**
 * List item component for displaying order details in the Order Hub list.
 *
 * Designed to handle long customer names, multi-badge arrangements (e.g., status, express, overdue),
 * and concise service summaries without layout overflow or clutter.
 *
 * @param order The [Order] domain model to render.
 * @param onClick Callback invoked when the order item is clicked.
 * @param onEdit Callback invoked when the edit button is clicked.
 * @param onReissue Optional callback invoked when the reissue button is clicked.
 * @param modifier Optional [Modifier] for root container layout customization.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderItem(
    order: Order,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onReissue: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val statusColor = when (order.status) {
        OrderStatus.NEW -> MaterialTheme.statusColor.info
        OrderStatus.IN_PROGRESS -> MaterialTheme.statusColor.warning
        OrderStatus.READY -> MaterialTheme.statusColor.success
        OrderStatus.CANCELLED -> MaterialTheme.statusColor.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = MaterialTheme.spacing.medium)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            // Badges Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.small)
            ) {
                SupernovaTag(
                    text = order.status.label,
                    containerColor = statusColor.copy(alpha = 0.1f),
                    contentColor = statusColor,
                    style = MaterialTheme.typography.labelSmall
                )

                if (order.deliveryOption == DeliveryOption.EXPRESS) {
                    SupernovaTag(
                        text = order.deliveryOption.label,
                        icon = Res.drawable.ic_thunderbolt,
                        containerColor = MaterialTheme.statusColor.express.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.statusColor.express,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                if (order.isOverdue()) {
                    SupernovaTag(
                        text = Res.string.label_late,
                        containerColor = MaterialTheme.statusColor.error.copy(alpha = 0.16f),
                        contentColor = MaterialTheme.statusColor.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                if (order.deliveryMethod == DeliveryMethod.HOME_DELIVERY && !order.customer.hasRegisteredAddress) {
                    SupernovaTag(
                        text = Res.string.screen_Order_badge_missing_address,
                        icon = Res.drawable.ic_warning,
                        containerColor = MaterialTheme.statusColor.error.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.statusColor.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Order ID Row
            SupernovaLabel(
                text = "#${order.id}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Header Row: Customer Name
            SupernovaLabel(
                text = order.customer.name.trim(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            // Storage Location and Delivery Date/Time Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium, Alignment.CenterHorizontally),
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
            ) {
                SupernovaLabel(
                    text = order.storageLocation.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                SupernovaLabel(
                    text = order.deliveryDatetime.toString().replace("T", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (order.isOverdue()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            // Action Buttons
            if (onReissue != null) {
                SupernovaIconButton(
                    icon = Res.drawable.ic_refresh,
                    contentDescription = Res.string.screen_Order_action_reissue_order,
                    onClick = onReissue
                )
            }

            if (!order.status.isTerminal()) {
                SupernovaIconButton(
                    icon = Res.drawable.ic_edit_square,
                    contentDescription = Res.string.label_edit,
                    onClick = onEdit
                )
            }
        }
    }
}
