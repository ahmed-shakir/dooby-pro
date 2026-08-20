package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Settings_order_notifications_delivery
import doobypro.shared.generated.resources.screen_Settings_order_notifications_label
import doobypro.shared.generated.resources.screen_Settings_order_notifications_new_orders
import doobypro.shared.generated.resources.screen_Settings_order_notifications_ready
import doobypro.shared.generated.resources.screen_Settings_system_notifications_label
import doobypro.shared.generated.resources.screen_Settings_system_notifications_payment_failure
import doobypro.shared.generated.resources.screen_Settings_system_notifications_printer_error
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
            text = Res.string.screen_Settings_order_notifications_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_notifications_new_orders,
                    checked = state.settings.notifications.newOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateNewOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_notifications_ready,
                    checked = state.settings.notifications.orderReady,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateOrderReadyNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_order_notifications_delivery,
                    checked = state.settings.notifications.deliveryUpdates,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateDeliveryUpdatesNotification(it)) }
                )
            }
        }

        SupernovaLabel(
            text = Res.string.screen_Settings_system_notifications_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaListGroup {
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_system_notifications_printer_error,
                    checked = state.settings.notifications.printerErrors,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdatePrinterErrorsNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_system_notifications_payment_failure,
                    checked = state.settings.notifications.paymentFailures,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdatePaymentFailuresNotification(it)) }
                )
            }
        }
    }
}
