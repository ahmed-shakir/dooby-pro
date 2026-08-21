package se.supernovait.doobypro.domain.model.settings.receipt

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptSettings(
    val includeCompanyLogo: Boolean = true,
    val includeCompanyName: Boolean = true,
    val includeCompanyAddress: Boolean = false,
    val includeCompanyPhone: Boolean = true,
    val includeCompanyEmail: Boolean = false,
    val includeCustomerName: Boolean = true,
    val includeOrderNumber: Boolean = true,
    val includeOrderTime: Boolean = true,
    val includeOrderItems: Boolean = true,
    val includeOrderTotal: Boolean = true,
    val includeOrderNotes: Boolean = false,
    val includeDeliveryDate: Boolean = true,
    val includeDeliveryOption: Boolean = false,
    val includeDeliveryMethod: Boolean = false,
    val includeTermsAndConditions: Boolean = false,
    val paperWidth: PaperWidth = PaperWidth.MM_80
)
