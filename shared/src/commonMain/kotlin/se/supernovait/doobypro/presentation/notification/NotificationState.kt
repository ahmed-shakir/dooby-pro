package se.supernovait.doobypro.presentation.notification

import se.supernovait.app.core.domain.model.notification.Notification

data class NotificationState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false
)
