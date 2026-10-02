package se.supernovait.doobypro.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.app.core.domain.model.notification.Notification
import se.supernovait.app.core.domain.notification.NotificationManager
import se.supernovait.doobypro.domain.util.LogTags

class NotificationViewModel(
    private val notificationManager: NotificationManager,
    private val logger: Logger
) : ViewModel() {

    init {
        logger.info("NotificationViewModel initialized", tag = LogTags.NOTIFICATION_VM)
    }

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
        logger.debug("Handling event: $event", tag = LogTags.NOTIFICATION_VM)
        when (event) {
            is NotificationEvent.MarkAsRead -> markAsRead(event.notificationId)
            NotificationEvent.MarkAllAsRead -> markAllAsRead()
            is NotificationEvent.DeleteNotification -> deleteNotification(event.notification)
            is NotificationEvent.HandleNotificationClick -> handleNotificationClick(event.notification)
        }
    }

    private fun markAsRead(notificationId: String) {
        logger.debug("Marking notification $notificationId as read", tag = LogTags.NOTIFICATION_VM)
        viewModelScope.launch {
            notificationManager.markAsRead(notificationId)
        }
    }

    private fun markAllAsRead() {
        logger.info("Marking all notifications as read", tag = LogTags.NOTIFICATION_VM)
        viewModelScope.launch {
            notificationManager.markAllAsRead()
        }
    }

    private fun deleteNotification(notification: Notification) {
        logger.info("Deleting notification ${notification.id}", tag = LogTags.NOTIFICATION_VM)
        viewModelScope.launch {
            notificationManager.delete(notification)
        }
    }

    private fun handleNotificationClick(notification: Notification) {
        logger.info("Handling click for notification ${notification.id}", tag = LogTags.NOTIFICATION_VM)
        viewModelScope.launch {
            notificationManager.handleNotificationClick(notification)
        }
    }
}
