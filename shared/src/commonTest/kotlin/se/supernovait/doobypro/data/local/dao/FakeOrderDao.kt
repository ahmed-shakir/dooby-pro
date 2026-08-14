package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.doobypro.data.local.entity.OrderEntity

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

    override suspend fun upsert(order: OrderEntity) {
        ordersState.value += (order.id to order)
    }

    override suspend fun delete(order: OrderEntity) {
        ordersState.value -= order.id
    }
}
