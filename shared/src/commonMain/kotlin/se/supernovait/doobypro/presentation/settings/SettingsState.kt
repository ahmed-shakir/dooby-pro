package se.supernovait.doobypro.presentation.settings

import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.settings.Settings

data class SettingsState(
    val settings: Settings = Settings(),
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
