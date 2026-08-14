package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import se.supernovait.doobypro.data.local.entity.AccountEntity

/**
 * Data Access Object for the "accounts" table.
 */
@Dao
interface AccountDao {

    /**
     * Retrieves an account by its ID.
     */
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: String): AccountEntity?

    /**
     * Inserts or updates an account.
     */
    @Upsert
    suspend fun upsert(account: AccountEntity)

    /**
     * Deletes an account.
     */
    @Delete
    suspend fun delete(account: AccountEntity)
}
