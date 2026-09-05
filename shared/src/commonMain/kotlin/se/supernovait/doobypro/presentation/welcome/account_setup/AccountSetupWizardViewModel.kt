package se.supernovait.doobypro.presentation.welcome.account_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.error.AuthError
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.domain.location.Address
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.repository.AccountRepository

/**
 * ViewModel for the Account Setup Wizard.
 * Manages the multi-step form state and handles user input events.
 */
class AccountSetupWizardViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountSetupWizardState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AppEvent>()
    val events = _events.receiveAsFlow()

    fun onEvent(event: AccountSetupWizardEvent) {
        when (event) {
            AccountSetupWizardEvent.OnNextClick -> onNextStep()
            AccountSetupWizardEvent.OnBackClick -> onBackStep()
            is AccountSetupWizardEvent.UpdateFirstName -> updateFirstName(event.value)
            is AccountSetupWizardEvent.UpdateLastName -> updateLastName(event.value)
            is AccountSetupWizardEvent.UpdateUsername -> updateUsername(event.value)
            is AccountSetupWizardEvent.UpdateBirthDate -> updateBirthDate(event.date)
            is AccountSetupWizardEvent.UpdateEmail -> updateEmail(event.value)
            is AccountSetupWizardEvent.UpdatePhoneNumber -> updatePhoneNumber(event.value)
            is AccountSetupWizardEvent.UpdateCompanyLegalName -> updateCompanyLegalName(event.value)
            is AccountSetupWizardEvent.UpdateCompanyDisplayName -> updateCompanyDisplayName(event.value)
            is AccountSetupWizardEvent.UpdateLicenseNumber -> updateLicenseNumber(event.value)
            is AccountSetupWizardEvent.UpdateCompanyPhone -> updateCompanyPhone(event.value)
            is AccountSetupWizardEvent.UpdateCompanyEmail -> updateCompanyEmail(event.value)
            is AccountSetupWizardEvent.UpdateStreetAddress -> updateStreetAddress(event.value)
            is AccountSetupWizardEvent.UpdateCity -> updateCity(event.value)
            is AccountSetupWizardEvent.UpdateSubdivision -> updateSubdivision(event.value)
            is AccountSetupWizardEvent.UpdatePostalCode -> updatePostalCode(event.value)
            is AccountSetupWizardEvent.UpdateCountry -> updateCountry(event.value)
            is AccountSetupWizardEvent.UpdateNotes -> updateNotes(event.value)
        }
    }

    private fun onNextStep() {
        if (_uiState.value.currentStep < 4) {
            _uiState.update { currentState ->
                currentState.copy(
                    phoneNumber = if (currentState.phoneNumber.isNotBlank()) formatPhoneNumber(currentState.phoneNumber) else "",
                    companyPhone = if (currentState.companyPhone.isNotBlank()) formatPhoneNumber(currentState.companyPhone) else ""
                )
            }

            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
        } else if (_uiState.value.currentStep == 4) {
            createAccount()
        }
    }

    private fun onBackStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    private fun updateFirstName(value: String) { _uiState.value = _uiState.value.copy(firstName = value) }
    private fun updateLastName(value: String) { _uiState.value = _uiState.value.copy(lastName = value) }
    private fun updateUsername(value: String) { _uiState.value = _uiState.value.copy(username = value) }
    private fun updateBirthDate(date: LocalDate?) { _uiState.value = _uiState.value.copy(birthDate = date) }
    private fun updateEmail(value: String) { _uiState.value = _uiState.value.copy(email = value) }
    private fun updatePhoneNumber(value: String) { _uiState.value = _uiState.value.copy(phoneNumber = value) }

    private fun updateCompanyLegalName(value: String) { _uiState.value = _uiState.value.copy(companyLegalName = value) }
    private fun updateCompanyDisplayName(value: String) { _uiState.value = _uiState.value.copy(companyDisplayName = value) }
    private fun updateLicenseNumber(value: String) { _uiState.value = _uiState.value.copy(licenseNumber = value) }
    private fun updateCompanyPhone(value: String) { _uiState.value = _uiState.value.copy(companyPhone = value) }
    private fun updateCompanyEmail(value: String) { _uiState.value = _uiState.value.copy(companyEmail = value) }

    private fun updateStreetAddress(value: String) { _uiState.value = _uiState.value.copy(streetAddress = value) }
    private fun updateCity(value: String) { _uiState.value = _uiState.value.copy(city = value) }
    private fun updateSubdivision(value: String) { _uiState.value = _uiState.value.copy(subdivision = value) }
    private fun updatePostalCode(value: String) { _uiState.value = _uiState.value.copy(postalCode = value) }
    private fun updateCountry(value: String) { _uiState.value = _uiState.value.copy(country = value) }
    private fun updateNotes(value: String) { _uiState.value = _uiState.value.copy(notes = value) }

    private fun createAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingAccount = true)
            
            val state = _uiState.value
            val account = Account(
                user = User(
                    username = state.username,
                    firstname = state.firstName,
                    lastname = state.lastName,
                    birthdate = state.birthDate,
                    email = state.email,
                    phoneNumber = state.phoneNumber
                ),
                company = Company(
                    legalName = state.companyLegalName,
                    displayName = state.companyDisplayName,
                    licenseNumber = state.licenseNumber,
                    phoneNumber = state.companyPhone,
                    email = state.companyEmail,
                    address = Address(
                        street = state.streetAddress,
                        city = state.city,
                        subdivision = state.subdivision,
                        postalCode = state.postalCode.ifBlank { null },
                        country = state.country,
                        notes = state.notes.ifBlank { null }
                    )
                )
            )
            
            val result = accountRepository.saveAccount(account)
            if (result.isSuccess) {
                _events.send(AppEvent.SignIn)
            } else {
                _events.send(AppEvent.Failure(AuthError.UNKNOWN))
            }

            _uiState.value = _uiState.value.copy(isCreatingAccount = false)
        }
    }

    /**
     * Formats the phone number by prepending the country code if it's missing.
     * Also handles sanitization of common input patterns.
     */
    private fun formatPhoneNumber(value: String): String {
        if (value.isBlank()) return ""

        // If it already has a plus, assume it's already formatted or the user is typing a full number
        if (value.startsWith("+")) return value

        val digits = value.filter { it.isDigit() }
        if (digits.isEmpty()) return ""

        val countryDigits = AppDefaults.COUNTRY_CODE.filter { it.isDigit() }

        // If the user typed the country code without the plus, just add the plus
        if (digits.startsWith(countryDigits)) {
            return "+$digits"
        }

        // Handle local format (remove leading zero if present before adding country code)
        val localNumber = if (digits.startsWith("0")) digits.drop(1) else digits
        return "${AppDefaults.COUNTRY_CODE}$localNumber"
    }
}
