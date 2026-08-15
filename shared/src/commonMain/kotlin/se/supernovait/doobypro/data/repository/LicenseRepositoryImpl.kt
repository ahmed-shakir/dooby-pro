package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.doobypro.domain.repository.LicenseRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [LicenseRepository] using [LicenseDao].
 *
 * @param licenseDao The data access object for license entities.
 */
class LicenseRepositoryImpl(
    private val licenseDao: LicenseDao
) : LicenseRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getLicenses(accountId: String): List<License> {
        return withContext(ioContext) {
            licenseDao.getByAccountId(accountId).map { it.toDomain() }
        }
    }

    override suspend fun getLicenseById(id: String): License? {
        return withContext(ioContext) {
            licenseDao.getById(id)?.toDomain()
        }
    }

    override suspend fun upsertLicense(license: License) {
        withContext(ioContext) {
            licenseDao.upsert(license.toEntity())
        }
    }

    override suspend fun deleteLicense(license: License) {
        withContext(ioContext) {
            licenseDao.delete(license.toEntity())
        }
    }
}
