package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.data.local.entity.AgreementEntity

/**
 * Data Access Object for the "agreements" table.
 */
@Dao
interface AgreementDao {

    /**
     * Observes all lease agreements.
     */
    @Query("SELECT * FROM agreements")
    fun getAll(): Flow<List<AgreementEntity>>

    /**
     * Retrieves an agreement by its ID.
     */
    @Query("SELECT * FROM agreements WHERE id = :id")
    suspend fun getById(id: String): AgreementEntity?

    /**
     * Inserts or updates a lease agreement.
     */
    @Upsert
    suspend fun upsert(agreement: AgreementEntity)

    /**
     * Deletes a lease agreement.
     */
    @Delete
    suspend fun delete(agreement: AgreementEntity)
}
