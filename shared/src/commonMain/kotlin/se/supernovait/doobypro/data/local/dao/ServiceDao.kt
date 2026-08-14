package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.data.local.entity.ServiceEntity

@Dao
interface ServiceDao {

    @Query("SELECT * FROM services")
    fun getAll(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getById(id: String): ServiceEntity?

    @Upsert
    suspend fun upsert(service: ServiceEntity)

    @Delete
    suspend fun delete(service: ServiceEntity)
}
