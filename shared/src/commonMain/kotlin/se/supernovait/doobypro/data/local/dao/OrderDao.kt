package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.data.local.entity.OrderEntity

/**
 * Data Access Object for the "orders" table.
 *
 * Provides methods for performing CRUD operations on [OrderEntity].
 */
@Dao
interface OrderDao {

    /**
     * Observes all orders in the database.
     *
     * @return A flow emitting the list of all orders.
     */
    @Query("SELECT * FROM orders")
    fun getAll(): Flow<List<OrderEntity>>

    /**
     * Retrieves an order by its ID.
     *
     * @param id The unique identifier of the order.
     * @return The found [OrderEntity], or null if not found.
     */
    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getById(id: String): OrderEntity?

    /**
     * Observes all orders for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A flow emitting the list of orders for the customer.
     */
    @Query("SELECT * FROM orders WHERE customerId = :customerId")
    fun getByCustomerId(customerId: String): Flow<List<OrderEntity>>

    /**
     * Inserts or updates an order in the database.
     *
     * @param order The order entity to upsert.
     */
    @Upsert
    suspend fun upsert(order: OrderEntity)

    /**
     * Deletes an order from the database.
     *
     * @param order The order entity to delete.
     */
    @Delete
    suspend fun delete(order: OrderEntity)
}
