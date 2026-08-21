package se.supernovait.doobypro.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.printer.ConnectionMethod
import se.supernovait.doobypro.domain.model.settings.printer.DiscoveredPrinter
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _isSearchingPrinters = MutableStateFlow(false)
    private val _discoveredPrinters = MutableStateFlow<List<DiscoveredPrinter>>(emptyList())
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsState> = combine(
        settingsRepository.settings,
        serviceRepository.getServices(),
        _isSearchingPrinters,
        _discoveredPrinters,
        _isLoading,
        _error
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        SettingsState(
            settings = args[0] as Settings,
            services = args[1] as List<Service>,
            isSearchingPrinters = args[2] as Boolean,
            discoveredPrinters = args[3] as List<DiscoveredPrinter>,
            isLoading = args[4] as Boolean,
            error = args[5] as String?
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsState(isLoading = true)
    )

    fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            SettingsScreenEvent.ResetSettings -> resetSettings()
            // Common settings
            is SettingsScreenEvent.UpdateCurrency -> updateSettings { it.copy(common = it.common.copy(currency = event.currency)) }
            is SettingsScreenEvent.UpdateDateFormat -> updateSettings { it.copy(common = it.common.copy(dateFormat = event.dateFormat)) }
            is SettingsScreenEvent.UpdateLanguage -> updateSettings { it.copy(common = it.common.copy(language = event.language)) }
            is SettingsScreenEvent.UpdateThemeMode -> updateSettings { it.copy(common = it.common.copy(themeMode = event.themeMode)) }
            // Order settings
            is SettingsScreenEvent.UpdateDefaultServiceId -> updateSettings { it.copy(order = it.order.copy(defaultServiceId = event.serviceId)) }
            is SettingsScreenEvent.UpdateDefaultDeliveryOption -> updateSettings { it.copy(order = it.order.copy(defaultDeliveryOption = event.option)) }
            is SettingsScreenEvent.UpdateDefaultDeliveryMethod -> updateSettings { it.copy(order = it.order.copy(defaultDeliveryMethod = event.method)) }
            is SettingsScreenEvent.UpdateDefaultHandlingTimeDays -> updateSettings { it.copy(order = it.order.copy(defaultHandlingTimeDays = event.days)) }
            is SettingsScreenEvent.UpdateAutoPrintReceipts -> updateSettings { it.copy(order = it.order.copy(autoPrintReceipts = event.enabled)) }
            // Receipt settings
            is SettingsScreenEvent.UpdateIncludeCompanyLogo -> updateSettings { it.copy(receipt = it.receipt.copy(includeCompanyLogo = event.include)) }
            is SettingsScreenEvent.UpdateIncludeCompanyName -> updateSettings { it.copy(receipt = it.receipt.copy(includeCompanyName = event.include)) }
            is SettingsScreenEvent.UpdateIncludeCompanyAddress -> updateSettings { it.copy(receipt = it.receipt.copy(includeCompanyAddress = event.include)) }
            is SettingsScreenEvent.UpdateIncludeCompanyPhone -> updateSettings { it.copy(receipt = it.receipt.copy(includeCompanyPhone = event.include)) }
            is SettingsScreenEvent.UpdateIncludeCompanyEmail -> updateSettings { it.copy(receipt = it.receipt.copy(includeCompanyEmail = event.include)) }
            is SettingsScreenEvent.UpdateIncludeCustomerName -> updateSettings { it.copy(receipt = it.receipt.copy(includeCustomerName = event.include)) }
            is SettingsScreenEvent.UpdateIncludeOrderNumber -> updateSettings { it.copy(receipt = it.receipt.copy(includeOrderNumber = event.include)) }
            is SettingsScreenEvent.UpdateIncludeOrderTime -> updateSettings { it.copy(receipt = it.receipt.copy(includeOrderTime = event.include)) }
            is SettingsScreenEvent.UpdateIncludeOrderItems -> updateSettings { it.copy(receipt = it.receipt.copy(includeOrderItems = event.include)) }
            is SettingsScreenEvent.UpdateIncludeOrderTotal -> updateSettings { it.copy(receipt = it.receipt.copy(includeOrderTotal = event.include)) }
            is SettingsScreenEvent.UpdateIncludeOrderNotes -> updateSettings { it.copy(receipt = it.receipt.copy(includeOrderNotes = event.include)) }
            is SettingsScreenEvent.UpdateIncludeDeliveryDate -> updateSettings { it.copy(receipt = it.receipt.copy(includeDeliveryDate = event.include)) }
            is SettingsScreenEvent.UpdateIncludeDeliveryOption -> updateSettings { it.copy(receipt = it.receipt.copy(includeDeliveryOption = event.include)) }
            is SettingsScreenEvent.UpdateIncludeDeliveryMethod -> updateSettings { it.copy(receipt = it.receipt.copy(includeDeliveryMethod = event.include)) }
            is SettingsScreenEvent.UpdateIncludeTermsAndConditions -> updateSettings { it.copy(receipt = it.receipt.copy(includeTermsAndConditions = event.include)) }
            is SettingsScreenEvent.UpdatePaperWidth -> updateSettings { it.copy(receipt = it.receipt.copy(paperWidth = event.width)) }
            // Printer settings
            is SettingsScreenEvent.UpdatePrinterConnectionMethod -> updateSettings { it.copy(printer = it.printer.copy(connectionMethod = event.method)) }
            is SettingsScreenEvent.DisconnectPrinter -> updateSettings { it.copy(printer = it.printer.copy(printerAddress = null, printerName = null)) }
            is SettingsScreenEvent.SearchPrinters -> searchPrinters()
            is SettingsScreenEvent.ConnectPrinter -> updateSettings { it.copy(printer = it.printer.copy(printerAddress = event.address, printerName = event.name)) }
            // Notification settings
            is SettingsScreenEvent.UpdateNewOrdersNotification -> updateSettings { it.copy(notifications = it.notifications.copy(newOrders = event.enabled)) }
            is SettingsScreenEvent.UpdateReadyOrdersNotification -> updateSettings { it.copy(notifications = it.notifications.copy(readyOrders = event.enabled)) }
            is SettingsScreenEvent.UpdateLateOrdersNotification -> updateSettings { it.copy(notifications = it.notifications.copy(lateOrders = event.enabled)) }
            is SettingsScreenEvent.UpdateOrderNotPickedUpNotification -> updateSettings { it.copy(notifications = it.notifications.copy(orderNotPickedUp = event.enabled)) }
            is SettingsScreenEvent.UpdateOrderNotDeliveredNotification -> updateSettings { it.copy(notifications = it.notifications.copy(orderNotDelivered = event.enabled)) }
            is SettingsScreenEvent.UpdatePrinterErrorsNotification -> updateSettings { it.copy(notifications = it.notifications.copy(printerErrors = event.enabled)) }
        }
    }

    private fun searchPrinters() {
        viewModelScope.launch {
            val method = uiState.value.settings.printer.connectionMethod
            _isSearchingPrinters.value = true
            _discoveredPrinters.value = emptyList()

            // TODO: call printer service to search and connect to printer
            _discoveredPrinters.value = if (method == ConnectionMethod.BLUETOOTH) {
                listOf()
            } else {
                listOf()
            }
            _isSearchingPrinters.value = false
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
