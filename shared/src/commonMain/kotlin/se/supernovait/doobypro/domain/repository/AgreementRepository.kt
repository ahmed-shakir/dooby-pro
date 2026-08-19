package se.supernovait.doobypro.domain.repository

import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.agreement.Agreement

/**
 * Repository for managing equipment lease agreements.
 */
interface AgreementRepository {

    /**
     * Retrieves all agreements for a specific account.
     */
    suspend fun getAgreements(accountId: String): List<Agreement>

    /**
     * Retrieves an agreement by its ID.
     */
    suspend fun getAgreementById(id: String): Result<Agreement, DataError>

    /**
     * Inserts or updates an agreement.
     */
    suspend fun saveAgreement(agreement: Agreement): Result<String, DataError>

    /**
     * Deletes an agreement.
     */
    suspend fun deleteAgreement(agreement: Agreement): Result<Unit, DataError>
}
