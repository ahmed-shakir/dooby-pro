package se.supernovait.doobypro.presentation.welcome.account_setup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import se.supernovait.doobypro.data.repository.FakeAccountRepository
import se.supernovait.doobypro.domain.model.AppDefaults
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AccountSetupWizardViewModelTest {
    private lateinit var viewModel: AccountSetupWizardViewModel
    private lateinit var fakeAccountRepository: FakeAccountRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAccountRepository = FakeAccountRepository()
        viewModel = AccountSetupWizardViewModel(
            accountRepository = fakeAccountRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertEquals(1, state.currentStep)
        assertEquals("", state.firstName)
        assertEquals("", state.username)
        assertEquals(false, state.isCreatingAccount)
    }

    @Test
    fun `OnNextClick increments currentStep`() = runTest(testDispatcher) {
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals(2, viewModel.uiState.value.currentStep)

        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals(3, viewModel.uiState.value.currentStep)

        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals(4, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `OnBackClick decrements currentStep`() = runTest(testDispatcher) {
        // Go to step 2 first
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals(2, viewModel.uiState.value.currentStep)

        viewModel.onEvent(AccountSetupWizardEvent.OnBackClick)
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `UpdateFirstName updates state`() = runTest(testDispatcher) {
        val name = "John"
        viewModel.onEvent(AccountSetupWizardEvent.UpdateFirstName(name))
        assertEquals(name, viewModel.uiState.value.firstName)
    }

    @Test
    fun `UpdateUsername updates state`() = runTest(testDispatcher) {
        val username = "johndoe"
        viewModel.onEvent(AccountSetupWizardEvent.UpdateUsername(username))
        assertEquals(username, viewModel.uiState.value.username)
    }

    @Test
    fun `UpdatePhoneNumber formats number on next step`() = runTest(testDispatcher) {
        val rawNumber = "501234567"
        viewModel.onEvent(AccountSetupWizardEvent.UpdatePhoneNumber(rawNumber))
        // Not formatted yet
        assertEquals(rawNumber, viewModel.uiState.value.phoneNumber)

        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals("${AppDefaults.COUNTRY_CODE}501234567", viewModel.uiState.value.phoneNumber)

        // Reset to step 1 for more checks
        viewModel.onEvent(AccountSetupWizardEvent.OnBackClick)
        
        val withLeadingZero = "0501234567"
        viewModel.onEvent(AccountSetupWizardEvent.UpdatePhoneNumber(withLeadingZero))
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals("${AppDefaults.COUNTRY_CODE}501234567", viewModel.uiState.value.phoneNumber)

        viewModel.onEvent(AccountSetupWizardEvent.OnBackClick)

        val alreadyFormatted = "+971501234567"
        viewModel.onEvent(AccountSetupWizardEvent.UpdatePhoneNumber(alreadyFormatted))
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals(alreadyFormatted, viewModel.uiState.value.phoneNumber)
        
        viewModel.onEvent(AccountSetupWizardEvent.OnBackClick)

        val countryCodeNoPlus = "971501234567"
        viewModel.onEvent(AccountSetupWizardEvent.UpdatePhoneNumber(countryCodeNoPlus))
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals("+971501234567", viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun `UpdateCompanyPhone formats number on next step`() = runTest(testDispatcher) {
        val rawNumber = "41234567"
        // Move to step 2 where company phone is
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        
        viewModel.onEvent(AccountSetupWizardEvent.UpdateCompanyPhone(rawNumber))
        // Not formatted yet
        assertEquals(rawNumber, viewModel.uiState.value.companyPhone)

        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        assertEquals("${AppDefaults.COUNTRY_CODE}$rawNumber", viewModel.uiState.value.companyPhone)
    }

    @Test
    fun `UpdateBirthDate updates both LocalDate and String`() = runTest(testDispatcher) {
        val validDate = LocalDate(1990, 1, 1)
        viewModel.onEvent(AccountSetupWizardEvent.UpdateBirthDate(validDate))
        assertEquals(validDate, viewModel.uiState.value.birthDate)

        val invalidDate = null
        viewModel.onEvent(AccountSetupWizardEvent.UpdateBirthDate(invalidDate))
        assertNull(viewModel.uiState.value.birthDate)
    }

    @Test
    fun `OnNextClick on step 4 triggers account creation`() = runTest(testDispatcher) {
        // Collect events to prevent 'send' from suspending indefinitely
        viewModel.events.onEach { }.launchIn(backgroundScope)

        // Step 1: User Info
        viewModel.onEvent(AccountSetupWizardEvent.UpdateFirstName("John"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateLastName("Doe"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateUsername("johndoe"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateEmail("john@example.com"))
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)

        // Step 2: Company Info
        viewModel.onEvent(AccountSetupWizardEvent.UpdateCompanyLegalName("Legal Name"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateCompanyDisplayName("Display Name"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateLicenseNumber("LIC-123"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateCompanyPhone("123456"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateCompanyEmail("company@example.com"))
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)

        // Step 3: Address Info
        viewModel.onEvent(AccountSetupWizardEvent.UpdateStreetAddress("Main St"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateCity("Dubai"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdateSubdivision("DUBAI"))
        viewModel.onEvent(AccountSetupWizardEvent.UpdatePostalCode("00000"))
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)

        // Step 4: Review
        assertEquals(4, viewModel.uiState.value.currentStep)

        // Trigger Creation
        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        
        // Let all internal coroutines complete
        advanceUntilIdle()
        
        assertEquals(false, viewModel.uiState.value.isCreatingAccount)
    }
}
