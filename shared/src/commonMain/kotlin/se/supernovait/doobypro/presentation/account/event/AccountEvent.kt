package se.supernovait.doobypro.presentation.account.event

sealed interface AccountEvent {
    data object LoadAccount : AccountEvent

    // User Profile Updates
    data class UpdateFirstName(val value: String) : AccountEvent
    data class UpdateLastName(val value: String) : AccountEvent
    data class UpdateBirthDate(val value: String) : AccountEvent
    data class UpdateEmail(val value: String) : AccountEvent
    data class UpdatePhoneNumber(val value: String) : AccountEvent
    data object SaveUserProfile : AccountEvent

    // Company Profile Updates
    data class UpdateCompanyLegalName(val value: String) : AccountEvent
    data class UpdateCompanyDisplayName(val value: String) : AccountEvent
    data class UpdateCompanyLicenseNumber(val value: String) : AccountEvent
    data class UpdateCompanyEmail(val value: String) : AccountEvent
    data class UpdateCompanyPhone(val value: String) : AccountEvent
    data class UpdateCompanyAddressStreet(val value: String) : AccountEvent
    data class UpdateCompanyAddressCity(val value: String) : AccountEvent
    data class UpdateCompanyAddressSubdivision(val value: String) : AccountEvent
    data class UpdateCompanyAddressPostalCode(val value: String) : AccountEvent
    data class UpdateCompanyAddressCountry(val value: String) : AccountEvent
    data class UpdateCompanyNotes(val value: String) : AccountEvent
    data object SaveCompanyProfile : AccountEvent

    // Account Actions
    data object DeactivateAccount : AccountEvent

    // UI Events
    data class ToggleAgreementExpansion(val agreementId: String) : AccountEvent
}