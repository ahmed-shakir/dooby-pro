package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTag
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun DetailSection(
    label: String,
    value: String,
    status: String? = null,
    valueColor: Color = Color.Unspecified
) {
    val finalValueColor = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor

    Column(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)) {
        SupernovaLabel(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            SupernovaLabel(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = finalValueColor,
                fontWeight = FontWeight.Medium
            )
            if (!status.isNullOrBlank()) {
                SupernovaTag(
                    text = status.uppercase(),
                    containerColor = valueColor.copy(alpha = 0.16f),
                    contentColor = valueColor,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
