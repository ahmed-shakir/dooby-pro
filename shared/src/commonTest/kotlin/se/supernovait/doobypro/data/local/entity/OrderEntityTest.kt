package se.supernovait.doobypro.data.local.entity

import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.OrderStatus
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for [OrderEntity].
 */
class OrderEntityTest {

    @Test
    fun `OrderEntity should generate a valid ID with correct prefix by default`() {
        val now = LocalDateTime(2026, 8, 15, 0, 0, 0)
        val entity = OrderEntity(
            customerId = SupernovaIdGenerator.generateId(IdType.USER.prefix),
            serviceId = SupernovaIdGenerator.generateId(IdType.SERVICE.prefix),
            storageLocationId = "default",
            status = OrderStatus.NEW,
            orderDatetime = now,
            deliveryDatetime = now,
            deliveryOption = DeliveryOption.STANDARD,
            deliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
            isPaymentDone = true,
            notes = ""
        )

        assertTrue(entity.id.startsWith(IdType.ORDER.prefix), "ID should start with ${IdType.ORDER.prefix}")
        assertTrue(entity.id.length > IdType.ORDER.prefix.length, "ID should have more characters after prefix")
    }
}
