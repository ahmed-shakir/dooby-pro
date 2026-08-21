package se.supernovait.doobypro.domain.model

import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption

/**
 * Domain model representing an order.
 *
 * This model is used throughout the business logic and UI layers.
 *
 * @property id The unique identifier for the order. Null for unsaved orders.
 * @property customer The [User] who placed the order.
 * @property service The [Service] being ordered.
 * @property deliveryOption The chosen delivery priority.
 * @property deliveryMethod The chosen delivery method.
 * @property orderDate The date and time the order was placed.
 * @property deliveryDate The scheduled delivery date and time.
 * @property isPaymentDone Whether the order is paid or not
 * @property notes Optional notes from the customer.
 */
data class Order(
    val id: String? = null,
    val customer: User,
    val service: Service,
    val deliveryOption: DeliveryOption,
    val deliveryMethod: DeliveryMethod,
    val orderDate: LocalDateTime,
    val deliveryDate: LocalDateTime,
    val isPaymentDone: Boolean,
    val notes: String?
)
