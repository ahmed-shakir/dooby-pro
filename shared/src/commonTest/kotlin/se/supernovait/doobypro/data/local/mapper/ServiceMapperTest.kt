package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.ServiceEntity
import se.supernovait.doobypro.data.local.entity.embedded.AmountEntity
import se.supernovait.doobypro.domain.model.Amount
import se.supernovait.doobypro.domain.model.DoobyIdType
import se.supernovait.doobypro.domain.model.Service
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for service and amount mappers.
 *
 * Ensures correct data transformation between local entities and domain models.
 */
class ServiceMapperTest {
    private val serviceId = SupernovaIdGenerator.generateId(DoobyIdType.SERVICE.prefix)

    private val testAmountEntity = AmountEntity(
        value = 100.0,
        currency = "AED"
    )

    private val testAmount = Amount(
        value = 100.0,
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
    fun `mapToModel should correctly transform ServiceEntity to Service model`() {
        val result = testServiceEntity.mapToModel()

        assertEquals(testService.id, result.id)
        assertEquals(testService.title, result.title)
        assertEquals(testService.description, result.description)
        assertEquals(testService.price.value, result.price.value)
        assertEquals(testService.price.currency, result.price.currency)
    }

    @Test
    fun `mapToEntity should correctly transform Service model to ServiceEntity`() {
        val result = testService.mapToEntity()

        assertEquals(testServiceEntity.id, result.id)
        assertEquals(testServiceEntity.title, result.title)
        assertEquals(testServiceEntity.description, result.description)
        assertEquals(testServiceEntity.price.value, result.price.value)
        assertEquals(testServiceEntity.price.currency, result.price.currency)
    }

    @Test
    fun `AmountEntity_mapToModel should correctly transform AmountEntity to Amount model`() {
        val result = testAmountEntity.mapToModel()

        assertEquals(testAmount.value, result.value)
        assertEquals(testAmount.currency, result.currency)
    }

    @Test
    fun `Amount_mapToEntity should correctly transform Amount model to AmountEntity`() {
        val result = testAmount.mapToEntity()

        assertEquals(testAmountEntity.value, result.value)
        assertEquals(testAmountEntity.currency, result.currency)
    }
}
