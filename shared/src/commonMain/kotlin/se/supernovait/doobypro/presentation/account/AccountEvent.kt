package se.supernovait.doobypro.presentation.account

import kotlinx.datetime.LocalDate

sealed interface AccountEvent {
    data object LoadAccount : AccountEvent

    // Navigation
    data class SwitchTab(val tab: AccountTab) : AccountEvent
    data class EnterEditMode(val cardId: String) : AccountEvent
    data object ExitEditMode : AccountEvent

    // User Profile Updates
    data class UpdateUserFirstName(val value: String) : AccountEvent
    data class UpdateUserLastName(val value: String) : AccountEvent
    data class UpdateUserBirthDate(val date: LocalDate?) : AccountEvent
    data class UpdateUserEmail(val value: String) : AccountEvent
    data class UpdateUserPhone(val value: String) : AccountEvent
    data class UpdateUserAddressStreet(val value: String) : AccountEvent
    data class UpdateUserAddressCity(val value: String) : AccountEvent
    data class UpdateUserAddressSubdivision(val value: String) : AccountEvent
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
    data class UpdateCompanyLogo(val bytes: ByteArray) : AccountEvent {
        // ByteArray/Array needs explicit equals/hashCode
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as UpdateCompanyLogo
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }
    data object SaveCompanyProfile : AccountEvent

    // Account Actions
    data object SignOut : AccountEvent
    data object DeactivateAccount : AccountEvent

    // UI Events
    data class ToggleAgreementExpansion(val agreementId: String) : AccountEvent
}
