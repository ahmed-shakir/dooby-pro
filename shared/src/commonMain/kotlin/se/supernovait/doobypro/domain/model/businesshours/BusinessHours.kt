package se.supernovait.doobypro.domain.model.businesshours

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.extension.now

data class BusinessHours(
    val companyId: String,
    val dayHours: Map<DayOfWeek, DayHours> = DayOfWeek.entries.associateWith { DayHours.Closed }
) {
    fun getDayHours(day: DayOfWeek): DayHours = dayHours[day] ?: DayHours.Closed

    fun isOpenNow(currentDateTime: LocalDateTime = LocalDateTime.now()): Boolean {
        val day = currentDateTime.dayOfWeek
        return when (val hours = getDayHours(day)) {
            DayHours.Closed -> false
            DayHours.OpenAllDay -> true
            is DayHours.OpenRange -> {
                val currentTime = currentDateTime.time
                currentTime in hours.openTime..hours.closeTime
            }
        }
    }
}
