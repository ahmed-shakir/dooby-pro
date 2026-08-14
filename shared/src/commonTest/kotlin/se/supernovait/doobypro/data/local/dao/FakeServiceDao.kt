package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.doobypro.data.local.entity.ServiceEntity

/**
 * A fake implementation of [ServiceDao] for unit testing purposes.
 */
class FakeServiceDao : ServiceDao {
    private val servicesState = MutableStateFlow<Map<String, ServiceEntity>>(emptyMap())

    override fun getAll(): Flow<List<ServiceEntity>> {
        return servicesState.map { it.values.toList() }
    }

    override suspend fun getById(id: String): ServiceEntity? {
        return servicesState.value[id]
    }

    override suspend fun getAllByIds(ids: List<String>): List<ServiceEntity> {
        return ids.mapNotNull { servicesState.value[it] }
    }

    override suspend fun upsert(service: ServiceEntity) {
        servicesState.value += (service.id to service)
    }

    override suspend fun delete(service: ServiceEntity) {
        servicesState.value -= service.id
    }
}
