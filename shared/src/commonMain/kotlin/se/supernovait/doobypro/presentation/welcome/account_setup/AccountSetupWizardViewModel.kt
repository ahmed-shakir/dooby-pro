package se.supernovait.doobypro.presentation.welcome.account_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.domain.extension.toLocalDate
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for the Account Setup Wizard.
 * Manages the multi-step form state and handles user input events.
 *
 * The wizard consists of 4 steps:
 * 1. User Profile (personal details)
 * 2. Company Information (business basics)
 * 3. Business Address (location details)
 * 4. Review & Confirm (summary and submission)
 */
class AccountSetupWizardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AccountSetupWizardState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<AppEvent>()
    val events = _events.receiveAsFlow()

    /**
     * Entry point for all user actions in the wizard.
     * @param event The [AccountSetupWizardEvent] to process.
     */
    fun onEvent(event: AccountSetupWizardEvent) {
        when (event) {
            AccountSetupWizardEvent.OnNextClick -> onNextStep()
            AccountSetupWizardEvent.OnBackClick -> onBackStep()
            is AccountSetupWizardEvent.UpdateFirstName -> updateFirstName(event.value)
            is AccountSetupWizardEvent.UpdateLastName -> updateLastName(event.value)
            is AccountSetupWizardEvent.UpdateBirthDate -> updateBirthDate(event.value)
            is AccountSetupWizardEvent.UpdateEmail -> updateEmail(event.value)
            is AccountSetupWizardEvent.UpdatePhoneNumber -> updatePhoneNumber(event.value)
            is AccountSetupWizardEvent.UpdateCompanyLegalName -> updateCompanyLegalName(event.value)
            is AccountSetupWizardEvent.UpdateCompanyDisplayName -> updateCompanyDisplayName(event.value)
            is AccountSetupWizardEvent.UpdateLicenseNumber -> updateLicenseNumber(event.value)
            is AccountSetupWizardEvent.UpdateCompanyPhone -> updateCompanyPhone(event.value)
            is AccountSetupWizardEvent.UpdateCompanyEmail -> updateCompanyEmail(event.value)
            is AccountSetupWizardEvent.UpdateStreetAddress -> updateStreetAddress(event.value)
            is AccountSetupWizardEvent.UpdateCity -> updateCity(event.value)
            is AccountSetupWizardEvent.UpdateEmirate -> updateEmirate(event.value)
            is AccountSetupWizardEvent.UpdatePostalCode -> updatePostalCode(event.value)
            is AccountSetupWizardEvent.UpdateLocationNote -> updateLocationNote(event.value)
        }
    }

    private fun onNextStep() {
        if (_uiState.value.currentStep < 4) {
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
    private fun updateBirthDate(value: String) {
        val date = value.toLocalDate()
        _uiState.value = _uiState.value.copy(birthDate = date, birthDateString = value)
    }
    private fun updateEmail(value: String) { _uiState.value = _uiState.value.copy(email = value) }
    private fun updatePhoneNumber(value: String) { _uiState.value = _uiState.value.copy(phoneNumber = value) }

    private fun updateCompanyLegalName(value: String) { _uiState.value = _uiState.value.copy(companyLegalName = value) }
    private fun updateCompanyDisplayName(value: String) { _uiState.value = _uiState.value.copy(companyDisplayName = value) }
    private fun updateLicenseNumber(value: String) { _uiState.value = _uiState.value.copy(licenseNumber = value) }
    private fun updateCompanyPhone(value: String) { _uiState.value = _uiState.value.copy(companyPhone = value) }
    private fun updateCompanyEmail(value: String) { _uiState.value = _uiState.value.copy(companyEmail = value) }

    private fun updateStreetAddress(value: String) { _uiState.value = _uiState.value.copy(streetAddress = value) }
    private fun updateCity(value: String) { _uiState.value = _uiState.value.copy(city = value) }
    private fun updateEmirate(value: String) { _uiState.value = _uiState.value.copy(emirate = value) }
    private fun updatePostalCode(value: String) { _uiState.value = _uiState.value.copy(postalCode = value) }
    private fun updateLocationNote(value: String) { _uiState.value = _uiState.value.copy(locationNote = value) }

    private fun createAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingAccount = true)
            // Simulate account creation
            delay(1500.milliseconds)

            // TODO: add call to accountRepository to create

            _uiState.value = _uiState.value.copy(isCreatingAccount = false)

            /*TODO: viewModelScope.launch {
                val result = authRepository.signIn(username)
                if (result.isSuccess) {
                    _events.send(AppEvent.SignIn)
                } else {
                    _events.send(AppEvent.Error(NetworkError.UNAUTHORIZED))
                }
            }*/
        }
    }
}
