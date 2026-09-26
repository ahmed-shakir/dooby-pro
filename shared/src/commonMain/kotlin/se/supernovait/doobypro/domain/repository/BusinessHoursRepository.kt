package se.supernovait.doobypro.domain.repository

import kotlinx.datetime.DayOfWeek
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.company.BusinessHours
import se.supernovait.doobypro.domain.model.company.DayHours

interface BusinessHoursRepository {
    suspend fun getBusinessHours(companyId: String): Result<BusinessHours, DataError>
    suspend fun saveBusinessHours(businessHours: BusinessHours): Result<Unit, DataError>
    suspend fun updateDayHours(companyId: String, day: DayOfWeek, hours: DayHours): Result<Unit, DataError>
}
