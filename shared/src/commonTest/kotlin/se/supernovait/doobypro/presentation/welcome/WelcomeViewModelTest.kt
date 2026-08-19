package se.supernovait.doobypro.presentation.welcome

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.doobypro.data.repository.FakeAuthRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WelcomeViewModelTest {

    private lateinit var viewModel: WelcomeViewModel
    private lateinit var authRepository: FakeAuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        viewModel = WelcomeViewModel(authRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertFalse(state.showSignInForm)
        assertFalse(state.isSigningIn)
        assertNull(state.signInError)
        assertFalse(state.isUsernameEmpty)
    }

    @Test
    fun `ShowSignInForm event updates state`() {
        viewModel.onEvent(WelcomeScreenEvent.ShowSignInForm)
        assertTrue(viewModel.uiState.value.showSignInForm)
    }

    @Test
    fun `HideSignInForm event updates state`() {
        viewModel.onEvent(WelcomeScreenEvent.ShowSignInForm)
        viewModel.onEvent(WelcomeScreenEvent.HideSignInForm)
        assertFalse(viewModel.uiState.value.showSignInForm)
    }

    @Test
    fun `SignIn with empty username sets isUsernameEmpty to true`() = runTest {
        viewModel.onEvent(WelcomeScreenEvent.SignIn(""))
        
        assertTrue(viewModel.uiState.value.isUsernameEmpty)
    }

    @Test
    fun `SignIn with valid username successfully updates state and sends event`() = runTest {
        val username = "testuser"
        authRepository.signUp(User(username = username, firstname = "", lastname = "", email = "", birthdate = null))
        
        viewModel.onEvent(WelcomeScreenEvent.SignIn(username))
        
        // Execute up to the delay in repository
        runCurrent()
        
        // Check loading state
        assertTrue(viewModel.uiState.value.isSigningIn)
        
        // Complete everything
        advanceUntilIdle()
        
        assertFalse(viewModel.uiState.value.isSigningIn)
        assertFalse(viewModel.uiState.value.showSignInForm)
        
        val event = viewModel.events.first()
        assertEquals(AppEvent.SignIn, event)
    }

    @Test
    fun `SignIn failure sets signInError and sends Failure event`() = runTest {
        viewModel.onEvent(WelcomeScreenEvent.SignIn("unknown"))
        
        advanceUntilIdle()
        
        assertNotNull(viewModel.uiState.value.signInError)
        val event = viewModel.events.first()
        assertTrue(event is AppEvent.Failure)
    }
}
