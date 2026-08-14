package se.supernovait.doobypro.data.local.entity

import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.doobypro.domain.model.DoobyIdType
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for [ServiceEntity].
 */
class ServiceEntityTest {

    @Test
    fun `ServiceEntity should generate a valid ID with correct prefix by default`() {
        val entity = ServiceEntity(
            title = "Test",
            description = "Test",
            price = AmountEntity(100, "AED")
        )

        assertTrue(entity.id.startsWith(DoobyIdType.SERVICE.prefix), "ID should start with ${DoobyIdType.SERVICE.prefix}")
        assertTrue(entity.id.length > DoobyIdType.SERVICE.prefix.length, "ID should have more characters after prefix")
    }
}
