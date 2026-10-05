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
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [ServiceRepository] using [ServiceDao].
 */
class ServiceRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val serviceDao: ServiceDao
) : ServiceRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getServices(): Flow<List<Service>> {
        logger.debug("Observing services catalog stream", tag = LogTags.SERVICE_REPO)
        return serviceDao.getAll().map { services ->
            services.map { it.toDomain() }
        }
    }

    override suspend fun getServiceById(id: String): Result<Service, DataError> {
        return withContext(ioContext) {
            logger.debug("Fetching service with ID: $id", tag = LogTags.SERVICE_REPO)
            serviceDao.getById(id)?.toDomain()?.let {
                Result.Success(it)
            } ?: run {
                logger.warn("Service not found with ID: $id", tag = LogTags.SERVICE_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveService(service: Service): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Saving service '${service.title}'", tag = LogTags.SERVICE_REPO)
                val entityToSave = service.toEntity()
                serviceDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (e: Exception) {
                logger.error("Error saving service '${service.title}'", e, tag = LogTags.SERVICE_REPO)
                crashReporter.recordException(e, mapOf("action" to "saveService", "serviceTitle" to service.title))
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteService(service: Service): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Deleting service with ID: ${service.id}", tag = LogTags.SERVICE_REPO)
                serviceDao.delete(service.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error deleting service with ID: ${service.id}", e, tag = LogTags.SERVICE_REPO)
                crashReporter.recordException(e, mapOf("action" to "deleteService", "serviceId" to (service.id ?: "unknown")))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
