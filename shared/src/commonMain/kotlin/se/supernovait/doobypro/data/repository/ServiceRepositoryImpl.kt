package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.repository.ServiceRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [ServiceRepository] using [ServiceDao].
 */
class ServiceRepositoryImpl(
    private val serviceDao: ServiceDao
) : ServiceRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getServices(): Flow<List<Service>> {
        return serviceDao.getAll().map { services -> services.map { it.toDomain() } }
    }

    override suspend fun getServiceById(id: String): Result<Service, DataError> {
        return withContext(ioContext) {
            val service = serviceDao.getById(id)
            if (service != null) {
                Result.Success(service.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveService(service: Service): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entityToSave = service.toEntity()
                serviceDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteService(service: Service): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                serviceDao.delete(service.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
