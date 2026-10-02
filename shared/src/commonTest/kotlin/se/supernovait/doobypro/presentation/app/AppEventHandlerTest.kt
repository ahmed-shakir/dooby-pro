package se.supernovait.doobypro.presentation.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.domain.logging.LogLevel
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.data.repository.fake.FakeAuthRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppEventHandlerTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var authManager: AuthenticationManager
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        authManager = AuthenticationManager(
            logger = FakeLogger(),
            authRepository = authRepository,
            managerScope = CoroutineScope(SupervisorJob() + testDispatcher),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `handleAppEvent should return true for handled events`() {
        assertTrue(handleAppEvent(AppEvent.NavigateBack, null, authManager))
        assertTrue(handleAppEvent(AppEvent.SignIn, null, authManager))
        assertTrue(handleAppEvent(AppEvent.SignOut, null, authManager))
    }

    @Test
    fun `handleAppEvent should return false for unhandled events`() {
        assertFalse(handleAppEvent(AppEvent.Message("test"), null, authManager))
    }

    @Test
    fun `handleAppEvent SignOut should trigger authManager signOut`() = runTest(testDispatcher) {
        // Seed user and sign in
        val user = User(
            id = "user-1",
            username = "test",
            firstname = "",
            lastname = "",
            email = "",
            birthdate = null
        )
        authRepository.signUp(user)
        assertTrue(authManager.isAuthenticated(), "Should be authenticated after setting user ID")

        handleAppEvent(AppEvent.SignOut, null, authManager)

        assertFalse(authManager.isAuthenticated(), "Should be signed out after event")
    }

    private class FakeLogger : Logger {
        override fun trace(message: String, throwable: Throwable?, tag: String?) {}
        override fun debug(message: String, throwable: Throwable?, tag: String?) {}
        override fun info(message: String, throwable: Throwable?, tag: String?) {}
        override fun warn(message: String, throwable: Throwable?, tag: String?) {}
        override fun error(message: String, throwable: Throwable?, tag: String?) {}
        override fun log(level: LogLevel, message: String, throwable: Throwable?, tag: String?) {}
    }
}
