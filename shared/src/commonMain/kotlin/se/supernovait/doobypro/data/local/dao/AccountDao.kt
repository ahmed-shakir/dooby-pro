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
     * Retrieves all accounts marked for deletion.
     */
    @Query("SELECT * FROM accounts WHERE isMarkedForDeletion = 1")
    suspend fun getAccountsMarkedForDeletion(): List<AccountEntity>

    /**
     * Retrieves an account by its ID.
     */
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: String): AccountEntity?

    /**
     * Retrieves an account by user ID.
     */
    @Query("SELECT * FROM accounts WHERE userId = :userId")
    suspend fun getByUserId(userId: String): AccountEntity?

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
