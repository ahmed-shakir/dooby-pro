package se.supernovait.doobypro.presentation.order

import se.supernovait.app.core.domain.auth.User
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.order.OrderTab

/**
 * Events for the order management domain.
 */
sealed interface OrderEvent {
    /** Re-fetches the list of all orders. */
    data object LoadOrders : OrderEvent

    /**
     * Initializes a new order process (starts with customer search).
     */
    data object CreateNewOrder : OrderEvent

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

    /**
     * Selects an operational tab.
     */
    data class SelectTab(val tab: OrderTab) : OrderEvent

    /**
     * Updates the search query for orders.
     */
    data class SearchOrders(val query: String) : OrderEvent

    /**
     * Updates the search query for customers when creating a new order.
     */
    data class SearchCustomers(val query: String) : OrderEvent

    /**
     * Selects a customer for a new order.
     */
    data class SelectCustomer(val customer: User) : OrderEvent

    /**
     * Starts the process of adding a new customer.
     */
    data object StartAddingCustomer : OrderEvent

    /**
     * Saves a new customer record.
     */
    data class SaveNewCustomer(val customer: User) : OrderEvent
}
