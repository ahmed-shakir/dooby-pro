package se.supernovait.doobypro.domain.model.order

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.domain.model.Service
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
        service = testService,
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
    fun `lifecycle - transition from NEW to IN_PROGRESS`() {
        val order = createOrder(status = OrderStatus.NEW)
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
    fun `isLate - NEW, IN_PROGRESS or HOME_DELIVERY READY and OUT_FOR_DELIVERY orders past delivery date are late`() {
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
    fun `isNotPickedUp - only IN_STORE_PICKUP orders in READY status past delivery date`() {
        val pastDateTime = LocalDateTime(2000, 1, 1, 0, 0, 0)
        val futureDateTime = LocalDateTime(2099, 1, 1, 0, 0, 0)

        val readyPickupPast = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = pastDateTime)
        val readyPickupFuture = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = futureDateTime)
        val readyDeliveryPast = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = pastDateTime)
        val newPickupPast = createOrder(status = OrderStatus.NEW, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = pastDateTime)

        assertTrue(readyPickupPast.isNotPickedUp())
        assertFalse(readyPickupFuture.isNotPickedUp())
        assertFalse(readyDeliveryPast.isNotPickedUp())
        assertFalse(newPickupPast.isNotPickedUp())
    }

    @Test
    fun `isNotDelivered - only HOME_DELIVERY orders in READY status past delivery date`() {
        val pastDateTime = LocalDateTime(2000, 1, 1, 0, 0, 0)
        val futureDateTime = LocalDateTime(2099, 1, 1, 0, 0, 0)

        val readyHomePast = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = pastDateTime)
        val readyHomeFuture = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = futureDateTime)
        val readyPickupPast = createOrder(status = OrderStatus.READY, deliveryMethod = DeliveryMethod.IN_STORE_PICKUP).copy(deliveryDatetime = pastDateTime)
        val deliveredHomePast = createOrder(status = OrderStatus.DELIVERED, deliveryMethod = DeliveryMethod.HOME_DELIVERY).copy(deliveryDatetime = pastDateTime)

        assertTrue(readyHomePast.isNotDelivered())
        assertFalse(readyHomeFuture.isNotDelivered())
        assertFalse(readyPickupPast.isNotDelivered())
        assertFalse(deliveredHomePast.isNotDelivered())
    }
}
