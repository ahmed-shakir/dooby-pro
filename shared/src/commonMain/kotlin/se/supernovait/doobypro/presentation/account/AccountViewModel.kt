package se.supernovait.doobypro.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.label_not_authenticated
import doobypro.shared.generated.resources.screen_Account_agreements_download_success
import doobypro.shared.generated.resources.screen_Account_business_hours_error_update
import doobypro.shared.generated.resources.screen_Account_business_hours_success_update
import doobypro.shared.generated.resources.screen_Account_error_delete_failed
import doobypro.shared.generated.resources.screen_Account_error_load_failed
import doobypro.shared.generated.resources.screen_Account_error_save_company_failed
import doobypro.shared.generated.resources.screen_Account_error_save_user_failed
import doobypro.shared.generated.resources.screen_Account_license_download_success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.location.Address
import se.supernovait.app.core.domain.observability.analytics.AnalyticsTracker
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.observability.performance.PerformanceMonitor
import se.supernovait.app.core.domain.observability.performance.traceAsync
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.model.company.BusinessHours
import se.supernovait.doobypro.domain.model.company.DayHours
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.domain.repository.BusinessHoursRepository
import se.supernovait.doobypro.domain.util.FileStorage
import se.supernovait.doobypro.domain.util.LogTags
import se.supernovait.doobypro.domain.util.PdfGenerator
import se.supernovait.doobypro.domain.util.PdfSection
import kotlin.time.Clock

/**
 * ViewModel for managing account-related operations, including user profile updates,
 * company profile updates, and agreement visibility.
 */
