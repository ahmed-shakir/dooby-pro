package se.supernovait.doobypro.domain.model.order

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.location.Address
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.decimal.Percentage
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.CollectionMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrderTest {
    private val testUser = User(
        id = "user_123",
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    private val testService = Service(
        id = "service_123",
        title = "Test Service",
        description = "Description",
        price = Amount(1000, "AED")
    )

    private val testStorage = StorageLocation(
        id = "default",
        label = "Uncategorized",
        isDefault = true
    )

    private val testDateTime = LocalDateTime(2026, 8, 21, 20, 0, 0)

    private fun createOrder(
        status: OrderStatus = OrderStatus.NEW,
        deliveryMethod: DeliveryMethod = DeliveryMethod.IN_STORE_PICKUP
    ) = Order(
        id = "order_123",
        customer = testUser,
        items = listOf(OrderServiceItem(testService, 1)),
        storageLocation = testStorage,
        status = status,
        orderDatetime = testDateTime,
        deliveryDatetime = testDateTime,
        deliveryOption = DeliveryOption.STANDARD,
        deliveryMethod = deliveryMethod,
        isPaymentDone = false,
        notes = null,
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    @Test
    fun `lifecycle - transition from NEW to IN_PROGRESS for in-store drop off orders`() {
        val order = createOrder(status = OrderStatus.NEW).copy(collectionMethod = CollectionMethod.IN_STORE_DROP_OFF)
        assertEquals(OrderStatus.IN_PROGRESS, order.getNextStatus())
    }

    @Test
    fun `lifecycle - transition from NEW to OUT_FOR_PICKUP for home pickup orders`() {
        val order = createOrder(status = OrderStatus.NEW).copy(collectionMethod = CollectionMethod.HOME_PICKUP)
        assertEquals(OrderStatus.OUT_FOR_PICKUP, order.getNextStatus())
    }

    @Test
    fun `lifecycle - transition from OUT_FOR_PICKUP to IN_PROGRESS`() {
        val order = createOrder(status = OrderStatus.OUT_FOR_PICKUP)
        assertEquals(OrderStatus.IN_PROGRESS, order.getNextStatus())
    }

    @Test
    fun `lifecycle - transition from IN_PROGRESS to READY`() {
        val order = createOrder(status = OrderStatus.IN_PROGRESS)
        assertEquals(OrderStatus.READY, order.getNextStatus())
    }

    @Test
    fun `lifecycle - transition from READY to PICKED_UP for pickup orders`() {
        val order = createOrder(
            status = OrderStatus.READY,
            deliveryMethod = DeliveryMethod.IN_STORE_PICKUP
        )
        assertEquals(OrderStatus.PICKED_UP, order.getNextStatus())
    }

    @Test
    fun `lifecycle - transition from READY to OUT_FOR_DELIVERY for home delivery orders`() {
        val order = createOrder(
            status = OrderStatus.READY,
            deliveryMethod = DeliveryMethod.HOME_DELIVERY
        )
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getNextStatus())
    }

    @Test
    fun `lifecycle - transition from OUT_FOR_DELIVERY to DELIVERED`() {
        val order = createOrder(status = OrderStatus.OUT_FOR_DELIVERY)
        assertEquals(OrderStatus.DELIVERED, order.getNextStatus())
    }

    @Test
    fun `lifecycle - terminal statuses return null for next status`() {
        assertNull(createOrder(status = OrderStatus.PICKED_UP).getNextStatus())
        assertNull(createOrder(status = OrderStatus.DELIVERED).getNextStatus())
        assertNull(createOrder(status = OrderStatus.CANCELLED).getNextStatus())
    }

    @Test
    fun `cancellation - can cancel non-terminal orders`() {
        assertTrue(createOrder(status = OrderStatus.NEW).canCancel())
        assertTrue(createOrder(status = OrderStatus.IN_PROGRESS).canCancel())
        assertTrue(createOrder(status = OrderStatus.READY).canCancel())
        assertTrue(createOrder(status = OrderStatus.OUT_FOR_DELIVERY).canCancel())
    }

    @Test
    fun `cancellation - cannot cancel terminal orders`() {
        assertFalse(createOrder(status = OrderStatus.PICKED_UP).canCancel())
        assertFalse(createOrder(status = OrderStatus.DELIVERED).canCancel())
        assertFalse(createOrder(status = OrderStatus.CANCELLED).canCancel())
    }

    @Test
    fun `deletion - only NEW orders can be deleted`() {
        assertTrue(createOrder(status = OrderStatus.NEW).canDelete())
        assertFalse(createOrder(status = OrderStatus.IN_PROGRESS).canDelete())
        assertFalse(createOrder(status = OrderStatus.READY).canDelete())
        assertFalse(createOrder(status = OrderStatus.PICKED_UP).canDelete())
        assertFalse(createOrder(status = OrderStatus.DELIVERED).canDelete())
        assertFalse(createOrder(status = OrderStatus.CANCELLED).canDelete())
    }

    @Test
    fun `isLate - NEW IN_PROGRESS or HOME_DELIVERY READY and OUT_FOR_DELIVERY orders past delivery date are late`() {
        val pastDateTime = LocalDateTime(2000, 1, 1, 0, 0, 0)
        val futureDateTime = LocalDateTime(2099, 1, 1, 0, 0, 0)

        val newPastOrder = createOrder(status = OrderStatus.NEW).copy(deliveryDatetime = pastDateTime)
        val inProgressPastOrder = createOrder(status = OrderStatus.IN_PROGRESS).copy(deliveryDatetime = pastDateTime)
        val readyPickupPastOrder = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = pastDateTime)
        val readyHomePastOrder = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = pastDateTime)
        val outForDeliveryHomePastOrder = createOrder(status = OrderStatus.OUT_FOR_DELIVERY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = pastDateTime)
        val newFutureOrder = createOrder(status = OrderStatus.NEW).copy(deliveryDatetime = futureDateTime)

        assertTrue(newPastOrder.isLate())
        assertTrue(inProgressPastOrder.isLate())
        assertFalse(readyPickupPastOrder.isLate())
        assertTrue(readyHomePastOrder.isLate())
        assertTrue(outForDeliveryHomePastOrder.isLate())
        assertFalse(newFutureOrder.isLate())
    }

    @Test
    fun `isLate - returns false when delivery date is less than 24h ago and true when more than 24h ago`() {
        val nowInstant = LocalDateTime.now().toInstant(TimeZone.currentSystemDefault())
        val systemTimeZone = TimeZone.currentSystemDefault()

        val delivery12HoursAgo = nowInstant.minus(12, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val delivery23HoursAgo = nowInstant.minus(23, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val delivery25HoursAgo = nowInstant.minus(25, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)

        val recentOrder12h = createOrder(status = OrderStatus.NEW).copy(deliveryDatetime = delivery12HoursAgo)
        val recentOrder23h = createOrder(status = OrderStatus.NEW).copy(deliveryDatetime = delivery23HoursAgo)
        val lateOrder25h = createOrder(status = OrderStatus.NEW).copy(deliveryDatetime = delivery25HoursAgo)

        assertFalse(recentOrder12h.isLate())
        assertFalse(recentOrder23h.isLate())
        assertTrue(lateOrder25h.isLate())
    }

    @Test
    fun `isNotPickedUp - only IN_STORE_PICKUP orders in READY status past delivery date and threshold`() {
        val nowInstant = LocalDateTime.now().toInstant(TimeZone.currentSystemDefault())
        val systemTimeZone = TimeZone.currentSystemDefault()

        val delivery12HoursAgo = nowInstant.minus(12, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val delivery25HoursAgo = nowInstant.minus(25, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val futureDateTime = LocalDateTime(2099, 1, 1, 0, 0, 0)

        val readyPickup12h = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = delivery12HoursAgo)
        val readyPickup25h = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = delivery25HoursAgo)
        val readyPickupFuture = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = futureDateTime)
        val readyDelivery25h = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = delivery25HoursAgo)
        val newPickup25h = createOrder(status = OrderStatus.NEW, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = delivery25HoursAgo)

        assertFalse(readyPickup12h.isNotPickedUp())
        assertTrue(readyPickup25h.isNotPickedUp())
        assertFalse(readyPickupFuture.isNotPickedUp())
        assertFalse(readyDelivery25h.isNotPickedUp())
        assertFalse(newPickup25h.isNotPickedUp())
    }

    @Test
    fun `isNotDelivered - only HOME_DELIVERY orders in READY status past delivery date and threshold`() {
        val nowInstant = LocalDateTime.now().toInstant(TimeZone.currentSystemDefault())
        val systemTimeZone = TimeZone.currentSystemDefault()

        val delivery12HoursAgo = nowInstant.minus(12, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val delivery25HoursAgo = nowInstant.minus(25, DateTimeUnit.HOUR).toLocalDateTime(systemTimeZone)
        val futureDateTime = LocalDateTime(2099, 1, 1, 0, 0, 0)

        val readyHome12h = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = delivery12HoursAgo)
        val readyHome25h = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = delivery25HoursAgo)
        val readyHomeFuture = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = futureDateTime)
        val readyPickup25h = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = delivery25HoursAgo)
        val deliveredHome25h = createOrder(status = OrderStatus.DELIVERED, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = delivery25HoursAgo)

        assertFalse(readyHome12h.isNotDelivered())
        assertTrue(readyHome25h.isNotDelivered())
        assertFalse(readyHomeFuture.isNotDelivered())
        assertFalse(readyPickup25h.isNotDelivered())
        assertFalse(deliveredHome25h.isNotDelivered())
    }

    @Test
    fun `hasRegisteredAddress - returns false when address is null and true when address is present`() {
        val userNoAddress = testUser.copy(address = null)
        assertFalse(userNoAddress.hasRegisteredAddress)

        val userValidAddress = testUser.copy(
            address = Address(
                street = "123 Main St",
                city = "Dubai",
                country = "UAE"
            )
        )
        assertTrue(userValidAddress.hasRegisteredAddress)
    }

    @Test
    fun `totalPrice includes subunitRounding correctly`() {
        val order = createOrder().copy(
            subunitRounding = Amount(27, "AED")
        )
        assertEquals(Amount(1027, "AED"), order.totalPrice)
    }

    @Test
    fun `totalPrice includes negative and positive subunitRounding correctly`() {
        val orderPositive = createOrder().copy(subunitRounding = Amount(12, "AED"))
        assertEquals(Amount(1012, "AED"), orderPositive.totalPrice)

        val orderNegative = createOrder().copy(subunitRounding = Amount(-12, "AED"))
        assertEquals(Amount(988, "AED"), orderNegative.totalPrice)
    }

    @Test
    fun `totalPrice applies flat discount correctly`() {
        val order = createOrder().copy(
            discountType = DiscountType.FLAT_AMOUNT,
            discount = Amount(200, "AED")
        )
        assertEquals(Amount(800, "AED"), order.totalPrice)
    }

    @Test
    fun `totalPrice applies percentage discount correctly`() {
        val order = createOrder().copy(
            discountType = DiscountType.PERCENTAGE,
            discountPercentage = Percentage.fromPercent(10.0),
            discount = Amount(100, "AED")
        )
        assertEquals(Amount(900, "AED"), order.totalPrice)
    }

    @Test
    fun `totalPrice applies discount before subunit rounding`() {
        val order = createOrder().copy(
            discountType = DiscountType.FLAT_AMOUNT,
            discount = Amount(200, "AED"),
            subunitRounding = Amount(50, "AED")
        )
        // 1000 items - 200 discount + 50 rounding = 850
        assertEquals(Amount(850, "AED"), order.totalPrice)
    }

    @Test
    fun `totalPrice applies tax correctly`() {
        val order = createOrder().copy(
            taxPercentage = Percentage.fromPercent(10.0),
            tax = Amount(100, "AED")
        )
        // 1000 items + 100 tax = 1100
        assertEquals(Amount(1100, "AED"), order.totalPrice)
    }
}
