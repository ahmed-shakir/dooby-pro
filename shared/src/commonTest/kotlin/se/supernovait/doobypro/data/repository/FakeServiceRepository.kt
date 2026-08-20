package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.repository.ServiceRepository

class FakeServiceRepository : ServiceRepository {
    private val services = MutableStateFlow<List<Service>>(emptyList())

    override fun getServices(): Flow<List<Service>> = services

    override suspend fun getServiceById(id: String): Result<Service, DataError> {
        val service = services.value.find { it.id == id }
        return if (service != null) Result.Success(service) else Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun saveService(service: Service): Result<String, DataError> {
        val id = service.id ?: "service_${services.value.size}"
        val newService = service.copy(id = id)
        services.value = services.value + newService
        return Result.Success(id)
    }

    override suspend fun deleteService(service: Service): Result<Unit, DataError> {
        services.value = services.value.filter { it.id != service.id }
        return Result.Success(Unit)
    }
}
