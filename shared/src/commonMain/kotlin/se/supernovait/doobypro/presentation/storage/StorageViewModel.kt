package se.supernovait.doobypro.presentation.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Storage_error_delete_failed
import doobypro.shared.generated.resources.screen_Storage_error_save_failed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.domain.util.LogTags

class StorageViewModel(
    private val storageLocationRepository: StorageLocationRepository,
    private val logger: Logger
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorageState())
    val uiState: StateFlow<StorageState> = _uiState.asStateFlow()

    init {
        logger.info("StorageViewModel initialized", tag = LogTags.STORAGE_VM)
        loadLocations()
    }

    fun onEvent(event: StorageEvent) {
        logger.debug("Handling event: $event", tag = LogTags.STORAGE_VM)
        when (event) {
            StorageEvent.LoadLocations -> loadLocations()
            is StorageEvent.SaveLocation -> saveLocation(event)
            is StorageEvent.EditLocation -> _uiState.update { it.copy(editingLocation = event.location) }
            is StorageEvent.DeleteLocation -> deleteLocation(event.location)
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            logger.debug("Loading storage locations list", tag = LogTags.STORAGE_VM)
            _uiState.update { it.copy(isLoading = true) }
            storageLocationRepository.getActiveLocations().collect { locations ->
                val sortedLocations = locations.sortedWith(
                    compareByDescending<StorageLocation> { it.isDefault }
                        .thenBy { it.label }
                )
                _uiState.update { it.copy(locations = sortedLocations, isLoading = false) }
            }
        }
    }

    private fun saveLocation(event: StorageEvent.SaveLocation) {
        viewModelScope.launch {
            logger.info("Saving storage location '${event.label}'", tag = LogTags.STORAGE_VM)
            _uiState.update { it.copy(isSaving = true) }
            val currentLocation = _uiState.value.editingLocation
            val locationToSave = currentLocation?.copy(
                label = event.label,
                type = event.type,
                capacity = event.capacity
            ) ?: StorageLocation(
                id = SupernovaIdGenerator.generateId(IdType.STORAGE_LOCATION.prefix),
                label = event.label,
                type = event.type,
                capacity = event.capacity
            )

            val result = storageLocationRepository.saveLocation(locationToSave)
            if (result is Result.Success) {
                logger.info("Storage location '${event.label}' saved successfully", tag = LogTags.STORAGE_VM)
                _uiState.update { it.copy(isSaving = false, editingLocation = null) }
            } else {
                logger.error("Failed to save storage location '${event.label}'", tag = LogTags.STORAGE_VM)
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Storage_error_save_failed) }
            }
        }
    }

    private fun deleteLocation(location: StorageLocation) {
        viewModelScope.launch {
            logger.info("Deleting storage location with ID: ${location.id}", tag = LogTags.STORAGE_VM)
            _uiState.update { it.copy(isSaving = true) }
            val result = storageLocationRepository.deleteLocation(location)
            if (result is Result.Success) {
                logger.info("Storage location with ID: ${location.id} deleted successfully", tag = LogTags.STORAGE_VM)
                _uiState.update { it.copy(isSaving = false) }
            } else {
                logger.error("Failed to delete storage location with ID: ${location.id}", tag = LogTags.STORAGE_VM)
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Storage_error_delete_failed) }
            }
        }
    }
}
