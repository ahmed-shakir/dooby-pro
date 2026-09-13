package se.supernovait.doobypro.presentation.order.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun DetailSection(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    val finalValueColor = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor

    Column(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)) {
        SupernovaLabel(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        SupernovaLabel(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = finalValueColor,
            fontWeight = FontWeight.Medium
        )
    }
}
