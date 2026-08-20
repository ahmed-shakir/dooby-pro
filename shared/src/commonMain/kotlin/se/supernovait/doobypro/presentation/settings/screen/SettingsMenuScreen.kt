package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_common_description
import doobypro.shared.generated.resources.screen_Settings_common_label
import doobypro.shared.generated.resources.screen_Settings_notifications_description
import doobypro.shared.generated.resources.screen_Settings_notifications_label
import doobypro.shared.generated.resources.screen_Settings_order_description
import doobypro.shared.generated.resources.screen_Settings_order_label
import doobypro.shared.generated.resources.screen_Settings_printer_description
import doobypro.shared.generated.resources.screen_Settings_printer_label
import doobypro.shared.generated.resources.screen_Settings_receipt_description
import doobypro.shared.generated.resources.screen_Settings_receipt_label
import se.supernovait.app.core.ui.component.list.SupernovaListItem
import se.supernovait.doobypro.presentation.settings.event.SettingsNavigationEvent

@Composable
fun SettingsMenuScreen(
    onNavigation: (SettingsNavigationEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        SupernovaListItem(
            title = Res.string.screen_Settings_common_label,
            description = Res.string.screen_Settings_common_description,
            onClick = { onNavigation(SettingsNavigationEvent.NavigateToCommon) }
        )
        SupernovaListItem(
            title = Res.string.screen_Settings_order_label,
            description = Res.string.screen_Settings_order_description,
            onClick = { onNavigation(SettingsNavigationEvent.NavigateToOrder) }
        )
        SupernovaListItem(
            title = Res.string.screen_Settings_receipt_label,
            description = Res.string.screen_Settings_receipt_description,
            onClick = { onNavigation(SettingsNavigationEvent.NavigateToReceipt) }
        )
        SupernovaListItem(
            title = Res.string.screen_Settings_printer_label,
            description = Res.string.screen_Settings_printer_description,
            onClick = { onNavigation(SettingsNavigationEvent.NavigateToPrinter) }
        )
        SupernovaListItem(
            title = Res.string.screen_Settings_notifications_label,
            description = Res.string.screen_Settings_notifications_description,
            onClick = { onNavigation(SettingsNavigationEvent.NavigateToNotifications) }
        )
    }
}
