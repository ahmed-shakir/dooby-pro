package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.order.OrderStatus
import kotlin.time.Instant

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

    override fun getByStatus(status: OrderStatus): Flow<List<OrderEntity>> {
        return ordersState.map { map ->
            map.values.filter { it.status == status }
        }
    }

    override fun getByStatusAndMethod(status: OrderStatus, method: DeliveryMethod): Flow<List<OrderEntity>> {
        return ordersState.map { map ->
            map.values.filter { it.status == status && it.deliveryMethod == method }
        }
    }

    override fun getReadyTabOrders(): Flow<List<OrderEntity>> {
        return ordersState.map { map ->
            map.values.filter { it.status == OrderStatus.READY || it.status == OrderStatus.OUT_FOR_DELIVERY }
        }
    }

    override fun getByStorageLocationId(storageLocationId: String): Flow<List<OrderEntity>> {
        return ordersState.map { map ->
            map.values.filter { it.storageLocationId == storageLocationId }
        }
    }

    override suspend fun getInDateRange(start: LocalDateTime, end: LocalDateTime): List<OrderEntity> {
        return ordersState.value.values.filter { it.deliveryDatetime in start..end }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus, timestamp: Instant) {
        ordersState.update { map ->
            val order = map[orderId]
            if (order != null) {
                map + (orderId to order.copy(status = newStatus, updatedAt = timestamp))
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

    override fun countByStatus(status: OrderStatus): Flow<Int> {
        return ordersState.map { it.values.count { o -> o.status == status } }
    }
}
