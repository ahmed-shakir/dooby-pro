package se.supernovait.doobypro.domain.model.order

import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod

/**
 * Represents the lifecycle stages of an order.
 *
 * Common Workflow:
 * [NEW] -> [IN_PROGRESS] or [CANCELLED]
 * [IN_PROGRESS] -> [READY] or [CANCELLED]
 *
 * [DeliveryMethod.IN_STORE_PICKUP] specific:
 * [READY] -> [PICKED_UP] or [CANCELLED]
 *
 * [DeliveryMethod.HOME_DELIVERY] specific:
 * [READY] -> [OUT_FOR_DELIVERY] or [CANCELLED]
 * [OUT_FOR_DELIVERY] -> [DELIVERED] or [CANCELLED]
 *
 * Terminal statuses: [PICKED_UP], [DELIVERED], [CANCELLED]
 */
enum class OrderStatus {
    /**
     * Order received and waiting to be processed.
     * This is the only state where an order can be deleted.
     */
    NEW,

    /**
     * Laundry is being processed (washing, drying, ironing, etc.).
     */
    IN_PROGRESS,

    /**
     * Laundry is ready for pickup or out for delivery.
     */
    READY,

    /**
     * Laundry is with the delivery driver and on its way to the customer.
     * Specific to [DeliveryMethod.HOME_DELIVERY].
     */
    OUT_FOR_DELIVERY,

    /**
     * Customer has picked up their laundry from the store.
     * Terminal state for Pickup orders.
     */
    PICKED_UP,

    /**
     * Laundry has been delivered to the customer's address.
     * Terminal state for Home Delivery orders.
     */
    DELIVERED,

    /**
     * Order was cancelled.
     * Terminal state.
     */
    CANCELLED;

    /**
     * Determines the next logical status in the workflow based on the delivery method.
     *
     * @param deliveryMethod The method chosen for order fulfillment.
     * @return The next [OrderStatus], or null if already in a terminal state.
     */
    fun next(deliveryMethod: DeliveryMethod): OrderStatus? = when (this) {
        NEW -> IN_PROGRESS
        IN_PROGRESS -> READY
        READY -> {
            if (deliveryMethod == DeliveryMethod.IN_STORE_PICKUP) {
                PICKED_UP
            } else {
                OUT_FOR_DELIVERY
            }
        }
        OUT_FOR_DELIVERY -> DELIVERED
        else -> null
    }

    /**
     * Returns whether the order can be deleted in this status.
     */
    fun canBeDeleted(): Boolean = this == NEW

    /**
     * Returns whether this is a terminal (final) status.
     */
    fun isTerminal(): Boolean = this in listOf(PICKED_UP, DELIVERED, CANCELLED)
}
