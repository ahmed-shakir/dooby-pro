package se.supernovait.doobypro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import se.supernovait.doobypro.data.local.entity.BusinessHoursDayEntity

@Dao
interface BusinessHoursDao {
    @Query("SELECT * FROM business_hours_day WHERE companyId = :companyId")
    suspend fun getBusinessHours(companyId: String): List<BusinessHoursDayEntity>

    @Query("SELECT * FROM business_hours_day WHERE companyId = :companyId AND dayOfWeek = :dayOfWeek")
    suspend fun getDayHours(companyId: String, dayOfWeek: Int): BusinessHoursDayEntity?

    @Upsert
    suspend fun upsert(entity: BusinessHoursDayEntity)

    @Upsert
    suspend fun upsertAll(entities: List<BusinessHoursDayEntity>)

    @Delete
    suspend fun delete(entity: BusinessHoursDayEntity)

    @Query("DELETE FROM business_hours_day WHERE companyId = :companyId")
    suspend fun deleteAllForCompany(companyId: String)
}
