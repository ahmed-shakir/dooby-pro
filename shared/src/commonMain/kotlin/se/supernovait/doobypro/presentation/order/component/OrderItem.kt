package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_edit_square
import doobypro.shared.generated.resources.ic_refresh
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.screen_Order_action_reissue_order
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.app.core.ui.theme.statusColor
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus

@Composable
fun OrderItem(
    order: Order,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onReissue: (() -> Unit)? = null
) {
    val statusColor = when (order.status) {
        OrderStatus.NEW -> MaterialTheme.statusColor.info
        OrderStatus.IN_PROGRESS -> MaterialTheme.statusColor.warning
        OrderStatus.READY -> MaterialTheme.statusColor.success
        OrderStatus.CANCELLED -> MaterialTheme.statusColor.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = MaterialTheme.spacing.medium)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SupernovaLabel(
                    text = "${order.customer.firstname} ${order.customer.lastname}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(MaterialTheme.spacing.small))
                SupernovaTag(
                    text = order.status.label,
                    containerColor = statusColor.copy(alpha = 0.1f),
                    contentColor = statusColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            SupernovaLabel(
                text = order.service.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
            ) {
                SupernovaLabel(
                    text = order.storageLocation.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(MaterialTheme.spacing.medium))
                SupernovaLabel(
                    text = order.deliveryDatetime.toString().replace("T", " "), // Basic formatting for now
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (onReissue != null) {
            SupernovaIconButton(
                icon = Res.drawable.ic_refresh,
                contentDescription = stringResource(Res.string.screen_Order_action_reissue_order),
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
