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
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.StorageLocationRepository

class StorageViewModel(
    private val storageLocationRepository: StorageLocationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StorageState())
    val uiState: StateFlow<StorageState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    fun onEvent(event: StorageEvent) {
        when (event) {
            StorageEvent.LoadLocations -> loadLocations()
            is StorageEvent.SaveLocation -> saveLocation(event)
            is StorageEvent.EditLocation -> _uiState.update { it.copy(editingLocation = event.location) }
            is StorageEvent.DeleteLocation -> deleteLocation(event.location)
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
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
                _uiState.update { it.copy(isSaving = false, editingLocation = null) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Storage_error_save_failed) }
            }
        }
    }

    private fun deleteLocation(location: StorageLocation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = storageLocationRepository.deleteLocation(location)
            if (result is Result.Success) {
                _uiState.update { it.copy(isSaving = false) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Storage_error_delete_failed) }
            }
        }
    }
}
