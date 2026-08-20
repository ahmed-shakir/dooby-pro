package se.supernovait.doobypro.presentation.settings.event

sealed interface SettingsNavigationEvent {
    data object NavigateToCommon : SettingsNavigationEvent
    data object NavigateToOrder : SettingsNavigationEvent
    data object NavigateToReceipt : SettingsNavigationEvent
    data object NavigateToPrinter : SettingsNavigationEvent
    data object NavigateToNotifications : SettingsNavigationEvent
}
