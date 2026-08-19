package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Company

/**
 * Repository for managing [Company] domain models.
 */
interface CompanyRepository {

    /**
     * Observes the list of all companies.
     */
    fun getCompanies(): Flow<List<Company>>

    /**
     * Retrieves a company by its ID.
     */
    suspend fun getCompanyById(id: String): Result<Company, DataError>

    /**
     * Inserts or updates a company profile.
     */
    suspend fun saveCompany(company: Company): Result<String, DataError>

    /**
     * Deletes a company profile.
     */
    suspend fun deleteCompany(company: Company): Result<Unit, DataError>
}
