package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.data.local.entity.ServiceEntity

/**
 * Data Access Object for the "services" table.
 */
@Dao
interface ServiceDao {

    /**
     * Observes all services in the database.
     *
     * @return A flow emitting the list of all services.
     */
    @Query("SELECT * FROM services")
    fun getAll(): Flow<List<ServiceEntity>>

    /**
     * Retrieves a list of services by their IDs.
     *
     * @param ids The list of service identifiers.
     * @return A list of found [ServiceEntity] objects.
     */
    @Query("SELECT * FROM services WHERE id IN (:ids)")
    suspend fun getAllByIds(ids: List<String>): List<ServiceEntity>

    /**
     * Retrieves a service by its ID.
     *
     * @param id The unique identifier of the service.
     * @return The found [ServiceEntity], or null if not found.
     */
    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getById(id: String): ServiceEntity?

    /**
     * Inserts or updates a service.
     *
     * @param service The service entity to upsert.
     */
    @Upsert
    suspend fun upsert(service: ServiceEntity)

    /**
     * Deletes a service.
     *
     * @param service The service entity to delete.
     */
    @Delete
    suspend fun delete(service: ServiceEntity)
}
