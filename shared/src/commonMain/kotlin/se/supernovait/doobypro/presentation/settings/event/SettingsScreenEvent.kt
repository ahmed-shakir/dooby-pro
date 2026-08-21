package se.supernovait.doobypro.presentation.settings.event

import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.settings.common.Currency
import se.supernovait.doobypro.domain.model.settings.common.DateFormat
import se.supernovait.doobypro.domain.model.settings.common.Language
import se.supernovait.doobypro.domain.model.settings.common.ThemeMode
import se.supernovait.doobypro.domain.model.settings.printer.ConnectionMethod
import se.supernovait.doobypro.domain.model.settings.receipt.PaperWidth

sealed interface SettingsScreenEvent {
    data object ResetSettings : SettingsScreenEvent
    
    // Common
    data class UpdateCurrency(val currency: Currency) : SettingsScreenEvent
    data class UpdateDateFormat(val dateFormat: DateFormat) : SettingsScreenEvent
    data class UpdateLanguage(val language: Language) : SettingsScreenEvent
    
    // Theme
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsScreenEvent
    
    // Order
    data class UpdateDefaultServiceId(val serviceId: String?) : SettingsScreenEvent
    data class UpdateDefaultDeliveryOption(val option: DeliveryOption) : SettingsScreenEvent
    data class UpdateDefaultDeliveryMethod(val method: DeliveryMethod) : SettingsScreenEvent
    data class UpdateDefaultHandlingTimeDays(val days: Int) : SettingsScreenEvent
    data class UpdateAutoPrintReceipts(val enabled: Boolean) : SettingsScreenEvent
    
    // Receipt
    data class UpdateIncludeCompanyLogo(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeCompanyName(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeCompanyAddress(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeCompanyPhone(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeCompanyEmail(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeCustomerName(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeOrderNumber(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeOrderTime(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeOrderItems(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeOrderTotal(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeOrderNotes(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeDeliveryDate(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeDeliveryOption(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeDeliveryMethod(val include: Boolean) : SettingsScreenEvent
    data class UpdateIncludeTermsAndConditions(val include: Boolean) : SettingsScreenEvent
    data class UpdatePaperWidth(val width: PaperWidth) : SettingsScreenEvent

    // Printer
    data class UpdatePrinterConnectionMethod(val method: ConnectionMethod) : SettingsScreenEvent
    data object DisconnectPrinter : SettingsScreenEvent
    data object SearchPrinters : SettingsScreenEvent
    data class ConnectPrinter(val name: String, val address: String) : SettingsScreenEvent
    
    // Notifications
    data class UpdateNewOrdersNotification(val enabled: Boolean) : SettingsScreenEvent
    data class UpdateReadyOrdersNotification(val enabled: Boolean) : SettingsScreenEvent
    data class UpdateLateOrdersNotification(val enabled: Boolean) : SettingsScreenEvent
    data class UpdateOrderNotPickedUpNotification(val enabled: Boolean) : SettingsScreenEvent
    data class UpdateOrderNotDeliveredNotification(val enabled: Boolean) : SettingsScreenEvent
    data class UpdatePrinterErrorsNotification(val enabled: Boolean) : SettingsScreenEvent
}
