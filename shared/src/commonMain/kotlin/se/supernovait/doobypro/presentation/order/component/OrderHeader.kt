package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_thunderbolt
import doobypro.shared.generated.resources.ic_warning
import doobypro.shared.generated.resources.label_late
import doobypro.shared.generated.resources.screen_Order_badge_missing_address
import doobypro.shared.generated.resources.screen_Order_label_new_order
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.presentation.app.theme.statusColor

@Composable
fun OrderHeader(order: Order) {
    val statusColors = MaterialTheme.statusColor
    val statusColor = when (order.status) {
        OrderStatus.NEW, OrderStatus.OUT_FOR_PICKUP -> statusColors.info
        OrderStatus.IN_PROGRESS -> statusColors.warning
        OrderStatus.READY -> statusColors.success
        OrderStatus.OUT_FOR_DELIVERY -> statusColors.info
        OrderStatus.CANCELLED -> statusColors.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Order ID at the top, ensuring full visibility
        SupernovaLabel(
            text = order.id ?: stringResource(Res.string.screen_Order_label_new_order),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        // Metadata Row: Placed date
        SupernovaLabel(
            text = "Placed on ${order.orderDatetime.toString().replace("T", " ")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.extraSmall)
        )

        // Metadata Row: Status Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.small)
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
    }
}
