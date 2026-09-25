package se.supernovait.doobypro.domain.model.businesshours

import kotlinx.datetime.LocalTime

sealed class DayHours {
    data object Closed : DayHours()
    data object OpenAllDay : DayHours()
    data class OpenRange(
        val openTime: LocalTime,
        val closeTime: LocalTime
    ) : DayHours()

    fun toStatus(): String = when (this) {
        Closed -> "closed"
        OpenAllDay -> "open-all-day"
        is OpenRange -> "open-range"
    }

    companion object {
        fun fromStatus(status: String, openTime: LocalTime?, closeTime: LocalTime?): DayHours {
            return when (status.lowercase()) {
                "open-all-day" -> OpenAllDay
                "open-range" -> OpenRange(
                    openTime = openTime ?: LocalTime(0, 0),
                    closeTime = closeTime ?: LocalTime(0, 0)
                )
                else -> Closed
            }
        }
    }
}
