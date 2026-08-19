package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Service

/**
 * Repository for managing [Service] domain models.
 */
interface ServiceRepository {

    /**
     * Observes the list of all available services.
     */
    fun getServices(): Flow<List<Service>>

    /**
     * Retrieves a service by its ID.
     */
    suspend fun getServiceById(id: String): Result<Service, DataError>

    /**
     * Inserts or updates a service.
     */
    suspend fun saveService(service: Service): Result<String, DataError>

    /**
     * Deletes a service.
     */
    suspend fun deleteService(service: Service): Result<Unit, DataError>
}
