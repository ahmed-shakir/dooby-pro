package se.supernovait.doobypro.domain.usecase

import kotlinx.datetime.DayOfWeek
import se.supernovait.doobypro.domain.model.company.BusinessHours
import se.supernovait.doobypro.domain.model.company.DayHours

class FormatBusinessHoursUseCase {

    fun formatDay(dayHours: DayHours): String {
        return when (dayHours) {
            DayHours.Closed -> "Closed"
            DayHours.OpenAllDay -> "Open 24 hours"
            is DayHours.OpenRange -> {
                val openStr = "${dayHours.openTime.hour.toString().padStart(2, '0')}:${dayHours.openTime.minute.toString().padStart(2, '0')}"
                val closeStr = "${dayHours.closeTime.hour.toString().padStart(2, '0')}:${dayHours.closeTime.minute.toString().padStart(2, '0')}"
                "$openStr - $closeStr"
            }
        }
    }

    fun formatCompact(businessHours: BusinessHours): List<Pair<String, String>> {
        val days = DayOfWeek.entries
        val result = mutableListOf<Pair<String, String>>()
        if (days.isEmpty()) return result

        var startDay = days[0]
        var currentHours = businessHours.getDayHours(startDay)
        var endDay = startDay

        for (i in 1 until days.size) {
            val day = days[i]
            val hours = businessHours.getDayHours(day)
            if (hours == currentHours) {
                endDay = day
            } else {
                result.add(formatDayRange(startDay, endDay) to formatDay(currentHours))
                startDay = day
                endDay = day
                currentHours = hours
            }
        }
        result.add(formatDayRange(startDay, endDay) to formatDay(currentHours))
        return result
    }

    private fun formatDayRange(start: DayOfWeek, end: DayOfWeek): String {
        val startName = start.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        if (start == end) return startName
        val endName = end.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        return "$startName - $endName"
    }
}

fun defaultFormatDayHours(dayHours: DayHours): String {
    return FormatBusinessHoursUseCase().formatDay(dayHours)
}
