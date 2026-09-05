package se.supernovait.doobypro.domain.model.settings.common

import kotlinx.serialization.Serializable

@Serializable
enum class Language(val code: String, val label: String) {
    ENGLISH("en", "English"),
    ARABIC("ar", "عربي")
}
