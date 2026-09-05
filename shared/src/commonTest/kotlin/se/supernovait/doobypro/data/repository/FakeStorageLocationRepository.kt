package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.StorageLocationRepository

class FakeStorageLocationRepository : StorageLocationRepository {
    private val locationsState = MutableStateFlow<List<StorageLocation>>(emptyList())
    var incrementCount = 0
    var decrementCount = 0

    override fun getActiveLocations(): Flow<List<StorageLocation>> = locationsState

    override suspend fun getLocationById(id: String): Result<StorageLocation, DataError> {
        val location = locationsState.value.find { it.id == id }
        return if (location != null) Result.Success(location) else Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun getDefaultLocation(): Result<StorageLocation, DataError> {
        val location = locationsState.value.find { it.isDefault }
        return if (location != null) Result.Success(location) else Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun saveLocation(location: StorageLocation): Result<String, DataError> {
        val id = location.id ?: "gen_id"
        val newLocation = location.copy(id = id)
        locationsState.value = locationsState.value.filterNot { it.id == id } + newLocation
        return Result.Success(id)
    }

    override suspend fun deleteLocation(location: StorageLocation): Result<Unit, DataError> {
        locationsState.value = locationsState.value.filterNot { it.id == location.id }
        return Result.Success(Unit)
    }

    override suspend fun incrementOccupiedSlots(id: String) {
        incrementCount++
    }

    override suspend fun decrementOccupiedSlots(id: String) {
        decrementCount++
    }
}
