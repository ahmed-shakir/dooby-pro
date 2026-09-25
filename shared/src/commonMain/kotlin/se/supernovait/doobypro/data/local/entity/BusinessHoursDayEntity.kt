package se.supernovait.doobypro.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import se.supernovait.doobypro.domain.model.businesshours.DayHours
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(
    tableName = "business_hours_day",
    foreignKeys = [
        ForeignKey(
            entity = CompanyEntity::class,
            parentColumns = ["id"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["companyId"])]
)
data class BusinessHoursDayEntity(
    @PrimaryKey val id: String,
    val companyId: String,
    val dayOfWeek: Int, // 1 (Mon) - 7 (Sun)
    val status: String, // "CLOSED", "OPEN_ALL_DAY", "OPEN_RANGE"
    val openTime: LocalTime?,
    val closeTime: LocalTime?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    fun toDayHours(): DayHours {
        return when (status) {
            "CLOSED" -> DayHours.Closed
            "OPEN_ALL_DAY" -> DayHours.OpenAllDay
            "OPEN_RANGE" -> DayHours.OpenRange(
                openTime = openTime ?: LocalTime(0, 0),
                closeTime = closeTime ?: LocalTime(23, 59)
            )
            else -> DayHours.Closed
        }
    }

    companion object {
        fun fromDayHours(
            id: String,
            companyId: String,
            dayOfWeek: DayOfWeek,
            dayHours: DayHours
        ): BusinessHoursDayEntity {
            return when (dayHours) {
                DayHours.Closed -> BusinessHoursDayEntity(
                    id = id,
                    companyId = companyId,
                    dayOfWeek = dayOfWeek.ordinal + 1,
                    status = "CLOSED",
                    openTime = null,
                    closeTime = null
                )
                DayHours.OpenAllDay -> BusinessHoursDayEntity(
                    id = id,
                    companyId = companyId,
                    dayOfWeek = dayOfWeek.ordinal + 1,
                    status = "OPEN_ALL_DAY",
                    openTime = null,
                    closeTime = null
                )
                is DayHours.OpenRange -> BusinessHoursDayEntity(
                    id = id,
                    companyId = companyId,
                    dayOfWeek = dayOfWeek.ordinal + 1,
                    status = "OPEN_RANGE",
                    openTime = dayHours.openTime,
                    closeTime = dayHours.closeTime
                )
            }
        }
    }
}
