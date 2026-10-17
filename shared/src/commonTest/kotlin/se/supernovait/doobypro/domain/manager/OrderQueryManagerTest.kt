package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.observability.logging.LogLevel
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderServiceItem
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrderQueryManagerTest {
    private val testUser = User(
        id = "user_1",
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    private val testService = Service(
        id = "s_1",
        title = "Dry Clean",
        description = "Description",
        price = Amount(100, "AED")
    )

    private val testStorage = StorageLocation(
        id = "loc_1",
        label = "Rack A"
    )

    private val fakeRepo = FakeOrderRepository()
    private val logger = FakeLogger()
    private val queryManager = OrderQueryManager(fakeRepo, logger)

    private fun createOrder(
        id: String,
        status: OrderStatus = OrderStatus.NEW,
        deliveryOption: DeliveryOption = DeliveryOption.STANDARD,
        orderDatetime: LocalDateTime = LocalDateTime(2026, 1, 10, 10, 0),
        deliveryDatetime: LocalDateTime = LocalDateTime(2099, 1, 1, 10, 0)
    ) = Order(
        id = id,
        customer = testUser,
        items = listOf(OrderServiceItem(testService, 1)),
        storageLocation = testStorage,
        status = status,
        orderDatetime = orderDatetime,
        deliveryDatetime = deliveryDatetime,
        deliveryOption = deliveryOption,
        deliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
        isPaymentDone = false
    )

    @Test
    fun `getOrdersForTab sorts orders by Overdue first then Express then FIFO`() = runTest {
        val nowInstant = LocalDateTime.now().toInstant(TimeZone.currentSystemDefault())
        val systemTimeZone = TimeZone.currentSystemDefault()
        val overduePastDate = nowInstant.minus(30, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val futureDeliveryDate = nowInstant.plus(48, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)

        // 1. Standard order placed early (Jan 1) - Standard FIFO #1
        val standardOld = createOrder(
            id = "standard_old",
            orderDatetime = LocalDateTime(2026, 1, 1, 10, 0),
            deliveryDatetime = futureDeliveryDate
        )

        // 2. Standard order placed later (Jan 5) - Standard FIFO #2
        val standardNew = createOrder(
            id = "standard_new",
            orderDatetime = LocalDateTime(2026, 1, 5, 10, 0),
            deliveryDatetime = futureDeliveryDate
        )

        // 3. Express order placed later (Jan 3) - Express priority
        val expressOrder = createOrder(
            id = "express_order",
            deliveryOption = DeliveryOption.EXPRESS,
            orderDatetime = LocalDateTime(2026, 1, 3, 10, 0),
            deliveryDatetime = futureDeliveryDate
        )

        // 4. Overdue order placed recently (Jan 4) but past delivery threshold
        val overdueOrder = createOrder(
            id = "overdue_order",
            orderDatetime = LocalDateTime(2026, 1, 4, 10, 0),
            deliveryDatetime = overduePastDate
        )

        assertTrue(overdueOrder.isOverdue(), "overdueOrder should be overdue")
        assertFalse(expressOrder.isOverdue(), "expressOrder should not be overdue")
        assertFalse(standardOld.isOverdue(), "standardOld should not be overdue")
        assertFalse(standardNew.isOverdue(), "standardNew should not be overdue")

        // Pass out of order list to repository
        fakeRepo.emit(listOf(standardNew, standardOld, expressOrder, overdueOrder))

        val result = queryManager.getOrdersForTab(OrderTab.NEW).first()

        assertEquals(4, result.size)
        // 1st: Overdue order
        assertEquals("overdue_order", result[0].id)
        // 2nd: Express order
        assertEquals("express_order", result[1].id)
        // 3rd: Oldest standard order (FIFO)
        assertEquals("standard_old", result[2].id)
        // 4th: Newer standard order (FIFO)
        assertEquals("standard_new", result[3].id)
    }

    private class FakeOrderRepository : OrderRepository {
        private val _orders = MutableStateFlow<List<Order>>(emptyList())

        fun emit(orders: List<Order>) {
            _orders.value = orders
        }

        override fun getOrders(): Flow<List<Order>> = _orders
        override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> = MutableStateFlow(emptyList())
        override suspend fun getOrderById(id: String): Result<Order, DataError> = Result.Failure(DataError.NOT_FOUND)
        override suspend fun saveOrder(order: Order): Result<String, DataError> = Result.Success("gen")
        override suspend fun deleteOrder(order: Order): Result<Unit, DataError> = Result.Success(Unit)
        override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> = Result.Success(Unit)
    }

    private class FakeLogger : Logger {
        override fun trace(message: String, throwable: Throwable?, tag: String?) {}
        override fun debug(message: String, throwable: Throwable?, tag: String?) {}
        override fun info(message: String, throwable: Throwable?, tag: String?) {}
        override fun warn(message: String, throwable: Throwable?, tag: String?) {}
        override fun error(message: String, throwable: Throwable?, tag: String?) {}
        override fun log(level: LogLevel, message: String, throwable: Throwable?, tag: String?) {}
    }
}
