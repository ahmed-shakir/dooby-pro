package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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

    override suspend fun getServiceById(id: String): Service? {
        return withContext(ioContext) {
            serviceDao.getById(id)?.toDomain()
        }
    }

    override suspend fun saveService(service: Service) {
        withContext(ioContext) {
            serviceDao.upsert(service.toEntity())
        }
    }

    override suspend fun deleteService(service: Service) {
        withContext(ioContext) {
            serviceDao.delete(service.toEntity())
        }
    }
}
