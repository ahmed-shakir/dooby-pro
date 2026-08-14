package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.Order
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
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

    private val testOrderEntity = OrderEntity(
        id = orderId,
        customerId = userId,
        serviceId = serviceId,
        deliveryOption = DeliveryOption.EXPRESS,
        deliveryMethod = DeliveryMethod.HOME_DELIVERY,
        orderDate = testDateTime,
        deliveryDate = testDateTime,
        note = "Handle with care"
    )

    private val testOrder = Order(
        id = orderId,
        customer = testUser,
        service = testService,
        deliveryOption = DeliveryOption.EXPRESS,
        deliveryMethod = DeliveryMethod.HOME_DELIVERY,
        orderDate = testDateTime,
        deliveryDate = testDateTime,
        note = "Handle with care"
    )

    @Test
    fun `toDomain should correctly transform OrderEntity to Order model`() {
        val result = testOrderEntity.toDomain(testUser, testService)

        assertEquals(testOrder.id, result.id)
        assertEquals(testOrder.customer.id, result.customer.id)
        assertEquals(testOrder.service.id, result.service.id)
        assertEquals(testOrder.deliveryOption, result.deliveryOption)
        assertEquals(testOrder.deliveryMethod, result.deliveryMethod)
        assertEquals(testOrder.orderDate, result.orderDate)
        assertEquals(testOrder.deliveryDate, result.deliveryDate)
        assertEquals(testOrder.note, result.note)
    }

    @Test
    fun `toEntity should correctly transform Order model to OrderEntity`() {
        val result = testOrder.toEntity()

        assertEquals(testOrderEntity.id, result.id)
        assertEquals(testOrderEntity.customerId, result.customerId)
        assertEquals(testOrderEntity.serviceId, result.serviceId)
        assertEquals(testOrderEntity.deliveryOption, result.deliveryOption)
        assertEquals(testOrderEntity.deliveryMethod, result.deliveryMethod)
        assertEquals(testOrderEntity.orderDate, result.orderDate)
        assertEquals(testOrderEntity.deliveryDate, result.deliveryDate)
        assertEquals(testOrderEntity.note, result.note)
    }
}
