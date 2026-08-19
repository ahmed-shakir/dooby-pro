package se.supernovait.doobypro.domain.repository

import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Account

/**
 * Repository for managing [Account] domain models.
 *
 * This repository is responsible for assembling the full account model from
 * multiple data sources.
 */
interface AccountRepository {

    /**
     * Retrieves the account for a specific ID.
     *
     * @param id The account identifier.
     * @return The assembled [Account], or null if not found.
     */
    suspend fun getAccount(id: String): Result<Account, DataError>

    /**
     * Inserts or updates a account.
     *
     * @param account The domain account model to upsert.
     */
    suspend fun saveAccount(account: Account): Result<String, DataError>

    /**
     * Deletes a account.
     *
     * @param account The domain account model to delete.
     */
    suspend fun deleteAccount(account: Account): Result<Unit, DataError>
}
