package se.supernovait.doobypro.presentation.settings.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.notification_schedule_label
import doobypro.shared.generated.resources.screen_Settings_notifications_order_label
import doobypro.shared.generated.resources.screen_Settings_notifications_order_late
import doobypro.shared.generated.resources.screen_Settings_notifications_order_new
import doobypro.shared.generated.resources.screen_Settings_notifications_order_not_delivered
import doobypro.shared.generated.resources.screen_Settings_notifications_order_not_picked_up
import doobypro.shared.generated.resources.screen_Settings_notifications_order_ready
import doobypro.shared.generated.resources.screen_Settings_notifications_system_label
import doobypro.shared.generated.resources.screen_Settings_notifications_system_printer_error
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.container.SupernovaListGroup
import se.supernovait.app.core.ui.component.selection.SupernovaSelectionGroup
import se.supernovait.app.core.ui.component.selection.SupernovaToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.settings.notification.NotificationSchedule
import se.supernovait.doobypro.presentation.settings.SettingsState
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

@Composable
fun NotificationSettingsScreen(
    uiState: SettingsState,
    onEvent: (SettingsScreenEvent) -> Unit
) {
    val notificationScheduleLabels = NotificationSchedule.entries.associateWith { stringResource(it.label) }

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
                    checked = uiState.settings.notification.newOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateNewOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_ready,
                    checked = uiState.settings.notification.readyOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateReadyOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_late,
                    checked = uiState.settings.notification.lateOrders,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateLateOrdersNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_not_picked_up,
                    checked = uiState.settings.notification.orderNotPickedUp,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdateOrderNotPickedUpNotification(it)) }
                )
            }
            item {
                SupernovaToggle(
                    label = Res.string.screen_Settings_notifications_order_not_delivered,
                    checked = uiState.settings.notification.orderNotDelivered,
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
                    checked = uiState.settings.notification.printerErrors,
                    onCheckedChange = { onEvent(SettingsScreenEvent.UpdatePrinterErrorsNotification(it)) }
                )
            }
        }

        SupernovaLabel(
            text = Res.string.notification_schedule_label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = MaterialTheme.spacing.large, bottom = MaterialTheme.spacing.small)
        )
        SupernovaSelectionGroup(
            options = NotificationSchedule.entries,
            selectedOption = uiState.settings.notification.notificationSchedule,
            optionLabel = { notificationScheduleLabels[it] ?: "" },
            onOptionSelected = { onEvent(SettingsScreenEvent.UpdateNotificationSchedule(it)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
