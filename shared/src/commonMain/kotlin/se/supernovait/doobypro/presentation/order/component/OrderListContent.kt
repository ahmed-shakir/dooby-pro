package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_empty_state
import doobypro.shared.generated.resources.screen_Order_search_hint
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.input.SupernovaSearchField
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.order.Order

@Composable
fun OrderListContent(
    orders: List<Order>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOrderClick: (String) -> Unit,
    onEditOrder: ((Order) -> Unit)? = null,
    onReissueOrder: ((Order) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SupernovaSearchField(
            value = searchQuery,
            onValueChange = onSearchChange,
            onSearch = onSearchChange,
            placeholder = stringResource(Res.string.screen_Order_search_hint),
            modifier = Modifier.fillMaxWidth().padding(MaterialTheme.spacing.medium)
        )

        Box(modifier = Modifier.weight(1f)) {
            if (orders.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.large),
                    contentAlignment = Alignment.Center
                ) {
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
                    items(orders) { order ->
                        OrderItem(
                            order = order,
                            onClick = { onOrderClick(order.id!!) },
                            onEdit = { onEditOrder?.invoke(order) },
                            onReissue = onReissueOrder?.let { { it(order) } }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    item { Spacer(Modifier.height(MaterialTheme.spacing.x5Large)) }
                }
            }
        }
    }
}
