package se.supernovait.doobypro.domain.model.settings.common

enum class Currency(val code: String, val symbol: String, val label: String) {
    AED("AED", "AED", "United Arab Emirates Dirham"),
    USD("USD", "$", "US Dollar"),
    EUR("EUR", "€", "Euro"),
    GBP("GBP", "£", "British Pound")
}
