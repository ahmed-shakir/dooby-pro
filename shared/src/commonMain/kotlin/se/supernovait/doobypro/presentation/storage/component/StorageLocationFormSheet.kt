package se.supernovait.doobypro.presentation.storage.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.screen_Storage_action_add_location
import doobypro.shared.generated.resources.screen_Storage_field_capacity
import doobypro.shared.generated.resources.screen_Storage_field_label
import doobypro.shared.generated.resources.screen_Storage_field_type
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.input.SupernovaTextField
import se.supernovait.app.core.ui.component.selection.SupernovaSelectField
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType
import se.supernovait.doobypro.presentation.app.theme.statusColor

@Composable
fun StorageLocationFormSheet(
    location: StorageLocation,
    onSave: (String, StorageType, Int) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var label by remember { mutableStateOf(location.label) }
    var type by remember { mutableStateOf(location.type) }
    var capacity by remember { mutableStateOf(location.capacity.toString()) }

    val isNew = location.id == null
    val typeLabels = StorageType.entries.associateWith { stringResource(it.label) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        SupernovaTitle(
            text = if (isNew) stringResource(Res.string.screen_Storage_action_add_location) else stringResource(Res.string.label_edit),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaTextField(
            label = stringResource(Res.string.screen_Storage_field_label),
            value = label,
            onValueChange = { v, _ -> label = v },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        SupernovaSelectField(
            label = Res.string.screen_Storage_field_type,
            options = StorageType.entries,
            selectedOption = type,
            onOptionSelected = { type = it },
            optionLabel = { typeLabels[it] ?: it.name }
        )

        if (!location.isDefault) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            SupernovaTextField(
                label = stringResource(Res.string.screen_Storage_field_capacity),
                value = capacity,
                onValueChange = { v, _ -> capacity = v },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            if (onDelete != null && !location.isDefault) {
                SupernovaOutlinedButton(
                    label = Res.string.label_delete,
                    onClick = onDelete,
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.statusColor.error),
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            SupernovaOutlinedButton(
                label = Res.string.label_save,
                onClick = { onSave(label, type, capacity.toIntOrNull() ?: 0) },
                enabled = label.isNotBlank() && (location.isDefault || (capacity.toIntOrNull() ?: 0) > 0),
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
