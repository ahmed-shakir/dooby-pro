package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for order mappers.
 */
class OrderMapperTest {
    private val orderId = SupernovaIdGenerator.generateId(IdType.ORDER.prefix)
    private val userId = SupernovaIdGenerator.generateId(IdType.USER.prefix)
    private val serviceId = SupernovaIdGenerator.generateId(IdType.SERVICE.prefix)

    private val testDateTime = LocalDateTime(2026, 8, 14, 20, 0, 0)
    private val testInstant = testDateTime.toInstant(TimeZone.currentSystemDefault())
    
    private val testUser = User(
        id = userId,
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    private val testService = Service(
        id = serviceId,
        title = "Test Service",
        description = "Description",
        price = Amount(1000, "AED")
    )

    private val testStorage = StorageLocation(
        id = "Shelf B",
        label = "Shelf B"
    )

    private val testOrderEntity = OrderEntity(
        id = orderId,
        customerId = userId,
        serviceId = serviceId,
        storageLocationId = "Shelf B",
        status = OrderStatus.NEW,
        orderDatetime = testDateTime,
        deliveryDatetime = testDateTime,
        deliveryOption = DeliveryOption.EXPRESS,
        deliveryMethod = DeliveryMethod.HOME_DELIVERY,
        isPaymentDone = true,
        notes = "Handle with care",
        createdAt = testInstant,
        updatedAt = testInstant
    )

    private val testOrder = Order(
        id = orderId,
        customer = testUser,
        service = testService,
        storageLocation = testStorage,
        status = OrderStatus.NEW,
        orderDatetime = testDateTime,
        deliveryDatetime = testDateTime,
        deliveryOption = DeliveryOption.EXPRESS,
        deliveryMethod = DeliveryMethod.HOME_DELIVERY,
        isPaymentDone = true,
        notes = "Handle with care",
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    @Test
    fun `toDomain should correctly transform OrderEntity to Order model`() {
        val result = testOrderEntity.toDomain(testUser, testService, testStorage)

        assertEquals(testOrder.id, result.id)
        assertEquals(testOrder.customer.id, result.customer.id)
        assertEquals(testOrder.service.id, result.service.id)
        assertEquals(testOrder.storageLocation?.id, result.storageLocation?.id)
        assertEquals(testOrder.status, result.status)
        assertEquals(testOrder.orderDatetime, result.orderDatetime)
        assertEquals(testOrder.deliveryDatetime, result.deliveryDatetime)
        assertEquals(testOrder.deliveryOption, result.deliveryOption)
        assertEquals(testOrder.deliveryMethod, result.deliveryMethod)
        assertEquals(testOrder.isPaymentDone, result.isPaymentDone)
        assertEquals(testOrder.notes, result.notes)
        assertEquals(testOrder.createdAt, result.createdAt)
        assertEquals(testOrder.updatedAt, result.updatedAt)
    }

    @Test
    fun `toDomain with OUT_FOR_DELIVERY status`() {
        val entity = testOrderEntity.copy(status = OrderStatus.OUT_FOR_DELIVERY)
        val result = entity.toDomain(testUser, testService, testStorage)
        assertEquals(OrderStatus.OUT_FOR_DELIVERY, result.status)
    }

    @Test
    fun `toEntity should correctly transform Order model to OrderEntity`() {
        val result = testOrder.toEntity()

        assertEquals(testOrderEntity.copy(updatedAt = result.updatedAt), result)
    }
}
