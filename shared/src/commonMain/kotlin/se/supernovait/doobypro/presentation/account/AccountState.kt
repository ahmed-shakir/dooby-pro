package se.supernovait.doobypro.presentation.account

import se.supernovait.doobypro.domain.model.Account

data class AccountState(
    val account: Account? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    
    // Form states (editable copies)
    val editFirstName: String = "",
    val editLastName: String = "",
    val editBirthDate: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    
    val editCompanyLegalName: String = "",
    val editCompanyDisplayName: String = "",
    val editCompanyLicenseNumber: String = "",
    val editCompanyEmail: String = "",
    val editCompanyPhone: String = "",
    val editCompanyAddressStreet: String = "",
    val editCompanyAddressCity: String = "",
    val editCompanyAddressSubdivision: String = "",
    val editCompanyAddressPostalCode: String = "",
    val editCompanyAddressCountry: String = "",
    val editCompanyNotes: String = "",

    // UI state
    val expandedAgreementIds: Set<String> = emptySet()
)
