package se.supernovait.doobypro.presentation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Service_error_delete_failed
import doobypro.shared.generated.resources.screen_Service_error_save_failed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository

class ServiceViewModel(
    private val serviceRepository: ServiceRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ServiceState())
    val uiState: StateFlow<ServiceState> = _uiState.asStateFlow()

    init {
        loadServices()
        observeSettings()
    }

    fun onEvent(event: ServiceEvent) {
        when (event) {
            ServiceEvent.LoadServices -> loadServices()
            is ServiceEvent.EditService -> _uiState.update { it.copy(editingService = event.service) }
            is ServiceEvent.SaveService -> saveService(event)
            is ServiceEvent.DeleteService -> deleteService(event.service)
        }
    }

    private fun loadServices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            serviceRepository.getServices().collect { services ->
                _uiState.update { it.copy(services = services.sortedBy { s -> s.title }, isLoading = false) }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(currency = settings.common.currency.code) }
            }
        }
    }

    private fun saveService(event: ServiceEvent.SaveService) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currency = _uiState.value.currency
            val currentService = _uiState.value.editingService
            val serviceToSave = currentService?.copy(
                title = event.title,
                description = event.description,
                price = Amount(event.priceValue, currency)
            ) ?: Service(
                id = SupernovaIdGenerator.generateId(IdType.SERVICE.prefix),
                title = event.title,
                description = event.description,
                price = Amount(event.priceValue, currency)
            )

            val result = serviceRepository.saveService(serviceToSave)
            if (result is Result.Success) {
                _uiState.update { it.copy(isSaving = false, editingService = null) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Service_error_save_failed) }
            }
        }
    }

    private fun deleteService(service: Service) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = serviceRepository.deleteService(service)
            if (result is Result.Success) {
                _uiState.update { it.copy(isSaving = false) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Service_error_delete_failed) }
            }
        }
    }
}
