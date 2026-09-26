package se.supernovait.doobypro.presentation.businesshours.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.business_hours_closes
import doobypro.shared.generated.resources.business_hours_opens
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing

@Composable
fun TimeRangePicker(
    openTime: LocalTime,
    closeTime: LocalTime,
    onOpenTimeChange: (LocalTime) -> Unit,
    onCloseTimeChange: (LocalTime) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
    ) {
        // Labels Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            SupernovaLabel(
                text = stringResource(Res.string.business_hours_opens),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.weight(0.2f))
            SupernovaLabel(
                text = stringResource(Res.string.business_hours_closes),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        // Fields Row with vertically centered '-'
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TimePickerField(
                    value = openTime,
                    onValueChange = onOpenTimeChange
                )
            }

            Box(
                modifier = Modifier.weight(0.2f),
                contentAlignment = Alignment.Center
            ) {
                SupernovaLabel(
                    text = "-",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                TimePickerField(
                    value = closeTime,
                    onValueChange = onCloseTimeChange
                )
            }
        }
    }
}
