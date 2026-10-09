package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.observability.performance.PerformanceMonitor
import se.supernovait.app.core.domain.observability.performance.traceAsync
import se.supernovait.doobypro.data.local.dao.BusinessHoursDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.company.BusinessHours
import se.supernovait.doobypro.domain.model.company.Company
import se.supernovait.doobypro.domain.repository.CompanyRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [CompanyRepository] using [CompanyDao].
 */
class CompanyRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val performanceMonitor: PerformanceMonitor,
    private val companyDao: CompanyDao,
    private val businessHoursDao: BusinessHoursDao
) : CompanyRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getCompanies(): Flow<List<Company>> {
        logger.debug("Observing all companies", tag = LogTags.COMPANY_REPO)
        return companyDao.getAll().map { entities ->
            entities.map { entity ->
                val bhEntities = businessHoursDao.getBusinessHours(entity.id)
                val dayHoursMap = bhEntities.associate { bhEntity ->
                    val day = DayOfWeek.entries.first { (it.ordinal + 1) == bhEntity.dayOfWeek }
                    day to bhEntity.toDayHours()
                }
                val businessHours = BusinessHours(companyId = entity.id, dayHours = dayHoursMap)
                entity.toDomain().copy(businessHours = businessHours)
            }
        }
    }

    override suspend fun getCompanyById(id: String): Result<Company, DataError> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("CompanyRepository.getCompanyById") {
                logger.debug("Fetching company with ID: $id", tag = LogTags.COMPANY_REPO)
                val entity = companyDao.getById(id)
                if (entity == null) {
                    logger.warn("Company not found with ID: $id", tag = LogTags.COMPANY_REPO)
                    return@traceAsync Result.Failure(DataError.NOT_FOUND)
                }
                val bhEntities = businessHoursDao.getBusinessHours(id)
                val dayHoursMap = bhEntities.associate { bhEntity ->
                    val day = DayOfWeek.entries.first { (it.ordinal + 1) == bhEntity.dayOfWeek }
                    day to bhEntity.toDayHours()
                }
                val businessHours = BusinessHours(companyId = id, dayHours = dayHoursMap)
                Result.Success(entity.toDomain().copy(businessHours = businessHours))
            }
        }
    }

    override suspend fun saveCompany(company: Company): Result<String, DataError> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("CompanyRepository.saveCompany") {
                try {
                    logger.info("Saving company profile for company with ID: ${company.id}", tag = LogTags.COMPANY_REPO)
                    val entityToSave = company.toEntity()
                    companyDao.upsert(entityToSave)
                    Result.Success(entityToSave.id)
                } catch (e: Exception) {
                    logger.error("Error saving company profile for company with ID: ${company.id}", e, tag = LogTags.COMPANY_REPO)
                    crashReporter.recordException(e, mapOf("action" to "saveCompany", "companyId" to (company.id ?: "new")))
                    Result.Failure(DataError.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun deleteCompany(company: Company): Result<Unit, DataError> {
        return withContext(ioContext) {
            performanceMonitor.traceAsync("CompanyRepository.deleteCompany") {
                try {
                    logger.info("Deleting company profile for company with ID: ${company.id}", tag = LogTags.COMPANY_REPO)
                    companyDao.delete(company.toEntity())
                    Result.Success(Unit)
                } catch (e: Exception) {
                    logger.error("Error deleting company profile for company with ID: ${company.id}", e, tag = LogTags.COMPANY_REPO)
                    crashReporter.recordException(e, mapOf("action" to "deleteCompany", "companyId" to (company.id ?: "unknown")))
                    Result.Failure(DataError.UNKNOWN)
                }
            }
        }
    }
}
