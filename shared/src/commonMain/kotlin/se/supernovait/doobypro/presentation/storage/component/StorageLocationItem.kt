package se.supernovait.doobypro.presentation.storage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_edit_square
import doobypro.shared.generated.resources.label_default
import doobypro.shared.generated.resources.label_edit
import doobypro.shared.generated.resources.screen_Storage_label_occupancy
import doobypro.shared.generated.resources.screen_Storage_label_unlimited
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.storage.StorageLocation

@Composable
fun StorageLocationItem(
    location: StorageLocation,
    onEdit: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = MaterialTheme.spacing.medium)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SupernovaLabel(
                    text = location.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (location.isDefault) {
                    Spacer(Modifier.width(MaterialTheme.spacing.small))
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
            SupernovaLabel(
                text = location.type.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(MaterialTheme.spacing.small))

            if (location.capacity > 0) {
                val progress = location.occupiedSlots.toFloat() / location.capacity.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.extraSmall),
                    color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                SupernovaLabel(
                    text = stringResource(Res.string.screen_Storage_label_occupancy, location.occupiedSlots, location.capacity),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (progress >= 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
                )
            } else {
                SupernovaLabel(
                    text = stringResource(Res.string.screen_Storage_label_unlimited),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SupernovaIconButton(
            icon = Res.drawable.ic_edit_square,
            contentDescription = Res.string.label_edit,
            onClick = onEdit
        )
    }
}
