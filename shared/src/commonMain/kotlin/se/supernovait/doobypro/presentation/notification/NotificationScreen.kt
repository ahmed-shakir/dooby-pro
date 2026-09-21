package se.supernovait.doobypro.presentation.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.ic_check_circle
import doobypro.shared.generated.resources.ic_delete
import doobypro.shared.generated.resources.ic_error
import doobypro.shared.generated.resources.ic_info
import doobypro.shared.generated.resources.ic_read
import doobypro.shared.generated.resources.ic_warning
import doobypro.shared.generated.resources.label_delete
import doobypro.shared.generated.resources.screen_Notification_action_mark_all_read
import doobypro.shared.generated.resources.screen_Notification_action_mark_read
import doobypro.shared.generated.resources.screen_Notification_empty_state
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.domain.model.notification.Notification
import se.supernovait.app.core.domain.model.notification.NotificationType
import se.supernovait.app.core.ui.component.SupernovaIcon
import se.supernovait.app.core.ui.component.list.SupernovaSwipeableItem
import se.supernovait.app.core.ui.component.list.SwipeAction
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.component.text.SupernovaTitle
import se.supernovait.app.core.ui.component.topbar.LocalTopBarState
import se.supernovait.app.core.ui.component.topbar.TopBarAction
import se.supernovait.app.core.ui.theme.sizing
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.presentation.app.theme.statusColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    uiState: NotificationState,
    onEvent: (NotificationEvent) -> Unit,
) {
    val topBarState = LocalTopBarState.current
    val markAllReadLabel = stringResource(Res.string.screen_Notification_action_mark_all_read)

    DisposableEffect(uiState.notifications) {
        topBarState.actions(
            if (uiState.notifications.isNotEmpty()) {
                listOf(
                    TopBarAction(
                        icon = Res.drawable.ic_check_circle,
                        label = markAllReadLabel,
                        contentDescription = markAllReadLabel,
                        onClick = { onEvent(NotificationEvent.MarkAllAsRead) }
                    )
                )
            } else emptyList()
        )
        onDispose {
            topBarState.actions(emptyList())
        }
    }

    if (uiState.notifications.isEmpty() && !uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            SupernovaLabel(text = stringResource(Res.string.screen_Notification_empty_state))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            items(uiState.notifications, key = { it.id }) { notification ->
                SupernovaSwipeableItem(
                    startAction = SwipeAction(
                        icon = Res.drawable.ic_read,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        contentDescription = Res.string.screen_Notification_action_mark_read,
                        onSwipe = { onEvent(NotificationEvent.MarkAsRead(notification.id)) }
                    ),
                    endAction = SwipeAction(
                        icon = Res.drawable.ic_delete,
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        contentDescription = Res.string.label_delete,
                        onSwipe = { onEvent(NotificationEvent.DeleteNotification(notification)) }
                    )
                ) {
                    NotificationItem(
                        notification = notification
                    ) { onEvent(NotificationEvent.HandleNotificationClick(notification)) }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            NotificationIcon(notification.type)
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                SupernovaTitle(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                )
                SupernovaLabel(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (!notification.isRead) {
                Badge(
                    modifier = Modifier.size(MaterialTheme.spacing.small),
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun NotificationIcon(type: NotificationType) {
    val color = when (type) {
        NotificationType.SUCCESS -> MaterialTheme.statusColor.success
        NotificationType.WARNING -> MaterialTheme.statusColor.warning
        NotificationType.ERROR -> MaterialTheme.statusColor.error
        NotificationType.INFO -> MaterialTheme.statusColor.info
    }

    val icon = when (type) {
        NotificationType.SUCCESS -> Res.drawable.ic_check_circle
        NotificationType.WARNING -> Res.drawable.ic_warning
        NotificationType.ERROR -> Res.drawable.ic_error
        NotificationType.INFO -> Res.drawable.ic_info
    }
    
    SupernovaIcon(
        icon = icon,
        tint = color,
        size = MaterialTheme.sizing.icon.medium
    )
}
