package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.domain.model.Service

interface DoobyRepository {
    /* *** SERVICE *** */
    fun getServices(): Flow<List<Service>>
    suspend fun getServiceById(id: String): Service?
    suspend fun upsertService(service: Service)
    suspend fun deleteService(service: Service)
}
