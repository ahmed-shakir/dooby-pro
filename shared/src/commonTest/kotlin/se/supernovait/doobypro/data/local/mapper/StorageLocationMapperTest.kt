package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import se.supernovait.doobypro.data.local.entity.StorageLocationEntity
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageLocationMapperTest {
    private val testDateTime = LocalDateTime(2026, 9, 12, 18, 35, 34)
    private val testInstant = testDateTime.toInstant(TimeZone.currentSystemDefault())

    private val testEntity = StorageLocationEntity(
        id = "loc_1",
        label = "Shelf A",
        type = StorageType.SHELF,
        capacity = 10,
        occupiedSlots = 2,
        isDefault = false,
        isActive = true,
        createdAt = testInstant,
        updatedAt = testInstant
    )

    private val testDomain = StorageLocation(
        id = "loc_1",
        label = "Shelf A",
        type = StorageType.SHELF,
        capacity = 10,
        occupiedSlots = 2,
        isDefault = false,
        isActive = true,
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    @Test
    fun `toDomain should correctly map entity to domain model`() {
        val result = testEntity.toDomain()
        assertEquals(testDomain, result)
    }

    @Test
    fun `toEntity should correctly map domain model to entity`() {
        val result = testDomain.toEntity()
        assertEquals(testEntity.copy(updatedAt = result.updatedAt), result)
    }
}
