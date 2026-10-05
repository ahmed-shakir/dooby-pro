package se.supernovait.doobypro.domain.util

/**
 * Constants for logging tags across the app.
 */
object LogTags {
    // ViewModels
    const val WELCOME_VM = "WelcomeVM"
    const val ACCOUNT_SETUP_VM = "AccountSetupVM"
    const val NOTIFICATION_VM = "NotificationVM"
    const val SUPPORT_VM = "SupportVM"
    const val ACCOUNT_VM = "AccountVM"
    const val SETTINGS_VM = "SettingsVM"
    const val ORDER_VM = "OrderVM"
    const val ORDER_DETAILS_VM = "OrderDetailsVM"
    const val SERVICE_VM = "ServiceVM"
    const val STORAGE_VM = "StorageVM"

    // Managers
    const val ORDER_MANAGER = "OrderManager"
    const val ORDER_QUERY_MANAGER = "OrderQueryManager"
    const val STORAGE_LOCATION_MANAGER = "StorageLocationManager"

    // Repositories
    const val ACCOUNT_REPO = "AccountRepo"
    const val AGREEMENT_REPO = "AgreementRepo"
    const val AUTH_REPO = "AuthRepo"
    const val BUSINESS_HOURS_REPO = "BusinessHoursRepo"
    const val COMPANY_REPO = "CompanyRepo"
    const val CUSTOMER_REPO = "CustomerRepo"
    const val LICENSE_REPO = "LicenseRepo"
    const val ORDER_REPO = "OrderRepo"
    const val SERVICE_REPO = "ServiceRepo"
    const val SETTINGS_REPO = "SettingsRepo"
    const val STORAGE_LOCATION_REPO = "StorageLocationRepo"

    // Workers & Services
    const val ORDER_ALERT_WORKER = "OrderAlertWorker"

    // Navigation
    const val NAVIGATION = "Navigation"
}