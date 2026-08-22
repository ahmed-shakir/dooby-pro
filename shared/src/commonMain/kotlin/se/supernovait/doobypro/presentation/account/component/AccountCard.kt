package se.supernovait.doobypro.presentation.account.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_edit_square
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.label_save
import doobypro.shared.generated.resources.label_saving
import doobypro.shared.generated.resources.screen_Account_dialog_cancel_action
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun AccountCard(
    title: String,
    isSaving: Boolean,
    isEditing: Boolean,
    onEditClick: (() -> Unit)? = null,
    onCancelClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = MaterialTheme.spacing.divider,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.small
            )
            .padding(MaterialTheme.spacing.medium)
    ) {
        // Card Header
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = MaterialTheme.spacing.extraLarge)
        ) {
            SupernovaLabel(
                text = title.uppercase(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            if (!isEditing && onEditClick != null) {
                SupernovaIconButton(
                    icon = Res.drawable.ic_edit_square,
                    contentDescription = stringResource(Res.string.label_edit),
                    tint = MaterialTheme.colorScheme.primary,
                    iconSize = MaterialTheme.sizing.icon.small,
                    onClick = onEditClick
                )
            }
        }

        Spacer(Modifier.height(MaterialTheme.spacing.small))

        // Card Content
        content()

        // Action Buttons (visible only in edit mode)
        if (isEditing) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                SupernovaOutlinedButton(
                    label = stringResource(Res.string.screen_Account_dialog_cancel_action),
                    onClick = onCancelClick,
                    enabled = !isSaving,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.weight(1f)
                )
                SupernovaButton(
                    label = if (isSaving) stringResource(Res.string.label_saving) else stringResource(Res.string.label_save),
                    onClick = onSaveClick,
                    enabled = !isSaving,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
