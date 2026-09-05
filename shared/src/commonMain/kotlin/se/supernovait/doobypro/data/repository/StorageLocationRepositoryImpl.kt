package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.data.local.dao.StorageLocationDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import kotlin.coroutines.CoroutineContext

class StorageLocationRepositoryImpl(
    private val storageLocationDao: StorageLocationDao
) : StorageLocationRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getActiveLocations(): Flow<List<StorageLocation>> {
        return storageLocationDao.getAllActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLocationById(id: String): Result<StorageLocation, DataError> {
        return withContext(ioContext) {
            val location = storageLocationDao.getById(id)
            if (location != null) {
                Result.Success(location.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun getDefaultLocation(): Result<StorageLocation, DataError> {
        return withContext(ioContext) {
            val location = storageLocationDao.getDefault()
            if (location != null) {
                Result.Success(location.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveLocation(location: StorageLocation): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entityToSave = location.toEntity()
                storageLocationDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteLocation(location: StorageLocation): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                storageLocationDao.delete(location.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }

    override suspend fun incrementOccupiedSlots(id: String) {
        storageLocationDao.incrementOccupiedSlots(id)
    }

    override suspend fun decrementOccupiedSlots(id: String) {
        storageLocationDao.decrementOccupiedSlots(id)
    }
}
