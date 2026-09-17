package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.screen_Order_action_add_order
import doobypro.shared.generated.resources.screen_Order_field_customer
import doobypro.shared.generated.resources.screen_Order_field_delivery_date
import doobypro.shared.generated.resources.screen_Order_field_delivery_method
import doobypro.shared.generated.resources.screen_Order_field_delivery_option
import doobypro.shared.generated.resources.screen_Order_field_notes
import doobypro.shared.generated.resources.screen_Order_field_payment_status
import doobypro.shared.generated.resources.screen_Order_field_service
import doobypro.shared.generated.resources.screen_Order_field_storage
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.input.SupernovaDatetimeField
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.storage.StorageLocation

@Composable
fun OrderFormSheet(
    order: Order,
    customers: List<User>,
    services: List<Service>,
    storageLocations: List<StorageLocation>,
    isManualStorageMode: Boolean,
    onSave: (Order) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var selectedCustomer by remember { mutableStateOf(order.customer) }
    var selectedService by remember { mutableStateOf(order.service) }
    var selectedLocation by remember { mutableStateOf(order.storageLocation) }
    var deliveryDatetime by remember { mutableStateOf(order.deliveryDatetime) }
    var deliveryOption by remember { mutableStateOf(order.deliveryOption) }
    var deliveryMethod by remember { mutableStateOf(order.deliveryMethod) }
    var isPaymentDone by remember { mutableStateOf(order.isPaymentDone) }
    var notes by remember { mutableStateOf(order.notes ?: "") }

    val isNew = order.id == null

    val deliveryOptionLabels = DeliveryOption.entries.associateWith { stringResource(it.label) }
    val deliveryMethodLabels = DeliveryMethod.entries.associateWith { stringResource(it.label) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.medium)
    ) {
        SupernovaTitle(
            text = if (isNew) stringResource(Res.string.screen_Order_action_add_order) else stringResource(Res.string.label_edit),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 1. customer
        SupernovaSelectField(
            label = Res.string.screen_Order_field_customer,
            options = customers,
            selectedOption = selectedCustomer,
            onOptionSelected = { selectedCustomer = it },
            optionLabel = { "${it.firstname} ${it.lastname}" }
        )
        
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 2. service
        SupernovaSelectField(
            label = Res.string.screen_Order_field_service,
            options = services,
            selectedOption = selectedService,
            onOptionSelected = { selectedService = it },
            optionLabel = { it.title }
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 3. storageLocation
        SupernovaSelectField(
            label = Res.string.screen_Order_field_storage,
            options = storageLocations,
            selectedOption = selectedLocation,
            onOptionSelected = { selectedLocation = it },
            optionLabel = { it.label },
            enabled = isManualStorageMode || !isNew
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 4. deliveryDatetime
        SupernovaDatetimeField(
            label = stringResource(Res.string.screen_Order_field_delivery_date),
            value = deliveryDatetime,
            onValueChange = { datetime, isValid ->
                if (isValid && datetime != null) {
                    deliveryDatetime = datetime
                }
            }
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 5. deliveryOption
        SupernovaSelectField(
            label = Res.string.screen_Order_field_delivery_option,
            options = DeliveryOption.entries,
            selectedOption = deliveryOption,
            onOptionSelected = { deliveryOption = it },
            optionLabel = { deliveryOptionLabels[it] ?: it.name }
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 6. deliveryMethod
        SupernovaSelectField(
            label = Res.string.screen_Order_field_delivery_method,
            options = DeliveryMethod.entries,
            selectedOption = deliveryMethod,
            onOptionSelected = { deliveryMethod = it },
            optionLabel = { deliveryMethodLabels[it] ?: it.name }
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 7. isPaymentDone
        SupernovaToggle(
            label = Res.string.screen_Order_field_payment_status,
            checked = isPaymentDone,
            onCheckedChange = { isPaymentDone = it }
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        // 8. notes
        SupernovaTextField(
            label = stringResource(Res.string.screen_Order_field_notes),
            value = notes,
            onValueChange = { v, _ -> notes = v },
            isMultiline = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            if (onDelete != null && !isNew) {
                SupernovaOutlinedButton(
                    label = Res.string.label_delete,
                    onClick = onDelete,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            val isValid = selectedCustomer.id != null && selectedService.id != null

            SupernovaOutlinedButton(
                label = Res.string.label_save,
                onClick = { 
                    onSave(order.copy(
                        customer = selectedCustomer,
                        service = selectedService,
                        storageLocation = selectedLocation,
                        deliveryDatetime = deliveryDatetime,
                        deliveryOption = deliveryOption,
                        deliveryMethod = deliveryMethod,
                        isPaymentDone = isPaymentDone,
                        notes = notes.ifBlank { null }
                    )) 
                },
                enabled = isValid,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.x5Large))
    }
}
