package se.supernovait.doobypro.presentation.welcome.account_setup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class AccountSetupWizardViewModelTest {

    private lateinit var viewModel: AccountSetupWizardViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AccountSetupWizardViewModel()
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
    fun `UpdateBirthDate updates both LocalDate and String`() = runTest(testDispatcher) {
        val validDate = "1990-01-01"
        viewModel.onEvent(AccountSetupWizardEvent.UpdateBirthDate(validDate))
        assertEquals(validDate, viewModel.uiState.value.birthDateString)
        assertEquals(LocalDate(1990, 1, 1), viewModel.uiState.value.birthDate)

        val invalidDate = "not-a-date"
        viewModel.onEvent(AccountSetupWizardEvent.UpdateBirthDate(invalidDate))
        assertEquals(invalidDate, viewModel.uiState.value.birthDateString)
        assertNull(viewModel.uiState.value.birthDate)
    }

    @Test
    fun `OnNextClick on step 4 triggers account creation`() = runTest(testDispatcher) {
        // Move to step 4
        repeat(3) { viewModel.onEvent(AccountSetupWizardEvent.OnNextClick) }
        assertEquals(4, viewModel.uiState.value.currentStep)

        viewModel.onEvent(AccountSetupWizardEvent.OnNextClick)
        
        runCurrent()
        assertTrue(viewModel.uiState.value.isCreatingAccount)
        
        // Wait for simulated delay
        advanceTimeBy(1500.milliseconds)
        runCurrent()
        
        assertEquals(false, viewModel.uiState.value.isCreatingAccount)
    }
}
