package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.doobypro.data.local.dao.BusinessHoursDao
import se.supernovait.doobypro.data.local.entity.BusinessHoursDayEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.company.BusinessHours
import se.supernovait.doobypro.domain.model.company.DayHours
import se.supernovait.doobypro.domain.repository.BusinessHoursRepository
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext

class BusinessHoursRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val businessHoursDao: BusinessHoursDao
) : BusinessHoursRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getBusinessHours(companyId: String): Result<BusinessHours, DataError> {
        return withContext(ioContext) {
            try {
                logger.debug("Fetching business hours for company with ID: $companyId", tag = LogTags.BUSINESS_HOURS_REPO)
                val entities = businessHoursDao.getBusinessHours(companyId)
                val dayHoursMap = entities.associate { entity ->
                    val dayOfWeek = DayOfWeek.entries.first { (it.ordinal + 1) == entity.dayOfWeek }
                    dayOfWeek to entity.toDayHours()
                }

                val completeDayHours = DayOfWeek.entries.associateWith { day -> (dayHoursMap[day] ?: DayHours.Closed) }

                Result.Success(
                    BusinessHours(
                        companyId = companyId,
                        dayHours = completeDayHours
                    )
                )
            } catch (e: Exception) {
                logger.error("Error fetching business hours for company with ID: $companyId", e, tag = LogTags.BUSINESS_HOURS_REPO)
                crashReporter.recordException(e, mapOf("action" to "getBusinessHours", "companyId" to companyId))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }

    override suspend fun saveBusinessHours(businessHours: BusinessHours): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Saving business hours for company with ID: ${businessHours.companyId}", tag = LogTags.BUSINESS_HOURS_REPO)
                val entities = businessHours.dayHours.map { (day, dayHours) ->
                    val id = SupernovaIdGenerator.generateId(IdType.BUSINESS_HOURS.prefix)
                    BusinessHoursDayEntity.fromDayHours(id, businessHours.companyId, day, dayHours)
                }

                businessHoursDao.deleteAllForCompany(businessHours.companyId)
                businessHoursDao.upsertAll(entities)

                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error saving business hours for company with ID: ${businessHours.companyId}", e, tag = LogTags.BUSINESS_HOURS_REPO)
                crashReporter.recordException(e, mapOf("action" to "saveBusinessHours", "companyId" to businessHours.companyId))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }

    override suspend fun updateDayHours(companyId: String, day: DayOfWeek, hours: DayHours): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Updating business hours for company with ID: $companyId on $day", tag = LogTags.BUSINESS_HOURS_REPO)
                val existingEntity = businessHoursDao.getDayHours(companyId, day.ordinal + 1)
                val id = existingEntity?.id ?: SupernovaIdGenerator.generateId(IdType.BUSINESS_HOURS.prefix)
                val entity = BusinessHoursDayEntity.fromDayHours(id, companyId, day, hours)

                businessHoursDao.upsert(entity)
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error updating business hours for company with ID: $companyId on $day", e, tag = LogTags.BUSINESS_HOURS_REPO)
                crashReporter.recordException(e, mapOf("action" to "updateDayHours", "companyId" to companyId, "day" to day.name))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
