package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.crash.CrashReporter
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.data.local.dao.StorageLocationDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

class StorageLocationRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val storageLocationDao: StorageLocationDao
) : StorageLocationRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getActiveLocations(): Flow<List<StorageLocation>> {
        logger.debug("Observing active storage locations", tag = LogTags.STORAGE_LOCATION_REPO)
        return storageLocationDao.getAllActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLocationById(id: String): Result<StorageLocation, DataError> {
        return withContext(ioContext) {
            logger.debug("Fetching storage location with ID: $id", tag = LogTags.STORAGE_LOCATION_REPO)
            storageLocationDao.getById(id)?.toDomain()?.let {
                Result.Success(it)
            } ?: run {
                logger.warn("Storage location not found with ID: $id", tag = LogTags.STORAGE_LOCATION_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun getDefaultLocation(): Result<StorageLocation, DataError> {
        return withContext(ioContext) {
            logger.debug("Fetching default storage location", tag = LogTags.STORAGE_LOCATION_REPO)
            val location = storageLocationDao.getDefault()
            if (location != null) {
                Result.Success(location.toDomain())
            } else {
                logger.warn("Default storage location not found", tag = LogTags.STORAGE_LOCATION_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveLocation(location: StorageLocation): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Saving storage location '${location.label}'", tag = LogTags.STORAGE_LOCATION_REPO)
                val entityToSave = location.toEntity()
                storageLocationDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (e: Exception) {
                logger.error("Error saving storage location '${location.label}'", e, tag = LogTags.STORAGE_LOCATION_REPO)
                crashReporter.recordException(e, mapOf("action" to "saveLocation", "label" to location.label))
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteLocation(location: StorageLocation): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Deleting storage location with ID: ${location.id}", tag = LogTags.STORAGE_LOCATION_REPO)
                storageLocationDao.delete(location.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error deleting storage location with ID: ${location.id}", e, tag = LogTags.STORAGE_LOCATION_REPO)
                crashReporter.recordException(e, mapOf("action" to "deleteLocation", "locationId" to (location.id ?: "unknown")))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }

    override suspend fun incrementOccupiedSlots(id: String) {
        logger.debug("Incrementing occupied slots for storage location with ID: $id", tag = LogTags.STORAGE_LOCATION_REPO)
        storageLocationDao.incrementOccupiedSlots(id)
    }

    override suspend fun decrementOccupiedSlots(id: String) {
        logger.debug("Decrementing occupied slots for storage location with ID: $id", tag = LogTags.STORAGE_LOCATION_REPO)
        storageLocationDao.decrementOccupiedSlots(id)
    }
}
