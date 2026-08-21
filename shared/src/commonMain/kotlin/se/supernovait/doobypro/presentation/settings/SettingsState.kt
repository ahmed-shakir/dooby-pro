package se.supernovait.doobypro.presentation.settings

import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.printer.DiscoveredPrinter

data class SettingsState(
    val settings: Settings = Settings(),
    val services: List<Service> = emptyList(),
    val discoveredPrinters: List<DiscoveredPrinter> = emptyList(),
    val isSearchingPrinters: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
