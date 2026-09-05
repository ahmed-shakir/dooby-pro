package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_order_auto_print_receipts_label
import doobypro.shared.generated.resources.screen_Settings_order_behavior_label
import doobypro.shared.generated.resources.screen_Settings_order_default_service_label
import doobypro.shared.generated.resources.screen_Settings_order_default_storage_label
import doobypro.shared.generated.resources.screen_Settings_order_delivery_method_label
import doobypro.shared.generated.resources.screen_Settings_order_delivery_option_label
import doobypro.shared.generated.resources.screen_Settings_order_handling_time_label
import doobypro.shared.generated.resources.screen_Settings_order_storage_allocation_label
import doobypro.shared.generated.resources.screen_Storage_title
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun OrderSettingsScreen(
    state: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val selectedService = state.services.find { it.id == state.settings.order.defaultServiceId }
    val deliveryOptionLabels = DeliveryOption.entries.associateWith { stringResource(it.label) }
    val deliveryMethodLabels = DeliveryMethod.entries.associateWith { stringResource(it.label) }
    
    val allocationModeLabels = StorageAllocationMode.entries.associateWith { stringResource(it.label) }
    val selectedStorageLocation = state.activeStorageLocations.find { it.id == state.settings.order.defaultStorageLocationId }

    SettingsScreen {
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_default_service_label,
            options = state.services,
            selectedOption = selectedService,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDefaultServiceId(it.id)) },
            optionLabel = { it.title }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_delivery_option_label,
            options = DeliveryOption.entries,
            selectedOption = state.settings.order.defaultDeliveryOption,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDefaultDeliveryOption(it)) },
            optionLabel = { deliveryOptionLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_delivery_method_label,
            options = DeliveryMethod.entries,
            selectedOption = state.settings.order.defaultDeliveryMethod,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateDefaultDeliveryMethod(it)) },
            optionLabel = { deliveryMethodLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Settings_order_handling_time_label),
            value = state.settings.order.defaultHandlingTimeDays.toString(),
            onValueChange = { newValue, _ ->
                newValue.toIntOrNull()?.let {
                    onEvent(SettingsScreenEvent.UpdateDefaultHandlingTimeDays(it))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.extraSmall, horizontal = MaterialTheme.spacing.extraSmall)
        )

        SupernovaLabel(
            text = Res.string.screen_Settings_order_behavior_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large)
        )

        SupernovaToggle(
            label = Res.string.screen_Settings_order_auto_print_receipts_label,
            checked = state.settings.order.autoPrintReceipts,
            onCheckedChange = { onEvent(SettingsScreenEvent.UpdateAutoPrintReceipts(it)) }
        )

        SupernovaLabel(
            text = Res.string.screen_Storage_title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_storage_allocation_label,
            options = StorageAllocationMode.entries,
            selectedOption = state.settings.order.storageAllocationMode,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateStorageAllocationMode(it)) },
            optionLabel = { allocationModeLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_order_default_storage_label,
            options = state.activeStorageLocations,
            selectedOption = selectedStorageLocation,
            onOptionSelected = { it.id?.let { id -> onEvent(SettingsScreenEvent.UpdateDefaultStorageLocationId(id)) } },
            optionLabel = { it.label }
        )
    }
}
