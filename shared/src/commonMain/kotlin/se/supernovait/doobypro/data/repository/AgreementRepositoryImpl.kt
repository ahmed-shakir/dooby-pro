package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.observability.performance.PerformanceMonitor
import se.supernovait.app.core.domain.observability.performance.traceAsync
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.repository.AgreementRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [AgreementRepository] using [AgreementDao].
 */
class AgreementRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val performanceMonitor: PerformanceMonitor,
    private val agreementDao: AgreementDao
) : AgreementRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getAgreements(accountId: String): List<Agreement> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("AgreementRepository.getAgreements") {
                logger.debug("Fetching agreements for account with ID: $accountId", tag = LogTags.AGREEMENT_REPO)
                agreementDao.getByAccountId(accountId).map { it.toDomain() }
            }
        }
    }

    override suspend fun getAgreementsByIds(ids: List<String>): List<Agreement> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("AgreementRepository.getAgreementsByIds") {
                logger.debug("Fetching ${ids.size} agreements by IDs", tag = LogTags.AGREEMENT_REPO)
                agreementDao.getByIds(ids).map { it.toDomain() }
            }
        }
    }

    override suspend fun getAgreementById(id: String): Result<Agreement, DataError> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("AgreementRepository.getAgreementById") {
                logger.debug("Fetching agreement with ID: $id", tag = LogTags.AGREEMENT_REPO)
                agreementDao.getById(id)?.toDomain()?.let {
                    Result.Success(it)
                } ?: run {
                    logger.warn("Agreement not found with ID: $id", tag = LogTags.AGREEMENT_REPO)
                    Result.Failure(DataError.NOT_FOUND)
                }
            }
        }
    }

    override suspend fun saveAgreement(agreement: Agreement): Result<String, DataError> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("AgreementRepository.saveAgreement") {
                try {
                    logger.info("Saving agreement with ID: ${agreement.id}", tag = LogTags.AGREEMENT_REPO)
                    val entityToSave = agreement.toEntity()
                    agreementDao.upsert(entityToSave)
                    Result.Success(entityToSave.id)
                } catch (e: Exception) {
                    logger.error("Error saving agreement with ID: ${agreement.id}", e, tag = LogTags.AGREEMENT_REPO)
                    crashReporter.recordException(e, mapOf("action" to "saveAgreement", "agreementId" to (agreement.id ?: "new")))
                    Result.Failure(DataError.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun deleteAgreement(agreement: Agreement): Result<Unit, DataError> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("AgreementRepository.deleteAgreement") {
                try {
                    logger.info("Deleting agreement with ID: ${agreement.id}", tag = LogTags.AGREEMENT_REPO)
                    agreementDao.delete(agreement.toEntity())
                    Result.Success(Unit)
                } catch (e: Exception) {
                    logger.error("Error deleting agreement with ID: ${agreement.id}", e, tag = LogTags.AGREEMENT_REPO)
                    crashReporter.recordException(e, mapOf("action" to "deleteAgreement", "agreementId" to (agreement.id ?: "unknown")))
                    Result.Failure(DataError.UNKNOWN)
                }
            }
        }
    }
}
