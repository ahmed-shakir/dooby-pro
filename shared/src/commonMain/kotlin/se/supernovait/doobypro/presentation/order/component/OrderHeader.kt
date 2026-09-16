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
import doobypro.shared.generated.resources.screen_Order_label_new_order
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.app.core.ui.theme.statusColor
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus

@Composable
fun OrderHeader(order: Order) {
    val statusColors = MaterialTheme.statusColor
    val statusColor = when (order.status) {
        OrderStatus.NEW -> statusColors.info
        OrderStatus.IN_PROGRESS -> statusColors.warning
        OrderStatus.READY -> statusColors.success
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

        // Metadata Row: Placed date and Status Tag
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.extraSmall)
        ) {
            SupernovaLabel(
                text = "Placed on ${order.orderDatetime.toString().replace("T", " ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SupernovaTag(
                text = order.status.label,
                containerColor = statusColor.copy(alpha = 0.1f),
                contentColor = statusColor
            )
        }
    }
}
