package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_notifications_order_label
import doobypro.shared.generated.resources.screen_Settings_notifications_order_late
import doobypro.shared.generated.resources.screen_Settings_notifications_order_new
import doobypro.shared.generated.resources.screen_Settings_notifications_order_not_delivered
import doobypro.shared.generated.resources.screen_Settings_notifications_order_not_picked_up
import doobypro.shared.generated.resources.screen_Settings_notifications_order_ready
import doobypro.shared.generated.resources.screen_Settings_notifications_system_label
import doobypro.shared.generated.resources.screen_Settings_notifications_system_printer_error
import se.supernovait.app.core.ui.component.container.SupernovaListGroup
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun NotificationSettingsScreen(
    state: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    SettingsScreen {
        SupernovaLabel(
            text = Res.string.screen_Settings_notifications_order_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_new,
                    checked = state.settings.notifications.newOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateNewOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_ready,
                    checked = state.settings.notifications.readyOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateReadyOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_late,
                    checked = state.settings.notifications.lateOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateLateOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_not_picked_up,
                    checked = state.settings.notifications.orderNotPickedUp,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateOrderNotPickedUpNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_not_delivered,
                    checked = state.settings.notifications.orderNotDelivered,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateOrderNotDeliveredNotification(it)) }
                )
            }
        }

        SupernovaLabel(
            text = Res.string.screen_Settings_notifications_system_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_system_printer_error,
                    checked = state.settings.notifications.printerErrors,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdatePrinterErrorsNotification(it)) }
                )
            }
        }
    }
}
