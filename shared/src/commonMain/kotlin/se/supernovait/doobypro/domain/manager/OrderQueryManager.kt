package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.domain.repository.OrderRepository

/**
 * Manager responsible for read-only order queries and UI-specific data aggregation.
 */
class OrderQueryManager(
    private val orderRepository: OrderRepository
) {
    /**
     * Observes orders filtered by an operational tab.
     */
    fun getOrdersForTab(tab: OrderTab): Flow<List<Order>> {
        return orderRepository.getOrders().map { orders ->
            when (tab) {
                OrderTab.NEW -> orders.filter { it.status == OrderStatus.NEW }
                OrderTab.IN_PROGRESS -> orders.filter { it.status == OrderStatus.IN_PROGRESS }
                OrderTab.READY -> orders.filter { it.status == OrderStatus.READY || it.status == OrderStatus.OUT_FOR_DELIVERY }
                OrderTab.COMPLETED -> orders.filter { it.status == OrderStatus.PICKED_UP || it.status == OrderStatus.DELIVERED }
            }
        }
    }

    /**
     * Observes only cancelled orders.
     */
    fun getCancelledOrders(): Flow<List<Order>> {
        return orderRepository.getOrders().map { orders ->
            orders.filter { it.status == OrderStatus.CANCELLED }
        }
    }

    /**
     * Calculates the count of late or overdue orders in each operational tab.
     * An order is considered late/overdue if its delivery deadline has passed.
     */
    fun getLateOrderCountPerTab(): Flow<Map<OrderTab, Int>> {
        return orderRepository.getOrders().map { orders ->
            OrderTab.entries.associateWith { tab ->
                orders.filter { it.isLate() || it.isNotPickedUp() || it.isNotDelivered() }.count { order ->
                    when (tab) {
                        OrderTab.NEW -> order.status == OrderStatus.NEW
                        OrderTab.IN_PROGRESS -> order.status == OrderStatus.IN_PROGRESS
                        OrderTab.READY -> order.status == OrderStatus.READY || order.status == OrderStatus.OUT_FOR_DELIVERY
                        OrderTab.COMPLETED -> false
                    }
                }
            }
        }
    }
}
