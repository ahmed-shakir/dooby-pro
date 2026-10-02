package se.supernovait.doobypro.presentation.support

import io.ktor.http.decodeURLQueryComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.logging.LogLevel
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.data.repository.fake.FakeAccountRepository
import se.supernovait.doobypro.data.repository.fake.FakeAuthRepository
import se.supernovait.doobypro.util.PlatformTestConfig
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SupportViewModelTest : PlatformTestConfig() {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeAccountRepo: FakeAccountRepository
    private lateinit var viewModel: SupportViewModel

    private val testUser = User(
        id = "user_1",
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        email = "john@example.com",
        birthdate = LocalDate(1990, 1, 1),
        phoneNumber = "+971501234567"
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepo = FakeAuthRepository()
        fakeAccountRepo = FakeAccountRepository()
        viewModel = SupportViewModel(
            authRepository = fakeAuthRepo,
            accountRepository = fakeAccountRepo,
            logger = FakeLogger()
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selecting tab updates selectedTab state`() = runTest(testDispatcher) {
        viewModel.onEvent(SupportEvent.SelectTab(1))
        assertEquals(1, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `submitting request retrieves session user info and formats mailto url`() = runTest(testDispatcher) {
        fakeAuthRepo.signUp(testUser)
        advanceUntilIdle()
        viewModel.onEvent(SupportEvent.UpdateMessage("Issue description"))

        var emailOpened = false
        var mailtoUrl = ""
        viewModel.onEvent(SupportEvent.SubmitRequest { url ->
            emailOpened = true
            mailtoUrl = url
        })
        advanceUntilIdle()

        val decodedMailtoUrl = mailtoUrl.decodeURLQueryComponent()

        assertTrue(emailOpened)
        assertTrue(decodedMailtoUrl.startsWith("mailto:support@supernovait.se"))
        assertTrue(decodedMailtoUrl.contains("John Doe"))
        assertTrue(decodedMailtoUrl.contains("john@example.com"))
        assertTrue(decodedMailtoUrl.contains("+971501234567"))
        assertTrue(decodedMailtoUrl.contains("Issue description"))
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
