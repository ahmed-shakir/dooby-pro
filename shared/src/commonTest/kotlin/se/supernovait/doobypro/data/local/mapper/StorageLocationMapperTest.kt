package se.supernovait.doobypro.data.local.mapper

import se.supernovait.doobypro.data.local.entity.StorageLocationEntity
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageLocationMapperTest {
    private val testEntity = StorageLocationEntity(
        id = "loc_1",
        label = "Shelf A",
        type = StorageType.SHELF,
        capacity = 10,
        occupiedSlots = 2,
        isDefault = false,
        isActive = true
    )

    private val testDomain = StorageLocation(
        id = "loc_1",
        label = "Shelf A",
        type = StorageType.SHELF,
        capacity = 10,
        occupiedSlots = 2,
        isDefault = false,
        isActive = true
    )

    @Test
    fun `toDomain should correctly map entity to domain model`() {
        val result = testEntity.toDomain()
        assertEquals(testDomain, result)
    }

    @Test
    fun `toEntity should correctly map domain model to entity`() {
        val result = testDomain.toEntity()
        assertEquals(testEntity, result)
    }
}
