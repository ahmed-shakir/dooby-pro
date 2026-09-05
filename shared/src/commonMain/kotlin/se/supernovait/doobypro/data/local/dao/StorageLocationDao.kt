package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.data.local.entity.StorageLocationEntity

@Dao
interface StorageLocationDao {

    @Query("SELECT * FROM storage_locations WHERE isActive = 1")
    fun getAllActive(): Flow<List<StorageLocationEntity>>

    @Query("SELECT * FROM storage_locations WHERE id IN (:ids)")
    suspend fun getAllByIds(ids: List<String>): List<StorageLocationEntity>

    @Query("SELECT * FROM storage_locations WHERE id = :id")
    suspend fun getById(id: String): StorageLocationEntity?

    @Query("SELECT * FROM storage_locations WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): StorageLocationEntity?

    /**
     * Inserts or updates a service.
     *
     * @param location The storage location entity to upsert.
     */
    @Upsert
    suspend fun upsert(location: StorageLocationEntity)

    /**
     * Deletes a storage location.
     *
     * @param location The storage location entity to delete.
     */
    @Delete
    suspend fun delete(location: StorageLocationEntity)

    @Query("UPDATE storage_locations SET occupiedSlots = occupiedSlots + :count WHERE id = :id")
    suspend fun incrementOccupiedSlots(id: String, count: Int = 1)

    @Query("UPDATE storage_locations SET occupiedSlots = MAX(0, occupiedSlots - :count) WHERE id = :id")
    suspend fun decrementOccupiedSlots(id: String, count: Int = 1)
}
