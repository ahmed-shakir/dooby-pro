package se.supernovait.doobypro.domain.model.order

import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.storage.StorageLocation

/**
 * Domain model representing a customer order for laundry services.
 *
 * This model contains the full details of the customer, the service requested,
 * scheduling, and internal operational details like storage placement.
 *
 * @property id The unique identifier for the order. Null for unsaved orders.
 * @property customer The [User] who placed the order.
 * @property service The [Service] being ordered.
 * @property storageLocation The assigned [StorageLocation] area.
 * @property status The current lifecycle stage of the order.
 * @property orderDatetime The date and time the order was placed.
 * @property deliveryDatetime The scheduled completion or delivery date and time.
 * @property deliveryOption The chosen delivery priority (e.g., Express, Standard).
 * @property deliveryMethod The chosen delivery method (e.g., Home Delivery, Pickup).
 * @property isPaymentDone Whether the order has been paid for.
 * @property notes Optional internal or customer-provided notes.
 */
data class Order(
    val id: String? = null,
    val customer: User,
    val service: Service,
    val storageLocation: StorageLocation,
    val status: OrderStatus,
    val orderDatetime: LocalDateTime,
    val deliveryDatetime: LocalDateTime,
    val deliveryOption: DeliveryOption,
    val deliveryMethod: DeliveryMethod,
    val isPaymentDone: Boolean,
    val notes: String?
) {
    /**
     * Determines the next logical status in the workflow based on the current state.
     *
     * @return The next [OrderStatus], or null if the order is already in a terminal state.
     */
    fun getNextStatus(): OrderStatus? = status.next(deliveryMethod)

    /**
     * Checks if the order is eligible for cancellation.
     */
    fun canCancel(): Boolean = !status.isTerminal()

    /**
     * Checks if the order can be permanently deleted from the active list.
     */
    fun canDelete(): Boolean = status.canBeDeleted()
}
