package se.supernovait.doobypro.presentation.welcome.account_setup

/**
 * User actions and input events for the Account Setup Wizard.
 */
sealed interface AccountSetupWizardEvent {
    /** Navigates to the next step or triggers account creation. */
    data object OnNextClick : AccountSetupWizardEvent
    /** Navigates to the previous step. */
    data object OnBackClick : AccountSetupWizardEvent

    /** Updates the user's first name. */
    data class UpdateFirstName(val value: String) : AccountSetupWizardEvent
    /** Updates the user's last name. */
    data class UpdateLastName(val value: String) : AccountSetupWizardEvent
    /** Updates the user's username. */
    data class UpdateUsername(val value: String) : AccountSetupWizardEvent
    /** Updates the user's birthdate string. */
    data class UpdateBirthDate(val value: String) : AccountSetupWizardEvent
    /** Updates the user's email address. */
    data class UpdateEmail(val value: String) : AccountSetupWizardEvent
    /** Updates the user's phone number. */
    data class UpdatePhoneNumber(val value: String) : AccountSetupWizardEvent

    /** Updates the company's legal name. */
    data class UpdateCompanyLegalName(val value: String) : AccountSetupWizardEvent
    /** Updates the company's display name. */
    data class UpdateCompanyDisplayName(val value: String) : AccountSetupWizardEvent
    /** Updates the company's license number. */
    data class UpdateLicenseNumber(val value: String) : AccountSetupWizardEvent
    /** Updates the company's phone number. */
    data class UpdateCompanyPhone(val value: String) : AccountSetupWizardEvent
    /** Updates the company's email address. */
    data class UpdateCompanyEmail(val value: String) : AccountSetupWizardEvent

    /** Updates the business street address. */
    data class UpdateStreetAddress(val value: String) : AccountSetupWizardEvent
    /** Updates the business city. */
    data class UpdateCity(val value: String) : AccountSetupWizardEvent
    /** Updates the business subdivision (emirate/state). */
    data class UpdateSubdivision(val value: String) : AccountSetupWizardEvent
    /** Updates the business postal code. */
    data class UpdatePostalCode(val value: String) : AccountSetupWizardEvent
    /** Updates the business country. */
    data class UpdateCountry(val value: String) : AccountSetupWizardEvent
    /** Updates the business location notes. */
    data class UpdateNotes(val value: String) : AccountSetupWizardEvent
}
