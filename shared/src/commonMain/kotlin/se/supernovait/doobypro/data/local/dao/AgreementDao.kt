package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import se.supernovait.doobypro.data.local.entity.AgreementEntity

/**
 * Data Access Object for the "agreements" table.
 */
@Dao
interface AgreementDao {

    /**
     * Gets all agreements for a specific account.
     */
    @Query("SELECT * FROM agreements WHERE accountId = :accountId")
    suspend fun getByAccountId(accountId: String): List<AgreementEntity>

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
