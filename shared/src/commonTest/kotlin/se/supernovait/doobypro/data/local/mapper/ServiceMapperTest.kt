package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.data.local.entity.ServiceEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.Service
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for service and amount mappers.
 *
 * Ensures correct data transformation between local entities and domain models.
 */
class ServiceMapperTest {
    private val serviceId = SupernovaIdGenerator.generateId(IdType.SERVICE.prefix)

    private val testAmountEntity = AmountEntity(
        raw = 1000,
        currency = "AED"
    )

    private val testAmount = Amount(
        raw = 1000,
        currency = "AED"
    )

    private val testServiceEntity = ServiceEntity(
        id = serviceId,
        title = "Test Service",
        description = "This is a test service description",
        price = testAmountEntity
    )

    private val testService = Service(
        id = serviceId,
        title = "Test Service",
        description = "This is a test service description",
        price = testAmount
    )

    @Test
    fun `toDomain should correctly transform ServiceEntity to Service model`() {
        val result = testServiceEntity.toDomain()

        assertEquals(testService.id, result.id)
        assertEquals(testService.title, result.title)
        assertEquals(testService.description, result.description)
        assertEquals(testService.price.raw, result.price.raw)
        assertEquals(testService.price.currency, result.price.currency)
    }

    @Test
    fun `toEntity should correctly transform Service model to ServiceEntity`() {
        val result = testService.toEntity()

        assertEquals(testServiceEntity.id, result.id)
        assertEquals(testServiceEntity.title, result.title)
        assertEquals(testServiceEntity.description, result.description)
        assertEquals(testServiceEntity.price.raw, result.price.raw)
        assertEquals(testServiceEntity.price.currency, result.price.currency)
    }

    @Test
    fun `AmountEntity_toDomain should correctly transform AmountEntity to Amount model`() {
        val result = testAmountEntity.toDomain()

        assertEquals(testAmount.raw, result.raw)
        assertEquals(testAmount.currency, result.currency)
        assertEquals("10.00", result.value)
    }

    @Test
    fun `Amount_toEntity should correctly transform Amount model to AmountEntity`() {
        val result = testAmount.toEntity()

        assertEquals(testAmountEntity.raw, result.raw)
        assertEquals(testAmountEntity.currency, result.currency)
    }
}
