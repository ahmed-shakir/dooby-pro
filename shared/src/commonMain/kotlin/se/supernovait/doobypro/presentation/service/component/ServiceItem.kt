package se.supernovait.doobypro.presentation.service.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_edit_square
import doobypro.shared.generated.resources.label_edit
import se.supernovait.app.core.ui.component.action.SupernovaIconButton
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.Service

@Composable
fun ServiceItem(
    service: Service,
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
            SupernovaLabel(
                text = service.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SupernovaLabel(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            SupernovaLabel(
                text = service.price.formatted,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall)
            )
        }

        SupernovaIconButton(
            icon = Res.drawable.ic_edit_square,
            contentDescription = Res.string.label_edit,
            onClick = onEdit
        )
    }
}