class AccountViewModel(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
    private val performanceMonitor: PerformanceMonitor,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val businessHoursRepository: BusinessHoursRepository,
    private val fileStorage: FileStorage,
    private val pdfGenerator: PdfGenerator
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountState())
    val uiState: StateFlow<AccountState> = _uiState.asStateFlow()

    init {
        logger.info("AccountViewModel initialized", tag = LogTags.ACCOUNT_VM)
        loadAccount()
    }

    /**
     * Handles incoming [AccountEvent]s.
     */
    fun onEvent(event: AccountEvent) {
        logger.debug("Handling event: $event", tag = LogTags.ACCOUNT_VM)
        when (event) {
            AccountEvent.LoadAccount -> loadAccount()
            is AccountEvent.SwitchTab -> {
                analyticsTracker.trackEvent("account_tab_switched", mapOf("tab" to event.tab.name))
                _uiState.update { it.copy(currentTab = event.tab) }
            }
            is AccountEvent.EnterEditMode -> _uiState.update { it.copy(editingCardId = event.cardId) }
            AccountEvent.ExitEditMode -> _uiState.update { it.copy(editingCardId = null) }

            is AccountEvent.UpdateUserFirstName -> _uiState.update { it.copy(editUserFirstName = event.value) }
            is AccountEvent.UpdateUserLastName -> _uiState.update { it.copy(editUserLastName = event.value) }
            is AccountEvent.UpdateUserBirthDate -> _uiState.update { it.copy(editUserBirthDate = event.date) }
            is AccountEvent.UpdateUserEmail -> _uiState.update { it.copy(editUserEmail = event.value) }
            is AccountEvent.UpdateUserPhone -> _uiState.update { it.copy(editUserPhone = event.value) }
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
            is AccountEvent.UpdateCompanyLogo -> updateCompanyLogo(event.bytes)
            AccountEvent.SaveCompanyProfile -> saveCompanyProfile()
            is AccountEvent.UpdateDayHours -> updateDayHours(event.day, event.hours)
            
            AccountEvent.SignOut -> signOut()
            AccountEvent.DeactivateAccount -> deactivateAccount()
            AccountEvent.DownloadAgreementsPdf -> downloadAgreementsPdf()
            AccountEvent.DownloadLicensePdf -> downloadLicensePdf()
            is AccountEvent.ToggleAgreementExpansion -> toggleAgreement(event.agreementId)
            AccountEvent.ClearInfoMessage -> _uiState.update { it.copy(infoMessage = null) }
        }
    }

    private fun loadAccount() {
        logger.info("Loading account data", tag = LogTags.ACCOUNT_VM)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.loadAccount") {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val userIdResult = authRepository.getCurrentUserId()
                if (userIdResult is Result.Success) {
                    val accountResult = accountRepository.getAccountByUserId(userIdResult.data)
                    if (accountResult is Result.Success) {
                        val account = accountResult.data
                        logger.info("Account data loaded successfully for user ID: ${userIdResult.data}", tag = LogTags.ACCOUNT_VM)
                        val businessHours = (businessHoursRepository.getBusinessHours(account.company.id!!) as? Result.Success)?.data ?: BusinessHours(account.company.id!!)
                        _uiState.update { state ->
                            state.copy(
                                account = account,
                                isLoading = false,
                                businessHoursState = state.businessHoursState.copy(businessHours = businessHours),
                                editUserFirstName = account.user.firstname,
                                editUserLastName = account.user.lastname,
                                editUserBirthDate = account.user.birthdate,
                                editUserEmail = account.user.email,
                                editUserPhone = account.user.phoneNumber ?: "",
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
                        logger.error("Failed to load account data for user ID: ${userIdResult.data}", tag = LogTags.ACCOUNT_VM)
                        _uiState.update { it.copy(isLoading = false, error = Res.string.screen_Account_error_load_failed) }
                    }
                } else {
                    logger.warn("Load account failed: User not authenticated", tag = LogTags.ACCOUNT_VM)
                    _uiState.update { it.copy(isLoading = false, error = Res.string.label_not_authenticated) }
                }
            }
        }
    }

    private fun saveUserProfile() {
        val currentAccount = _uiState.value.account ?: return
        val state = _uiState.value
        logger.info("Saving user profile for user ID: ${currentAccount.user.id}", tag = LogTags.ACCOUNT_VM)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.saveUserProfile") {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                val updatedAddress = if (currentAccount.user.address != null || state.editUserAddressStreet.isNotBlank()) {
                    val street = state.editUserAddressStreet.takeIf { it.isNotBlank() } ?: currentAccount.user.address?.street ?: "N/A"
                    val city = state.editUserAddressCity.takeIf { it.isNotBlank() } ?: currentAccount.user.address?.city ?: "N/A"

                    currentAccount.user.address?.copy(
                        street = street,
                        city = city,
                        subdivision = state.editUserAddressSubdivision
                    ) ?: Address(
                        street = street,
                        city = city,
                        subdivision = state.editUserAddressSubdivision,
                        country = AppDefaults.COUNTRY
                    )
                } else null

                val updatedUser = currentAccount.user.copy(
                    firstname = state.editUserFirstName.takeIf { it.isNotBlank() } ?: currentAccount.user.firstname,
                    lastname = state.editUserLastName.takeIf { it.isNotBlank() } ?: currentAccount.user.lastname,
                    birthdate = state.editUserBirthDate,
                    email = state.editUserEmail.takeIf { it.isNotBlank() } ?: currentAccount.user.email,
                    phoneNumber = state.editUserPhone,
                    address = updatedAddress
                )
                val updatedAccount = currentAccount.copy(user = updatedUser)
                val result = accountRepository.saveAccount(updatedAccount)
                if (result is Result.Success) {
                    logger.info("User profile saved successfully", tag = LogTags.ACCOUNT_VM)
                    analyticsTracker.trackEvent("user_profile_updated")
                    _uiState.update { 
                        it.copy(
                            account = updatedAccount, 
                            isSaving = false, 
                            editingCardId = null
                        ) 
                    }
                } else {
                    logger.error("Failed to save user profile", tag = LogTags.ACCOUNT_VM)
                    crashReporter.log("Failed to save user profile for user ${currentAccount.user.id}")
                    _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Account_error_save_user_failed) }
                }
            }
        }
    }

    private fun saveCompanyProfile() {
        val currentAccount = _uiState.value.account ?: return
        val state = _uiState.value
        logger.info("Saving company profile for company ID: ${currentAccount.company.id}", tag = LogTags.ACCOUNT_VM)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.saveCompanyProfile") {
                _uiState.update { it.copy(isSaving = true, error = null) }

                val currentAddress = currentAccount.company.address
                val updatedCompany = currentAccount.company.copy(
                    legalName = state.editCompanyLegalName,
                    displayName = state.editCompanyDisplayName,
                    licenseNumber = state.editCompanyLicenseNumber,
                    email = state.editCompanyEmail,
                    phoneNumber = state.editCompanyPhone,
                    address = Address(
                        id = currentAddress?.id,
                        street = state.editCompanyAddressStreet,
                        city = state.editCompanyAddressCity,
                        subdivision = state.editCompanyAddressSubdivision,
                        postalCode = state.editCompanyAddressPostalCode,
                        country = state.editCompanyAddressCountry,
                        notes = state.editCompanyNotes
                    ),
                    logoUrl = state.editCompanyLogoUrl
                )
                val updatedAccount = currentAccount.copy(company = updatedCompany)
                val result = accountRepository.saveAccount(updatedAccount)
                if (result is Result.Success) {
                    logger.info("Company profile saved successfully", tag = LogTags.ACCOUNT_VM)
                    analyticsTracker.trackEvent("company_profile_updated")
                    _uiState.update { 
                        it.copy(
                            account = updatedAccount, 
                            isSaving = false, 
                            editingCardId = null
                        ) 
                    }
                } else {
                    logger.error("Failed to save company profile", tag = LogTags.ACCOUNT_VM)
                    _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Account_error_save_company_failed) }
                }
            }
        }
    }

    private fun updateCompanyLogo(bytes: ByteArray) {
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.updateCompanyLogo") {
                _uiState.value.account?.company?.let { company ->
                    logger.info("Updating company logo for company ID: ${company.id}", tag = LogTags.ACCOUNT_VM)
                    val logoUrl = fileStorage.saveFile("company_logo_${company.id}.png", bytes)
                    _uiState.update { it.copy(editCompanyLogoUrl = logoUrl) }
                }
            }
        }
    }

    private fun updateDayHours(day: DayOfWeek, hours: DayHours) {
        val companyId = _uiState.value.account?.company?.id ?: return
        logger.info("Updating business hours for $day in company ID: $companyId", tag = LogTags.ACCOUNT_VM)
        
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.updateDayHours") {
                _uiState.update { state ->
                    state.copy(businessHoursState = state.businessHoursState.copy(isSaving = true, error = null))
                }
                
                val result = businessHoursRepository.updateDayHours(companyId, day, hours)
                if (result is Result.Success) {
                    logger.info("Business hours for $day updated successfully", tag = LogTags.ACCOUNT_VM)
                    analyticsTracker.trackEvent("business_hours_updated", mapOf("day" to day.name))
                    val updatedHoursResult = businessHoursRepository.getBusinessHours(companyId)
                    val updatedHours = (updatedHoursResult as? Result.Success)?.data
                    _uiState.update { state ->
                        state.copy(
                            businessHoursState = state.businessHoursState.copy(
                                businessHours = updatedHours ?: state.businessHoursState.businessHours,
                                isSaving = false,
                                successMessage = Res.string.screen_Account_business_hours_success_update
                            )
                        )
                    }
                } else {
                    logger.error("Failed to update business hours for $day", tag = LogTags.ACCOUNT_VM)
                    _uiState.update { state ->
                        state.copy(
                            businessHoursState = state.businessHoursState.copy(
                                isSaving = false,
                                error = Res.string.screen_Account_business_hours_error_update
                            )
                        )
                    }
                }
            }
        }
    }

    private fun signOut() {
        logger.info("User initiated sign out from Account screen", tag = LogTags.ACCOUNT_VM)
        analyticsTracker.trackEvent("sign_out")
        analyticsTracker.setUserId(null)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.signOut") {
                authRepository.signOut()
            }
        }
    }

    private fun deactivateAccount() {
        val accountId = _uiState.value.account?.id ?: return
        logger.warn("Initiating account deactivation for account ID: $accountId", tag = LogTags.ACCOUNT_VM)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.deactivateAccount") {
                _uiState.update { it.copy(isSaving = true, error = null) }
                val result = accountRepository.deleteAccount(accountId)
                if (result is Result.Success) {
                    logger.info("Account ID: $accountId deactivated successfully", tag = LogTags.ACCOUNT_VM)
                    analyticsTracker.trackEvent("account_deactivated")
                    analyticsTracker.setUserId(null)
                    authRepository.signOut()
                } else {
                    logger.error("Failed to deactivate account ID: $accountId", tag = LogTags.ACCOUNT_VM)
                    _uiState.update { it.copy(isSaving = false, error = Res.string.screen_Account_error_delete_failed) }
                }
            }
        }
    }

    private fun toggleAgreement(id: String) {
        logger.debug("Toggling agreement expansion for ID: $id", tag = LogTags.ACCOUNT_VM)
        _uiState.update { state ->
            val newSet = if (state.expandedAgreementIds.contains(id)) {
                state.expandedAgreementIds - id
            } else {
                state.expandedAgreementIds + id
            }
            state.copy(expandedAgreementIds = newSet)
        }
    }

    private fun downloadAgreementsPdf() {
        val account = _uiState.value.account ?: return
        val agreements = account.agreements
        if (agreements.isEmpty()) {
            logger.warn("Cannot download agreements PDF: Agreements list is empty", tag = LogTags.ACCOUNT_VM)
            return
        }

        logger.info("Generating agreements PDF for account ID: ${account.id}", tag = LogTags.ACCOUNT_VM)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.downloadAgreementsPdf") {
                val sections = mutableListOf<PdfSection>()
                sections.add(PdfSection.Header("User Information"))
                sections.add(PdfSection.KeyValue("Name", "${account.user.firstname} ${account.user.lastname}"))
                sections.add(PdfSection.KeyValue("Email", account.user.email))

                sections.add(PdfSection.Header("Company Information"))
                sections.add(PdfSection.KeyValue("Company", account.company.legalName))
                sections.add(PdfSection.KeyValue("License", account.company.licenseNumber))

                sections.add(PdfSection.Header("Agreements"))
                agreements.forEach { agreement ->
                    sections.add(PdfSection.Header("Agreement ID: ${agreement.id}"))
                    sections.add(PdfSection.KeyValue("Status", agreement.status.toString()))
                    sections.add(PdfSection.KeyValue("Issue Date", agreement.issueDate.toString()))
                    agreement.cancellationDate?.let { sections.add(PdfSection.KeyValue("Cancellation Date", it.toString())) }
                    sections.add(PdfSection.KeyValue("Fee", agreement.fee.formatted))
                }

                val path = pdfGenerator.generatePdf(
                    fileName = "agreements_${account.id}.pdf",
                    title = "Dooby Pro Equipment Lease Agreements",
                    sections = sections
                )
                if (path != null) {
                    logger.info("Agreements PDF successfully generated at path: $path", tag = LogTags.ACCOUNT_VM)
                    analyticsTracker.trackEvent("agreements_pdf_downloaded")
                    _uiState.update { it.copy(infoMessage = Res.string.screen_Account_agreements_download_success) }
                } else {
                    logger.error("Failed to generate agreements PDF for account ID: ${account.id}", tag = LogTags.ACCOUNT_VM)
                }
            }
        }
    }

    private fun downloadLicensePdf() {
        val account = _uiState.value.account ?: run {
            logger.warn("Cannot download license PDF: Account is null", tag = LogTags.ACCOUNT_VM)
            return
        }
        val license = account.license ?: run {
            logger.warn("Cannot download license PDF: License is null", tag = LogTags.ACCOUNT_VM)
            return
        }

        logger.info("Generating license PDF for account ID: ${account.id}", tag = LogTags.ACCOUNT_VM)
        viewModelScope.launch {
            performanceMonitor.traceAsync("AccountViewModel.downloadLicensePdf") {
                val sections = mutableListOf<PdfSection>()
                sections.add(PdfSection.Header("User Information"))
                sections.add(PdfSection.KeyValue("Name", "${account.user.firstname} ${account.user.lastname}"))
                sections.add(PdfSection.KeyValue("Email", account.user.email))

                sections.add(PdfSection.Header("Company Information"))
                sections.add(PdfSection.KeyValue("Company", account.company.legalName))
                sections.add(PdfSection.KeyValue("License", account.company.licenseNumber))

                sections.add(PdfSection.Header("License Details"))
                sections.add(PdfSection.KeyValue("License ID", license.id))
                sections.add(PdfSection.KeyValue("Tier", license.tier.toString()))
                sections.add(PdfSection.KeyValue("Status", license.licenseStatus.toString()))
                sections.add(PdfSection.KeyValue("Issue Date", license.issueDate.toString()))
                sections.add(PdfSection.KeyValue("Expiry Date", license.expiryDate.toString()))

                sections.add(PdfSection.Header("Description"))
                sections.add(PdfSection.Text(license.description))

                val timestamp = Clock.System.now().toEpochMilliseconds()
                val path = pdfGenerator.generatePdf(
                    fileName = "license_${account.id}_$timestamp.pdf",
                    title = license.title,
                    sections = sections
                )
                if (path != null) {
                    logger.info("License PDF successfully generated at path: $path", tag = LogTags.ACCOUNT_VM)
                    analyticsTracker.trackEvent("license_pdf_downloaded")
                    _uiState.update { it.copy(infoMessage = Res.string.screen_Account_license_download_success) }
                } else {
                    logger.error("Failed to generate license PDF for account ID: ${account.id}", tag = LogTags.ACCOUNT_VM)
                }
            }
        }
    }
}
