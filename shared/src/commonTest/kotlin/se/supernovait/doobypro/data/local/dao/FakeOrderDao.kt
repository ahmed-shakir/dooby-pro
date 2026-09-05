package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.domain.model.order.OrderStatus

/**
 * A fake implementation of [OrderDao] for testing purposes.
 */
class FakeOrderDao : OrderDao {
    private val ordersState = MutableStateFlow<Map<String, OrderEntity>>(emptyMap())

    override fun getAll(): Flow<List<OrderEntity>> {
        return ordersState.map { it.values.toList() }
    }

    override suspend fun getById(id: String): OrderEntity? {
        return ordersState.value[id]
    }

    override fun getByCustomerId(customerId: String): Flow<List<OrderEntity>> {
        return ordersState.map { map ->
            map.values.filter { it.customerId == customerId }
        }
    }

    override fun getByStorageLocationId(storageLocationId: String): Flow<List<OrderEntity>> {
        return ordersState.map { map ->
            map.values.filter { it.storageLocationId == storageLocationId }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        ordersState.update { map ->
            val order = map[orderId]
            if (order != null) {
                map + (orderId to order.copy(status = newStatus))
            } else {
                map
            }
        }
    }

    override suspend fun upsert(order: OrderEntity) {
        ordersState.value += (order.id to order)
    }

    override suspend fun delete(order: OrderEntity) {
        ordersState.value -= order.id
    }
}
