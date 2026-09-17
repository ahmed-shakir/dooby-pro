package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_storage_allocation_mode_label
import doobypro.shared.generated.resources.screen_Settings_storage_default_storage_label
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun StorageSettingsScreen(
    uiState: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val allocationModeLabels = StorageAllocationMode.entries.associateWith { stringResource(it.label) }
    val selectedStorageLocation = uiState.activeStorageLocations.find { it.id == uiState.settings.storage.defaultStorageLocationId }

    SettingsScreen {
        Spacer(Modifier.height(MaterialTheme.spacing.small))

        SupernovaSelectField(
            label = Res.string.screen_Settings_storage_allocation_mode_label,
            options = StorageAllocationMode.entries,
            selectedOption = uiState.settings.storage.storageAllocationMode,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateStorageAllocationMode(it)) },
            optionLabel = { allocationModeLabels[it] ?: "" }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Settings_storage_default_storage_label,
            options = uiState.activeStorageLocations,
            selectedOption = selectedStorageLocation,
            onOptionSelected = { it.id?.let { id -> onEvent(SettingsScreenEvent.UpdateDefaultStorageLocationId(id)) } },
            optionLabel = { it.label }
        )
    }
}
