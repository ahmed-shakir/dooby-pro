package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.mapper.mapToEntity
import se.supernovait.doobypro.data.local.mapper.mapToModel
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.repository.DoobyRepository
import kotlin.coroutines.CoroutineContext

class DoobyRepositoryImpl(
    private val serviceDao: ServiceDao,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : DoobyRepository {

    override fun getServices(): Flow<List<Service>> {
        return serviceDao.getAll().map { services -> services.map { it.mapToModel() } }
    }

    override suspend fun getServiceById(id: String): Service? {
        return withContext(ioContext) {
            serviceDao.getById(id)?.mapToModel()
        }
    }

    override suspend fun upsertService(service: Service) {
        withContext(ioContext) {
            serviceDao.upsert(service.mapToEntity())
        }
    }

    override suspend fun deleteService(service: Service) {
        withContext(ioContext) {
            serviceDao.delete(service.mapToEntity())
        }
    }
}
