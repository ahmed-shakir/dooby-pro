package se.supernovait.doobypro.domain.repository

import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Account

/**
 * Repository for managing [Account] domain models.
 */
interface AccountRepository {

    /**
     * Retrieves the account for a specific ID.
     */
    suspend fun getAccount(id: String): Result<Account, DataError>

    /**
     * Retrieves the account associated with a specific user ID.
     */
    suspend fun getAccountByUserId(userId: String): Result<Account, DataError>

    /**
     * Inserts or updates an account.
     */
    suspend fun saveAccount(account: Account): Result<String, DataError>

    /**
     * Soft-deletes an account by marking it for deletion and deactivating it.
     * The actual data will be purged after a grace period.
     */
    suspend fun deleteAccount(id: String): Result<Unit, DataError>

    /**
     * Hard-deletes all accounts that have been marked for deletion and have
     * exceeded the grace period threshold.
     */
    suspend fun purgeDeletedAccounts(): Result<Int, DataError>
}
