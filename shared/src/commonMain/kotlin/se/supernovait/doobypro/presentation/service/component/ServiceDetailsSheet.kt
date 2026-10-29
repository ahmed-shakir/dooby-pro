package se.supernovait.doobypro.presentation.service.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.app_action_close_label
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.screen_Service_dialog_delete_message
import doobypro.shared.generated.resources.screen_Service_dialog_delete_title
import doobypro.shared.generated.resources.screen_Service_field_description
import doobypro.shared.generated.resources.screen_Service_field_price
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.action.SupernovaTextAction
import se.supernovait.app.core.ui.component.modal.LocalBottomSheetState
import se.supernovait.app.core.ui.component.modal.dialog.LocalDialogState
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.presentation.order.component.DetailSection

@Composable
fun ServiceDetailsSheet(
    service: Service,
    currency: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val bottomSheetState = LocalBottomSheetState.current
    val dialogState = LocalDialogState.current
    val deleteColor = MaterialTheme.colorScheme.error
    val deleteTitle = stringResource(Res.string.screen_Service_dialog_delete_title)
    val deleteMessage = stringResource(Res.string.screen_Service_dialog_delete_message)

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
            SupernovaTitle(
                text = service.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
            SupernovaTextAction(
                label = Res.string.app_action_close_label,
                textStyle = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = { bottomSheetState.hide() }
            )
        }
        Spacer(Modifier.height(MaterialTheme.spacing.medium))

        DetailSection(
            label = stringResource(Res.string.screen_Service_field_description),
            value = service.description.ifBlank { "-" }
        )

        DetailSection(
            label = stringResource(Res.string.screen_Service_field_price, currency),
            value = service.price.formatted
        )

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
