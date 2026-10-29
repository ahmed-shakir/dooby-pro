package se.supernovait.doobypro.presentation.storage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_action_close_label
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_default
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.screen_Storage_dialog_delete_message
import doobypro.shared.generated.resources.screen_Storage_dialog_delete_title
import doobypro.shared.generated.resources.screen_Storage_field_capacity
import doobypro.shared.generated.resources.screen_Storage_field_type
import doobypro.shared.generated.resources.screen_Storage_label_occupancy
import doobypro.shared.generated.resources.screen_Storage_label_unlimited
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.presentation.order.component.DetailSection

@Composable
fun StorageLocationDetailsSheet(
    location: StorageLocation,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val dialogState = LocalDialogState.current
    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Storage_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Storage_dialog_delete_message)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                modifier = Modifier.weight(1f)
            ) {
                SupernovaTitle(
                    text = location.label,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                if (location.isDefault) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.tiny)
                    ) {
                        SupernovaLabel(
                            text = Res.string.label_default,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            SupernovaTextAction(
                label = Res.string.app_action_close_label,
                textStyle = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = { bottomSheetState.hide() }
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        DetailSection(
            label = stringResource(Res.string.screen_Storage_field_type),
            value = location.type.name
        )

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.small)) {
            SupernovaLabel(
                text = stringResource(Res.string.screen_Storage_field_capacity),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
            if (location.capacity > 0) {
                val progress = location.occupiedSlots.toFloat() / location.capacity.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraSmall),
                    color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
                SupernovaLabel(
                    text = stringResource(Res.string.screen_Storage_label_occupancy, location.occupiedSlots, location.capacity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                SupernovaLabel(
                    text = stringResource(Res.string.screen_Storage_label_unlimited),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            SupernovaOutlinedButton(
                label = Res.string.label_delete,
                onClick = {
                    dialogState.showConfirmation(
                        title = deleteTitle,
                        message = deleteMessage,
                        confirmLabel = Res.string.label_delete,
                        dismissLabel = Res.string.label_cancel,
                        primaryActionColor = deleteColor,
                        onConfirm = {
                            onDelete()
                            dialogState.hide()
                        },
                        onDismiss = { dialogState.hide() }
                    )
                },
                shape = MaterialTheme.shapes.extraSmall,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = deleteColor),
                modifier = Modifier.weight(1f)
            )

            SupernovaOutlinedButton(
                label = Res.string.label_edit,
                onClick = onEdit,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
