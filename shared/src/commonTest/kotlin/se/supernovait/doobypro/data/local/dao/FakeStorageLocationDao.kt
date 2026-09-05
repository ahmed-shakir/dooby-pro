package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.doobypro.data.local.entity.StorageLocationEntity

class FakeStorageLocationDao : StorageLocationDao {
    private val locationsState = MutableStateFlow<Map<String, StorageLocationEntity>>(emptyMap())

    override fun getAllActive(): Flow<List<StorageLocationEntity>> {
        return locationsState.map { it.values.filter { loc -> loc.isActive } }
    }

    override suspend fun getById(id: String): StorageLocationEntity? {
        return locationsState.value[id]
    }

    override suspend fun getAllByIds(ids: List<String>): List<StorageLocationEntity> {
        return locationsState.value.values.filter { it.id in ids }
    }

    override suspend fun getDefault(): StorageLocationEntity? {
        return locationsState.value.values.firstOrNull { it.isDefault }
    }

    override suspend fun upsert(location: StorageLocationEntity) {
        locationsState.value += (location.id to location)
    }

    override suspend fun delete(location: StorageLocationEntity) {
        locationsState.value -= location.id
    }

    override suspend fun incrementOccupiedSlots(id: String, count: Int) {
        val location = locationsState.value[id] ?: return
        locationsState.value += (id to location.copy(occupiedSlots = location.occupiedSlots + count))
    }

    override suspend fun decrementOccupiedSlots(id: String, count: Int) {
        val location = locationsState.value[id] ?: return
        locationsState.value += (id to location.copy(occupiedSlots = (location.occupiedSlots - count).coerceAtLeast(0)))
    }
}
