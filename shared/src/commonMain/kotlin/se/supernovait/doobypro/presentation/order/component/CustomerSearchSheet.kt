package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_add
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.screen_Order_label_add_new_customer
import doobypro.shared.generated.resources.screen_Order_search_customer_hint
import doobypro.shared.generated.resources.screen_Order_search_customer_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.input.SupernovaSearchField
import se.supernovait.app.core.ui.component.list.SupernovaListItem
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun CustomerSearchSheet(
    customers: List<User>,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onSelect: (User) -> Unit,
    onAddClick: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            SupernovaTitle(
                text = stringResource(Res.string.screen_Order_search_customer_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
            SupernovaIconButton(
                icon = Res.drawable.ic_add,
                contentDescription = stringResource(Res.string.screen_Order_label_add_new_customer),
                tint = MaterialTheme.colorScheme.onSurface,
                onClick = onAddClick
            )
        }
        
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSearchField(
            value = searchQuery,
            onValueChange = { onSearch(it) },
            onSearch = { onSearch(it) },
            placeholder = stringResource(Res.string.screen_Order_search_customer_hint),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(customers) { customer ->
                SupernovaListItem(
                    title = "${customer.firstname} ${customer.lastname}",
                    description = customer.email,
                    onClick = { onSelect(customer) }
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            SupernovaOutlinedButton(
                label = Res.string.label_cancel,
                onClick = onCancel,
                shape = MaterialTheme.shapes.extraSmall
            )
        }
    }
}
