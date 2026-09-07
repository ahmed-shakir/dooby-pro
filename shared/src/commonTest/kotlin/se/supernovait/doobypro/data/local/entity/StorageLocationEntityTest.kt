package se.supernovait.doobypro.data.local.entity

import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.storage.StorageType
import kotlin.test.Test
import kotlin.test.assertTrue

class StorageLocationEntityTest {
    @Test
    fun `StorageLocationEntity should generate a valid ID with correct prefix by default`() {
        val entity = StorageLocationEntity(
            label = "Rack 1",
            type = StorageType.HANGER,
            capacity = 5
        )

        assertTrue(entity.id.startsWith(IdType.STORAGE_LOCATION.prefix), "ID should start with ${IdType.STORAGE_LOCATION.prefix}")
        assertTrue(entity.id.length > IdType.STORAGE_LOCATION.prefix.length, "ID should have more characters after prefix")
    }
}
