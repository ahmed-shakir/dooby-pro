package se.supernovait.doobypro.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.model.notification.Notification
import se.supernovait.app.core.domain.notification.NotificationManager

class NotificationViewModel(
    private val notificationManager: NotificationManager
) : ViewModel() {

    val uiState: StateFlow<NotificationState> = combine(
        notificationManager.notifications,
        notificationManager.unreadCount
    ) { notifications, unreadCount ->
        NotificationState(
            notifications = notifications,
            unreadCount = unreadCount,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationState(isLoading = true)
    )

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.MarkAsRead -> markAsRead(event.notificationId)
            NotificationEvent.MarkAllAsRead -> markAllAsRead()
            is NotificationEvent.DeleteNotification -> deleteNotification(event.notification)
            is NotificationEvent.HandleNotificationClick -> handleNotificationClick(event.notification)
        }
    }

    private fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationManager.markAsRead(notificationId)
        }
    }

    private fun markAllAsRead() {
        viewModelScope.launch {
            notificationManager.markAllAsRead()
        }
    }

    private fun deleteNotification(notification: Notification) {
        viewModelScope.launch {
            notificationManager.delete(notification)
        }
    }

    private fun handleNotificationClick(notification: Notification) {
        viewModelScope.launch {
            notificationManager.handleNotificationClick(notification)
        }
    }
}
