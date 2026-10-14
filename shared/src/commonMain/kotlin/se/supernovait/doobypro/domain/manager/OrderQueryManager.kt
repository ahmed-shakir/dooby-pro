package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.util.LogTags

/**
 * Manager responsible for read-only order queries and UI-specific data aggregation.
 */
class OrderQueryManager(
    private val orderRepository: OrderRepository,
    private val logger: Logger
) {
    /**
     * Observes orders filtered by an operational tab, sorted by operational priority:
     * Overdue → Express → FIFO (oldest orders first).
     */
    fun getOrdersForTab(tab: OrderTab): Flow<List<Order>> {
        logger.debug("Querying orders for tab: $tab", tag = LogTags.ORDER_QUERY_MANAGER)
        return orderRepository.getOrders().map { orders ->
            val tabOrders = when (tab) {
                OrderTab.NEW -> orders.filter { it.status == OrderStatus.NEW }
                OrderTab.IN_PROGRESS -> orders.filter { it.status == OrderStatus.IN_PROGRESS }
                OrderTab.READY -> orders.filter { it.status == OrderStatus.READY || it.status == OrderStatus.OUT_FOR_DELIVERY }
                OrderTab.COMPLETED -> orders.filter { it.status == OrderStatus.PICKED_UP || it.status == OrderStatus.DELIVERED }
            }
            sortOrders(tabOrders)
        }
    }

    /**
     * Observes only cancelled orders, sorted by operational priority.
     */
    fun getCancelledOrders(): Flow<List<Order>> {
        logger.debug("Querying cancelled orders", tag = LogTags.ORDER_QUERY_MANAGER)
        return orderRepository.getOrders().map { orders ->
            sortOrders(orders.filter { it.status == OrderStatus.CANCELLED })
        }
    }

    /**
     * Calculates the count of late or overdue orders in each operational tab.
     * An order is considered late/overdue if its delivery deadline has passed.
     */
    fun getLateOrderCountPerTab(): Flow<Map<OrderTab, Int>> {
        logger.debug("Calculating late order counts per tab", tag = LogTags.ORDER_QUERY_MANAGER)
        return orderRepository.getOrders().map { orders ->
            OrderTab.entries.associateWith { tab ->
                orders.filter { it.isOverdue() }.count { order ->
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

    /**
     * Sorts orders according to operational priority:
     * 1. Overdue orders first (`isOverdue()`)
     * 2. Express orders next (`deliveryOption == DeliveryOption.EXPRESS`)
     * 3. First-In, First-Out (FIFO) by creation/order time (`orderDatetime` ascending)
     */
    private fun sortOrders(orders: List<Order>): List<Order> {
        return orders.sortedWith(
            compareByDescending<Order> { it.isOverdue() }
                .thenByDescending { it.deliveryOption == DeliveryOption.EXPRESS }
                .thenBy { it.orderDatetime }
        )
    }
}
