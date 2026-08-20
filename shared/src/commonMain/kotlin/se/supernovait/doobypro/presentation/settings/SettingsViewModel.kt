package se.supernovait.doobypro.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsState> = combine(
        settingsRepository.settings,
        serviceRepository.getServices(),
        _isLoading,
        _error
    ) { settings, services, isLoading, error ->
        SettingsState(
            settings = settings,
            services = services,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsState(isLoading = true)
    )

    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            SettingsScreenEvent.ResetSettings -> resetSettings()
            is SettingsScreenEvent.UpdateCurrency -> updateSettings { it.copy(common = it.common.copy(currency = event.currency)) }
            is SettingsScreenEvent.UpdateDateFormat -> updateSettings { it.copy(common = it.common.copy(dateFormat = event.dateFormat)) }
            is SettingsScreenEvent.UpdateLanguage -> updateSettings { it.copy(common = it.common.copy(language = event.language)) }
            is SettingsScreenEvent.UpdateThemeMode -> updateSettings { it.copy(common = it.common.copy(themeMode = event.themeMode)) }
            is SettingsScreenEvent.UpdateDefaultServiceId -> updateSettings { it.copy(order = it.order.copy(defaultServiceId = event.serviceId)) }
            is SettingsScreenEvent.UpdateDefaultDeliveryOption -> updateSettings { it.copy(order = it.order.copy(defaultDeliveryOption = event.option)) }
            is SettingsScreenEvent.UpdateDefaultDeliveryMethod -> updateSettings { it.copy(order = it.order.copy(defaultDeliveryMethod = event.method)) }
            is SettingsScreenEvent.UpdateDefaultHandlingTimeDays -> updateSettings { it.copy(order = it.order.copy(defaultHandlingTimeDays = event.days)) }
            is SettingsScreenEvent.UpdateAutoPrintReceipts -> updateSettings { it.copy(order = it.order.copy(autoPrintReceipts = event.enabled)) }
            is SettingsScreenEvent.UpdateIncludeCustomerName -> updateSettings { it.copy(receipt = it.receipt.copy(includeCustomerName = event.include)) }
            is SettingsScreenEvent.UpdateIncludeOrderTime -> updateSettings { it.copy(receipt = it.receipt.copy(includeOrderTime = event.include)) }
            is SettingsScreenEvent.UpdateIncludePaymentMethod -> updateSettings { it.copy(receipt = it.receipt.copy(includePaymentMethod = event.include)) }
            is SettingsScreenEvent.UpdateIncludeStoreLocation -> updateSettings { it.copy(receipt = it.receipt.copy(includeStoreLocation = event.include)) }
            is SettingsScreenEvent.UpdatePaperWidth -> updateSettings { it.copy(receipt = it.receipt.copy(paperWidth = event.width)) }
            is SettingsScreenEvent.UpdatePrinterConnectionMethod -> updateSettings { it.copy(printer = it.printer.copy(connectionMethod = event.method)) }
            is SettingsScreenEvent.UpdatePrinterIp -> updateSettings { it.copy(printer = it.printer.copy(printerIp = event.ip)) }
            is SettingsScreenEvent.UpdatePrinterName -> updateSettings { it.copy(printer = it.printer.copy(printerName = event.name)) }
            is SettingsScreenEvent.UpdateNewOrdersNotification -> updateSettings { it.copy(notifications = it.notifications.copy(newOrders = event.enabled)) }
            is SettingsScreenEvent.UpdateOrderReadyNotification -> updateSettings { it.copy(notifications = it.notifications.copy(orderReady = event.enabled)) }
            is SettingsScreenEvent.UpdateDeliveryUpdatesNotification -> updateSettings { it.copy(notifications = it.notifications.copy(deliveryUpdates = event.enabled)) }
            is SettingsScreenEvent.UpdatePrinterErrorsNotification -> updateSettings { it.copy(notifications = it.notifications.copy(printerErrors = event.enabled)) }
            is SettingsScreenEvent.UpdatePaymentFailuresNotification -> updateSettings { it.copy(notifications = it.notifications.copy(paymentFailures = event.enabled)) }
        }
    }

    private fun updateSettings(transform: (Settings) -> Settings) {
        viewModelScope.launch {
            val currentSettings = uiState.value.settings
            val newSettings = transform(currentSettings)
            settingsRepository.updateSettings(newSettings)
        }
    }

    private fun resetSettings() {
        viewModelScope.launch {
            settingsRepository.resetSettings()
        }
    }
}
