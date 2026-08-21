package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_check_circle
import doobypro.shared.generated.resources.screen_Settings_printer_connection_method_label
import doobypro.shared.generated.resources.screen_Settings_printer_connection_status_connected
import doobypro.shared.generated.resources.screen_Settings_printer_disconnect_action
import doobypro.shared.generated.resources.screen_Settings_printer_search_action
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.list.SupernovaListItem
import se.supernovait.app.core.ui.component.selection.SupernovaRadioGroup
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.settings.printer.ConnectionMethod
import se.supernovait.doobypro.presentation.app.theme.statusColor
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun PrinterSettingsScreen(
    state: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val connectionMethodLabels = ConnectionMethod.entries.associateWith { stringResource(it.label) }
    val printer = state.settings.printer
    val isConnected = printer.printerAddress != null

    SettingsScreen {
        if (isConnected) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.medium)
            ) {
                Row(
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    SupernovaIcon(
                        icon = Res.drawable.ic_check_circle,
                        contentDescription = null as StringResource?,
                        tint = MaterialTheme.statusColor.active,
                        size = 20.dp
                    )
                    Column {
                        SupernovaLabel(
                            text = stringResource(Res.string.screen_Settings_printer_connection_status_connected),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        SupernovaLabel(
                            text = "${printer.printerName} (${printer.printerAddress})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            SupernovaOutlinedButton(
                label = stringResource(Res.string.screen_Settings_printer_disconnect_action),
                onClick = { onEvent(SettingsScreenEvent.DisconnectPrinter) },
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.medium)
            )
        }

        SupernovaLabel(
            text = Res.string.screen_Settings_printer_connection_method_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )

        SupernovaRadioGroup(
            options = ConnectionMethod.entries,
            selectedOption = printer.connectionMethod,
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdatePrinterConnectionMethod(it)) },
            optionLabel = { connectionMethodLabels[it] ?: "" }
        )

        if (state.isSearchingPrinters) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally).size(24.dp),
                strokeWidth = 2.dp
            )
        }

        if (state.discoveredPrinters.isNotEmpty()) {
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            state.discoveredPrinters.forEach { discovered ->
                SupernovaListItem(
                    title = discovered.name,
                    description = discovered.address,
                    onClick = { onEvent(SettingsScreenEvent.ConnectPrinter(discovered.name, discovered.address)) }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        SupernovaButton(
            label = stringResource(Res.string.screen_Settings_printer_search_action),
            onClick = { onEvent(SettingsScreenEvent.SearchPrinters) },
            shape = MaterialTheme.shapes.extraSmall,
            enabled = false, //!state.isSearchingPrinters, TODO: activate when printer service is in place
            modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.spacing.medium)
        )
    }
}
