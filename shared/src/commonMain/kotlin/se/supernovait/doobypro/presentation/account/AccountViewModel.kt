package se.supernovait.doobypro.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_not_authenticated
import doobypro.shared.generated.resources.screen_Account_error_delete_failed
import doobypro.shared.generated.resources.screen_Account_error_load_failed
import doobypro.shared.generated.resources.screen_Account_error_save_company_failed
import doobypro.shared.generated.resources.screen_Account_error_save_user_failed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.location.Address
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.repository.AccountRepository

/**
 * ViewModel for managing account-related operations, including user profile updates,
 * company profile updates, and agreement visibility.
 */
class AccountViewModel(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountState())
    val uiState: StateFlow<AccountState> = _uiState.asStateFlow()

    init {
        loadAccount()
    }

    /**
     * Handles incoming [AccountEvent]s.
     */
    fun onEvent(event: AccountEvent) {
        when (event) {
            AccountEvent.LoadAccount -> loadAccount()
            is AccountEvent.SwitchTab -> _uiState.update { it.copy(currentTab = event.tab) }
            is AccountEvent.EnterEditMode -> _uiState.update { it.copy(editingCardId = event.cardId) }
            AccountEvent.ExitEditMode -> _uiState.update { it.copy(editingCardId = null) }

            is AccountEvent.UpdateFirstName -> _uiState.update { it.copy(editUserFirstName = event.value) }
            is AccountEvent.UpdateLastName -> _uiState.update { it.copy(editUserLastName = event.value) }
            is AccountEvent.UpdateBirthDate -> _uiState.update { it.copy(editUserBirthDate = event.date) }
            is AccountEvent.UpdateEmail -> _uiState.update { it.copy(editUserEmail = event.value) }
            is AccountEvent.UpdatePhoneNumber -> _uiState.update { it.copy(editUserPhone = event.value) }
            is AccountEvent.UpdateProfileImage -> _uiState.update { it.copy(editUserProfileImageUrl = event.url) }
            
            is AccountEvent.UpdateUserAddressStreet -> _uiState.update { it.copy(editUserAddressStreet = event.value) }
            is AccountEvent.UpdateUserAddressCity -> _uiState.update { it.copy(editUserAddressCity = event.value) }
            is AccountEvent.UpdateUserAddressSubdivision -> _uiState.update { it.copy(editUserAddressSubdivision = event.value) }
            
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
            is AccountEvent.UpdateCompanyLogo -> _uiState.update { it.copy(editCompanyLogoUrl = event.url) }
            AccountEvent.SaveCompanyProfile -> saveCompanyProfile()
            
            AccountEvent.SignOut -> signOut()
            AccountEvent.DeactivateAccount -> deactivateAccount()
            is AccountEvent.ToggleAgreementExpansion -> toggleAgreement(event.agreementId)
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val userIdResult = authRepository.getCurrentUserId()
            if (userIdResult is Result.Success) {
                val accountResult = accountRepository.getAccountByUserId(userIdResult.data)
                if (accountResult is Result.Success) {
                    val account = accountResult.data
                    _uiState.update { state ->
                        state.copy(
                            account = account,
                            isLoading = false,
                            editUserFirstName = account.user.firstname,
                            editUserLastName = account.user.lastname,
                            editUserBirthDate = account.user.birthdate,
                            editUserEmail = account.user.email,
                            editUserPhone = account.user.phoneNumber ?: "",
                            editUserProfileImageUrl = account.user.profileImage,
                            editUserAddressStreet = account.user.address?.street ?: "",
                            editUserAddressCity = account.user.address?.city ?: "",
                            editUserAddressSubdivision = account.user.address?.subdivision ?: "",
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
                            editCompanyNotes = account.company.address?.notes ?: "",
                            editCompanyLogoUrl = account.company.logoUrl,
                            memberSince = account.user.createdAt.toString().substringBefore("T"),
                            registeredSince = account.company.createdAt.toString().substringBefore("T")
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = Res.string.screen_Account_error_load_failed) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = Res.string.label_not_authenticated) }
            }
        }
    }

    private fun saveUserProfile() {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            val updatedAddress = if (account.user.address != null || _uiState.value.editUserAddressStreet.isNotBlank()) {
                account.user.address?.copy(
                    street = _uiState.value.editUserAddressStreet,
                    city = _uiState.value.editUserAddressCity,
                    subdivision = _uiState.value.editUserAddressSubdivision
                ) ?: Address(
                    street = _uiState.value.editUserAddressStreet,
                    city = _uiState.value.editUserAddressCity,
                    subdivision = _uiState.value.editUserAddressSubdivision,
                    country = AppDefaults.COUNTRY
                )
            } else null

            val updatedUser = account.user.copy(
                firstname = _uiState.value.editUserFirstName,
                lastname = _uiState.value.editUserLastName,
                birthdate = _uiState.value.editUserBirthDate,
                email = _uiState.value.editUserEmail,
                phoneNumber = _uiState.value.editUserPhone,
                profileImage = _uiState.value.editUserProfileImageUrl,
                address = updatedAddress
            )
            val updatedAccount = account.copy(user = updatedUser)
            val result = accountRepository.saveAccount(updatedAccount)
            if (result is Result.Success) {
                _uiState.update { 
                    it.copy(
                        account = updatedAccount, 
                        isSaving = false, 
                        editingCardId = null
                    ) 
                }
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Account_error_save_user_failed) }
            }
        }
    }

    private fun saveCompanyProfile() {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
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
                    notes = _uiState.value.editCompanyNotes
                ),
                logoUrl = _uiState.value.editCompanyLogoUrl
            )
            val updatedAccount = account.copy(company = updatedCompany)
            val result = accountRepository.saveAccount(updatedAccount)
            if (result is Result.Success) {
                _uiState.update { 
                    it.copy(
                        account = updatedAccount, 
                        isSaving = false, 
                        editingCardId = null
                    ) 
                }
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Account_error_save_company_failed) }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    private fun deactivateAccount() {
        val accountId = _uiState.value.account?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val result = accountRepository.deleteAccount(accountId)
            if (result is Result.Success) {
                authRepository.signOut()
            } else {
                _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Account_error_delete_failed) }
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
