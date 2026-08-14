package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.domain.model.Order

/**
 * Repository for managing [Order] domain models.
 *
 * This repository handles the orchestration of order data, ensuring that
 * [Order] models are correctly hydrated with full customer and service objects.
 */
interface OrderRepository {

    /**
     * Observes all orders in the system.
     *
     * @return A flow emitting the list of all orders.
     */
    fun getOrders(): Flow<List<Order>>

    /**
     * Observes all orders for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A flow emitting the list of orders for the customer.
     */
    fun getOrdersByCustomerId(customerId: String): Flow<List<Order>>

    /**
     * Retrieves an order by its ID.
     *
     * @param id The unique identifier of the order.
     * @return The found [Order], or null if not found.
     */
    suspend fun getOrderById(id: String): Order?
    /**
     * Inserts or updates an order.
     *
     * @param order The domain order model to upsert.
     */
    suspend fun upsertOrder(order: Order)

    /**
     * Deletes an order.
     *
     * @param order The domain order model to delete.
     */
    suspend fun deleteOrder(order: Order)
}
