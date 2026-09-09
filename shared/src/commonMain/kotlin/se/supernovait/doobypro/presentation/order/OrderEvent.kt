package se.supernovait.doobypro.presentation.order

import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus

/**
 * Events for the order management domain.
 */
sealed interface OrderEvent {
    /** Re-fetches the list of all orders. */
    data object LoadOrders : OrderEvent

    /**
     * Sets the order that is currently being created or updated.
     *
     * @param order The order instance to load into the form.
     */
    data class EditOrder(val order: Order) : OrderEvent

    /**
     * Saves an order to the database.
     */
    data class SaveOrder(val order: Order) : OrderEvent

    /**
     * Permanently deletes an order.
     */
    data class DeleteOrder(val order: Order) : OrderEvent

    /**
     * Updates the status of an order.
     */
    data class UpdateStatus(val orderId: String, val newStatus: OrderStatus) : OrderEvent
}
