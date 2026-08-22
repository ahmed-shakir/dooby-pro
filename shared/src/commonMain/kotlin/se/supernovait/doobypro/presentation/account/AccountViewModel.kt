package se.supernovait.doobypro.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.extension.toLocalDate
import se.supernovait.app.core.domain.location.Address
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.presentation.account.event.AccountEvent

class AccountViewModel(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountState())
    val uiState = _uiState
        .onStart { loadAccount() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AccountState()
        )

    fun onEvent(event: AccountEvent) {
        when (event) {
            AccountEvent.LoadAccount -> loadAccount()
            is AccountEvent.UpdateFirstName -> _uiState.update { it.copy(editFirstName = event.value) }
            is AccountEvent.UpdateLastName -> _uiState.update { it.copy(editLastName = event.value) }
            is AccountEvent.UpdateBirthDate -> _uiState.update { it.copy(editBirthDate = event.value) }
            is AccountEvent.UpdateEmail -> _uiState.update { it.copy(editEmail = event.value) }
            is AccountEvent.UpdatePhoneNumber -> _uiState.update { it.copy(editPhone = event.value) }
            AccountEvent.SaveUserProfile -> saveUserProfile()
            
            is AccountEvent.UpdateCompanyLegalName -> _uiState.update { it.copy(editCompanyLegalName = event.value) }
            is AccountEvent.UpdateCompanyDisplayName -> _uiState.update { it.copy(editCompanyDisplayName = event.value) }
            is AccountEvent.UpdateCompanyLicenseNumber -> _uiState.update { it.copy(editCompanyLicenseNumber = event.value) }
            is AccountEvent.UpdateCompanyEmail -> _uiState.update { it.copy(editCompanyEmail = event.value) }
            is AccountEvent.UpdateCompanyPhone -> _uiState.update { it.copy(editCompanyPhone = event.value) }
            is AccountEvent.UpdateCompanyAddressStreet -> _uiState.update { it.copy(editCompanyAddressStreet = event.value) }
            is AccountEvent.UpdateCompanyAddressCity -> _uiState.update { it.copy(editCompanyAddressCity = event.value) }
            is AccountEvent.UpdateCompanyAddressSubdivision -> _uiState.update { it.copy(editCompanyAddressSubdivision = event.value) }
            is AccountEvent.UpdateCompanyAddressPostalCode -> _uiState.update { it.copy(editCompanyAddressPostalCode = event.value) }
            is AccountEvent.UpdateCompanyAddressCountry -> _uiState.update { it.copy(editCompanyAddressCountry = event.value) }
            is AccountEvent.UpdateCompanyNotes -> _uiState.update { it.copy(editCompanyNotes = event.value) }
            AccountEvent.SaveCompanyProfile -> saveCompanyProfile()
            
            AccountEvent.DeactivateAccount -> deactivateAccount()
            is AccountEvent.ToggleAgreementExpansion -> toggleAgreement(event.agreementId)
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val userIdResult = authRepository.getCurrentUserId()
            if (userIdResult is Result.Success) {
                val accountResult = accountRepository.getAccountByUserId(userIdResult.data)
                if (accountResult is Result.Success) {
                    val account = accountResult.data
                    _uiState.update { state ->
                        state.copy(
                            account = account,
                            isLoading = false,
                            editFirstName = account.user.firstname,
                            editLastName = account.user.lastname,
                            editBirthDate = account.user.birthdate?.toString() ?: "",
                            editEmail = account.user.email,
                            editPhone = account.user.phoneNumber ?: "",
                            editCompanyLegalName = account.company.legalName,
                            editCompanyDisplayName = account.company.displayName,
                            editCompanyLicenseNumber = account.company.licenseNumber,
                            editCompanyEmail = account.company.email,
                            editCompanyPhone = account.company.phoneNumber,
                            editCompanyAddressStreet = account.company.address?.street ?: "",
                            editCompanyAddressCity = account.company.address?.city ?: "",
                            editCompanyAddressSubdivision = account.company.address?.subdivision ?: "",
                            editCompanyAddressPostalCode = account.company.address?.postalCode ?: "",
                            editCompanyAddressCountry = account.company.address?.country ?: AppDefaults.COUNTRY,
                            editCompanyNotes = account.company.address?.notes ?: ""
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load account details") }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Not authenticated") }
            }
        }
    }

    private fun saveUserProfile() {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val updatedUser = account.user.copy(
                firstname = _uiState.value.editFirstName,
                lastname = _uiState.value.editLastName,
                birthdate = _uiState.value.editBirthDate.toLocalDate(),
                email = _uiState.value.editEmail,
                phoneNumber = _uiState.value.editPhone
            )
            val updatedAccount = account.copy(user = updatedUser)
            val result = accountRepository.saveAccount(updatedAccount)
            if (result is Result.Success) {
                _uiState.update { it.copy(account = updatedAccount, isSaving = false) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = "Failed to save user profile") }
            }
        }
    }

    private fun saveCompanyProfile() {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentAddress = account.company.address
            val updatedCompany = account.company.copy(
                legalName = _uiState.value.editCompanyLegalName,
                displayName = _uiState.value.editCompanyDisplayName,
                licenseNumber = _uiState.value.editCompanyLicenseNumber,
                email = _uiState.value.editCompanyEmail,
                phoneNumber = _uiState.value.editCompanyPhone,
                address = Address(
                    id = currentAddress?.id,
                    street = _uiState.value.editCompanyAddressStreet,
                    city = _uiState.value.editCompanyAddressCity,
                    subdivision = _uiState.value.editCompanyAddressSubdivision,
                    postalCode = _uiState.value.editCompanyAddressPostalCode,
                    country = _uiState.value.editCompanyAddressCountry,
                    notes = _uiState.value.editCompanyNotes,
                    createdAt = currentAddress?.createdAt ?: 0L // Address init handles default if 0 is problematic, but better to use current or now
                )
            )
            val updatedAccount = account.copy(company = updatedCompany)
            val result = accountRepository.saveAccount(updatedAccount)
            if (result is Result.Success) {
                _uiState.update { it.copy(account = updatedAccount, isSaving = false) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = "Failed to save company profile") }
            }
        }
    }

    private fun deactivateAccount() {
        val accountId = _uiState.value.account?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = accountRepository.deleteAccount(accountId)
            if (result is Result.Success) {
                authRepository.signOut()
                // Navigation to Welcome screen will be handled by AppRoot observer
            } else {
                _uiState.update { it.copy(isSaving = false, error = "Failed to delete account") }
            }
        }
    }

    private fun toggleAgreement(id: String) {
        _uiState.update { state ->
            val newSet = if (state.expandedAgreementIds.contains(id)) {
                state.expandedAgreementIds - id
            } else {
                state.expandedAgreementIds + id
            }
            state.copy(expandedAgreementIds = newSet)
        }
    }
}
