package se.supernovait.doobypro.data.local.entity

import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.DoobyIdType
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
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
            customerId = SupernovaIdGenerator.generateId(DoobyIdType.USER.prefix),
            serviceId = SupernovaIdGenerator.generateId(DoobyIdType.SERVICE.prefix),
            deliveryOption = DeliveryOption.STANDARD,
            deliveryMethod = DeliveryMethod.PICKUP,
            orderDate = now,
            deliveryDate = now,
            note = ""
        )

        assertTrue(entity.id.startsWith(DoobyIdType.ORDER.prefix), "ID should start with ${DoobyIdType.ORDER.prefix}")
        assertTrue(entity.id.length > DoobyIdType.ORDER.prefix.length, "ID should have more characters after prefix")
    }
}
