package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.storage.StorageLocation

/**
 * Repository for managing [StorageLocation] models.
 */
interface StorageLocationRepository {
    fun getActiveLocations(): Flow<List<StorageLocation>>
    suspend fun getLocationById(id: String): Result<StorageLocation, DataError>
    suspend fun getDefaultLocation(): Result<StorageLocation, DataError>
    suspend fun saveLocation(location: StorageLocation): Result<String, DataError>
    suspend fun deleteLocation(location: StorageLocation): Result<Unit, DataError>
    suspend fun incrementOccupiedSlots(id: String)
    suspend fun decrementOccupiedSlots(id: String)
}
