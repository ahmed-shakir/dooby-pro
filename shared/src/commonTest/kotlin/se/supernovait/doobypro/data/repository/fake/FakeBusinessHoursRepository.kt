package se.supernovait.doobypro.data.repository.fake

import kotlinx.datetime.DayOfWeek
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.company.BusinessHours
import se.supernovait.doobypro.domain.model.company.DayHours
import se.supernovait.doobypro.domain.repository.BusinessHoursRepository

class FakeBusinessHoursRepository : BusinessHoursRepository {
    private var businessHours = BusinessHours(companyId = "comp-123")

    override suspend fun getBusinessHours(companyId: String): Result<BusinessHours, DataError> {
        return Result.Success(businessHours.copy(companyId = companyId))
    }

    override suspend fun saveBusinessHours(businessHours: BusinessHours): Result<Unit, DataError> {
        this.businessHours = businessHours
        return Result.Success(Unit)
    }

    override suspend fun updateDayHours(companyId: String, day: DayOfWeek, hours: DayHours): Result<Unit, DataError> {
        val updatedMap = businessHours.dayHours.toMutableMap().apply { put(day, hours) }
        businessHours = businessHours.copy(companyId = companyId, dayHours = updatedMap)
        return Result.Success(Unit)
    }
}
