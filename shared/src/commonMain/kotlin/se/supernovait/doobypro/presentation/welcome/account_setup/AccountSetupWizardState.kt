package se.supernovait.doobypro.presentation.welcome.account_setup

import kotlinx.datetime.LocalDate

/**
 * State representation for the Account Setup Wizard.
 *
 * @property currentStep The active step in the wizard (1-4).
 * @property firstName User's first name.
 * @property lastName User's last name.
 * @property birthDate Parsed [LocalDate] of the user's birthdate.
 * @property birthDateString Raw string input for the birthdate.
 * @property email User's personal email address.
 * @property phoneNumber User's personal phone number.
 * @property companyLegalName Official registered name of the business.
 * @property companyDisplayName Public-facing name of the business.
 * @property licenseNumber Trade or regulatory license number.
 * @property companyPhone Main business phone number.
 * @property companyEmail Main business email address.
 * @property streetAddress Physical street address of the business.
 * @property city City where the business is located.
 * @property emirate State or Emirate where the business is located.
 * @property postalCode Zip or postal code for the business address.
 * @property locationNote Additional directions or landmark info.
 * @property isCreatingAccount Flag indicating if the account creation process is in progress.
 */
data class AccountSetupWizardState(
    val currentStep: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: LocalDate? = null,
    val birthDateString: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val companyLegalName: String = "",
    val companyDisplayName: String = "",
    val licenseNumber: String = "",
    val companyPhone: String = "",
    val companyEmail: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val emirate: String = "",
    val postalCode: String = "",
    val locationNote: String = "",
    val isCreatingAccount: Boolean = false
)
