package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.doobypro.domain.repository.LicenseRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [LicenseRepository] using [LicenseDao].
 *
 * @param logger The logger for license operations.
 * @param crashReporter The crash reporter for error reporting.
 * @param licenseDao The data access object for license entities.
 */
class LicenseRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val licenseDao: LicenseDao
) : LicenseRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getLicenses(accountId: String): List<License> {
        return withContext(ioContext) {
            logger.debug("Fetching licenses for account with ID: $accountId", tag = LogTags.LICENSE_REPO)
            licenseDao.getByAccountId(accountId).map { it.toDomain() }
        }
    }

    override suspend fun getLicenseById(id: String): Result<License, DataError> {
        return withContext(ioContext) {
            logger.debug("Fetching license with ID: $id", tag = LogTags.LICENSE_REPO)
            licenseDao.getById(id)?.toDomain()?.let {
                Result.Success(it)
            } ?: run {
                logger.warn("License not found with ID: $id", tag = LogTags.LICENSE_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveLicense(license: License): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Saving license with ID: ${license.id}", tag = LogTags.LICENSE_REPO)
                val entityToSave = license.toEntity()
                licenseDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (e: Exception) {
                logger.error("Error saving license with ID: ${license.id}", e, tag = LogTags.LICENSE_REPO)
                crashReporter.recordException(e, mapOf("action" to "saveLicense", "licenseId" to license.id))
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteLicense(license: License): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Deleting license with ID: ${license.id}", tag = LogTags.LICENSE_REPO)
                licenseDao.delete(license.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error deleting license with ID: ${license.id}", e, tag = LogTags.LICENSE_REPO)
                crashReporter.recordException(e, mapOf("action" to "deleteLicense", "licenseId" to license.id))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
