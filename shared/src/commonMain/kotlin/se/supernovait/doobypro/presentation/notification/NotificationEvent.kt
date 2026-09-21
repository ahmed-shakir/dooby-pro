package se.supernovait.doobypro.presentation.notification

import se.supernovait.app.core.domain.model.notification.Notification

sealed interface NotificationEvent {
    data class MarkAsRead(val notificationId: String) : NotificationEvent
    data object MarkAllAsRead : NotificationEvent
    data class DeleteNotification(val notification: Notification) : NotificationEvent
    data class HandleNotificationClick(val notification: Notification) : NotificationEvent
}
