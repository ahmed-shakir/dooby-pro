package se.supernovait.doobypro.domain.repository

import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.model.license.License

/**
 * Repository for managing software [License] domain models.
 */
interface LicenseRepository {

    /**
     * Retrieves all licenses for a specific account.
     *
     * @param accountId The unique identifier of the account.
     * @return A list of licenses associated with the account.
     */
    suspend fun getLicenses(accountId: String): List<License>

    /**
     * Retrieves a license by its ID.
     *
     * @param id The unique identifier of the license.
     * @return The found [License], or null if not found.
     */
    suspend fun getLicenseById(id: String): Result<License, DataError>

    /**
     * Inserts or updates a license.
     *
     * @param license The domain license model to upsert.
     */
    suspend fun saveLicense(license: License): Result<String, DataError>

    /**
     * Deletes a license.
     *
     * @param license The domain license model to delete.
     */
    suspend fun deleteLicense(license: License): Result<Unit, DataError>
}
