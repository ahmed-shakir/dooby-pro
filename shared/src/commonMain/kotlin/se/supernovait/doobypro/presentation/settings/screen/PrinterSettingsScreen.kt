package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_printer_connection_method_label
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.selection.SupernovaRadioGroup
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.settings.printer.ConnectionMethod
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun PrinterSettingsScreen(
    state: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val connectionMethodLabels = ConnectionMethod.entries.associateWith { stringResource(it.label) }

    SettingsScreen {
        SupernovaLabel(
            text = Res.string.screen_Settings_printer_connection_method_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )

        SupernovaRadioGroup(
            options = ConnectionMethod.entries,
            selectedOption = state.settings.printer.connectionMethod,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdatePrinterConnectionMethod(it)) },
            optionLabel = { connectionMethodLabels[it] ?: "" }
        )
    }
}
