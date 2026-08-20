package se.supernovait.doobypro.domain.model.settings.common

import kotlinx.serialization.Serializable

@Serializable
data class CommonSettings(
    val currency: Currency = Currency.AED,
    val dateFormat: DateFormat = DateFormat.DD_MM_YYYY,
    val language: Language = Language.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
