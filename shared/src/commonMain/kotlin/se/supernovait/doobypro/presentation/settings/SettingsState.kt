package se.supernovait.doobypro.presentation.settings

import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.printer.DiscoveredPrinter
import se.supernovait.doobypro.domain.model.storage.StorageLocation

data class SettingsState(
    val settings: Settings = Settings(),
    val services: List<Service> = emptyList(),
    val activeStorageLocations: List<StorageLocation> = emptyList(),
    val discoveredPrinters: List<DiscoveredPrinter> = emptyList(),
    val isSearchingPrinters: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
