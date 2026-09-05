package se.supernovait.doobypro.domain.model.settings.common

import kotlinx.serialization.Serializable

@Serializable
enum class DateFormat(val pattern: String) {
    DD_MM_YYYY("DD/MM/YYYY"),
    MM_DD_YYYY("MM/DD/YYYY"),
    YYYY_MM_DD("YYYY-MM-DD")
}
