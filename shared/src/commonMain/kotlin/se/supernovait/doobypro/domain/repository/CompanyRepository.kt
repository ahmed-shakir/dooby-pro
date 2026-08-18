package se.supernovait.doobypro.domain.repository

import kotlinx.coroutines.flow.Flow
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
    suspend fun getCompanyById(id: String): Company?

    /**
     * Inserts or updates a company profile.
     */
    suspend fun saveCompany(company: Company)

    /**
     * Deletes a company profile.
     */
    suspend fun deleteCompany(company: Company)
}
