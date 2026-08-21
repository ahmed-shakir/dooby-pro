package se.supernovait.doobypro.domain.model.settings.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val newOrders: Boolean = false,
    val readyOrders: Boolean = false,
    val lateOrders: Boolean = true,
    val orderNotPickedUp: Boolean = true,
    val orderNotDelivered: Boolean = true,
    val printerErrors: Boolean = true
)
