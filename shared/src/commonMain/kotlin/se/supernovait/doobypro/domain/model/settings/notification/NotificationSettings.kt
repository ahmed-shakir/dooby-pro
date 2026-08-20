package se.supernovait.doobypro.domain.model.settings.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettings(
    val newOrders: Boolean = true,
    val orderReady: Boolean = true,
    val deliveryUpdates: Boolean = true,
    val printerErrors: Boolean = true,
    val paymentFailures: Boolean = true
)