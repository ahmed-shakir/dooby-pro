package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.doobypro.data.local.dao.FakeUserDao
import se.supernovait.doobypro.data.local.preferences.FakeDataStore
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [AuthRepositoryImpl].
 *
 * Verifies authentication flows, user persistence, and session management.
 */
class AuthRepositoryImplTest {

    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var repository: AuthRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val testUser = User(
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    @BeforeTest
    fun setUp() {
        fakeUserDao = FakeUserDao()
        fakeDataStore = FakeDataStore()
        repository = AuthRepositoryImpl(
            userDao = fakeUserDao,
            prefs = fakeDataStore,
            ioContext = testDispatcher
        )
    }

    @Test
    fun `signUp should persist user and save session`() = runTest(testDispatcher) {
        val result = repository.signUp(testUser)

        assertTrue(result.isSuccess)
        val savedUser = result.getOrNull()
        assertNotNull(savedUser)
        assertNotNull(savedUser.id)
        
        // Verify DAO
        val dbUser = fakeUserDao.getById(savedUser.id!!)
        assertNotNull(dbUser)
        assertEquals(testUser.username, dbUser.username)

        // Verify Session
        assertEquals(savedUser.id, repository.getCurrentUserId())
    }

    @Test
    fun `signIn should succeed for existing user and save session`() = runTest(testDispatcher) {
        // Prepare: Sign up first
        val signUpResult = repository.signUp(testUser)
        val userId = signUpResult.getOrNull()?.id!!
        
        // Clear session
        repository.signOut()
        assertNull(repository.getCurrentUserId())

        // Action: Sign in
        val result = repository.signIn(testUser.username)

        assertTrue(result.isSuccess)
        assertEquals(userId, result.getOrNull()?.id)
        assertEquals(userId, repository.getCurrentUserId())
    }

    @Test
    fun `signIn should fail for non-existent user`() = runTest(testDispatcher) {
        val result = repository.signIn("unknown")

        assertTrue(result.isFailure)
        assertNull(repository.getCurrentUserId())
    }

    @Test
    fun `signOut should clear session but keep user in db`() = runTest(testDispatcher) {
        val signUpResult = repository.signUp(testUser)
        val userId = signUpResult.getOrNull()?.id!!

        repository.signOut()

        assertNull(repository.getCurrentUserId())
        assertNotNull(fakeUserDao.getById(userId))
    }

    @Test
    fun `observeCurrentUserId should emit changes`() = runTest(testDispatcher) {
        val userId = repository.signUp(testUser).getOrNull()?.id!!
        
        assertEquals(userId, repository.observeCurrentUserId().first())

        repository.signOut()
        assertNull(repository.observeCurrentUserId().first())
    }

    @Test
    fun `getUserById should return model from dao`() = runTest(testDispatcher) {
        val userId = repository.signUp(testUser).getOrNull()?.id!!

        val result = repository.getUserById(userId)

        assertNotNull(result)
        assertEquals(testUser.username, result.username)
    }
}
