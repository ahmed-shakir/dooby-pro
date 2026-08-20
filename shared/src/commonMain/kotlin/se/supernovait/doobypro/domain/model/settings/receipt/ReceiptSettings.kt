package se.supernovait.doobypro.domain.model.settings.receipt

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptSettings(
    val includeCustomerName: Boolean = true,
    val includeOrderTime: Boolean = true,
    val includePaymentMethod: Boolean = true,
    val includeStoreLocation: Boolean = false,
    val paperWidth: PaperWidth = PaperWidth.MM_80
)
