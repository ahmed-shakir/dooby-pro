package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.doobypro.domain.model.agreement.Agreement

/**
 * Repository for managing equipment lease agreements.
 */
interface AgreementRepository {

    /**
     * Observes all agreements in the system.
     */
    fun getAgreements(): Flow<List<Agreement>>

    /**
     * Retrieves an agreement by its ID.
     */
    suspend fun getAgreementById(id: String): Agreement?

    /**
     * Inserts or updates an agreement.
     */
    suspend fun saveAgreement(agreement: Agreement)

    /**
     * Deletes an agreement.
     */
    suspend fun deleteAgreement(agreement: Agreement)
}
