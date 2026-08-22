package se.supernovait.doobypro.presentation.account.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_lock
import doobypro.shared.generated.resources.label_locked
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing

/**
 * A standard field component for account screens.
 *
 * @param label The text label to display above the field.
 * @param value The text value to display.
 * @param isLocked Whether the field is read-only and should display a lock icon.
 * @param isStatus Whether the value should be displayed as a status tag.
 * @param valueColor The color of the value text or status tag.
 * @param modifier The modifier to be applied to the field container.
 */
@Composable
fun AccountField(
    label: String,
    value: String,
    isLocked: Boolean = false,
    isStatus: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SupernovaLabel(
            text = label.uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        )
        Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))
        
        if (isLocked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(MaterialTheme.spacing.medium)
            ) {
                SupernovaLabel(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(MaterialTheme.spacing.small))
                SupernovaIcon(
                    icon = Res.drawable.ic_lock,
                    contentDescription = stringResource(Res.string.label_locked),
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.width(MaterialTheme.sizing.icon.small)
                )
            }
        } else if (isStatus && value.isNotBlank()) {
            SupernovaTag(
                text = value.uppercase(),
                containerColor = valueColor.copy(alpha = 0.12f),
                contentColor = valueColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        } else {
            SupernovaLabel(
                text = value.takeIf { it.isNotBlank() } ?: "—",
                color = valueColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
