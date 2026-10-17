package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.screen_Order_all_services_added
import doobypro.shared.generated.resources.screen_Order_no_services_available
import doobypro.shared.generated.resources.screen_Service_action_add_service
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.container.SupernovaListGroup
import se.supernovait.app.core.ui.component.input.SupernovaCheckbox
import se.supernovait.app.core.ui.component.modal.dialog.SupernovaDialogButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.OrderServiceItem

/**
 * Content for the dialog that allows selecting and adding multiple services to an order.
 *
 * @param services List of all available catalog services.
 * @param existingItems Currently added items in the order (used to filter out already added services).
 * @param onDismiss Callback invoked when the dialog is dismissed or cancelled.
 * @param onAddServices Callback invoked with newly selected service items when confirmed.
 */
@Composable
fun AddServiceDialog(
    services: List<Service>,
    existingItems: List<OrderServiceItem>,
    onDismiss: () -> Unit,
    onAddServices: (List<OrderServiceItem>) -> Unit
) {
    val availableServices = services.filter { service -> existingItems.none { it.service.id == service.id } }
    var dialogSelectedIds by remember { mutableStateOf(emptySet<String>()) }

    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium)
        ) {
            SupernovaTitle(
                text = stringResource(Res.string.screen_Service_action_add_service),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))

            if (availableServices.isEmpty()) {
                val emptyMessage = if (services.isEmpty()) {
                    stringResource(Res.string.screen_Order_no_services_available)
                } else {
                    stringResource(Res.string.screen_Order_all_services_added)
                }
                SupernovaLabel(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    SupernovaListGroup {
                        availableServices.forEach { service ->
                            val isChecked = dialogSelectedIds.contains(service.id)
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = MaterialTheme.spacing.small)
                                        .clickable {
                                            dialogSelectedIds = if (isChecked) {
                                                dialogSelectedIds - service.id!!
                                            } else {
                                                dialogSelectedIds + service.id!!
                                            }
                                        }
                                ) {
                                    SupernovaCheckbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            dialogSelectedIds = if (checked) {
                                                dialogSelectedIds + service.id!!
                                            } else {
                                                dialogSelectedIds - service.id!!
                                            }
                                        }
                                    )
                                    Spacer(Modifier.width(MaterialTheme.spacing.small))
                                    Column(modifier = Modifier.weight(1f)) {
                                        SupernovaLabel(
                                            text = service.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        SupernovaLabel(
                                            text = service.price.formatted,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.divider),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            SupernovaDialogButton(
                label = stringResource(Res.string.label_cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            )
            SupernovaDialogButton(
                label = stringResource(Res.string.label_save),
                onClick = {
                    val newItems = availableServices
                        .filter { dialogSelectedIds.contains(it.id) }
                        .map { OrderServiceItem(service = it, quantity = 1) }
                    onAddServices(newItems)
                },
                enabled = dialogSelectedIds.isNotEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
