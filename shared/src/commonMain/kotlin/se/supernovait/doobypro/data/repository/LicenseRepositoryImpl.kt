package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
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

    override suspend fun getLicenseById(id: String): Result<License, DataError> {
        return withContext(ioContext) {
            val license = licenseDao.getById(id)
            if (license != null) {
                Result.Success(license.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveLicense(license: License): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entityToSave = license.toEntity()
                licenseDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteLicense(license: License): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                licenseDao.delete(license.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
