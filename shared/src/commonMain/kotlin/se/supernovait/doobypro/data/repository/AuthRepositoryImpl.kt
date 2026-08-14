package se.supernovait.doobypro.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.AuthRepository.Companion.APP_USER_IDENTITY_KEY
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.DoobyIdType
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [AuthRepository] that manages user authentication state and persistence.
 *
 * This implementation uses [UserDao] for user data persistence and [DataStore] for
 * managing the session of the currently logged-in user.
 *
 * @param userDao The data access object for user entities.
 * @param prefs The data store for local preferences and session state.
 * @param ioContext The coroutine context for performing I/O operations. Defaults to [Dispatchers.IO].
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val prefs: DataStore<Preferences>,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : AuthRepository {
    private val userKey = stringPreferencesKey(APP_USER_IDENTITY_KEY)

    override fun observeCurrentUserId(): Flow<String?> {
        return prefs.data.map { preferences ->
            preferences[userKey]
        }
    }

    override fun observeUserById(id: String): Flow<User?> {
        return userDao.observeUserById(id).map { it?.toDomain() }
    }

    override suspend fun getCurrentUserId(): String? {
        return withContext(ioContext) {
            observeCurrentUserId().firstOrNull()
        }
    }

    override suspend fun getUserById(id: String): User? {
        return withContext(ioContext) {
            userDao.getById(id)?.toDomain()
        }
    }

    override suspend fun signUp(user: User): Result<User> {
        return withContext(ioContext) {
            try {
                val id = SupernovaIdGenerator.generateId(DoobyIdType.USER.prefix)
                userDao.upsert(user.toEntity().copy(id = id))
                val savedUser = userDao.getById(id)
                if (savedUser != null) {
                    saveUserToPrefs(id)
                    Result.success(savedUser.toDomain())
                } else {
                    Result.failure(Exception("Failed to retrieve saved user"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun signIn(username: String): Result<User> {
        return withContext(ioContext) {
            val user = userDao.getByUsername(username)
            return@withContext if (user != null) {
                saveUserToPrefs(user.id)
                Result.success(user.toDomain())
            } else {
                Result.failure(Exception("User not found"))
            }
        }
    }

    override suspend fun signOut() {
        withContext(ioContext) {
            prefs.edit { preferences ->
                preferences.remove(userKey)
            }
        }
    }

    /**
     * Persists the user ID to the local preferences.
     *
     * @param id The user ID to save.
     */
    private suspend fun saveUserToPrefs(id: String) {
        withContext(ioContext) {
            prefs.edit { preferences ->
                preferences[userKey] = id
            }
        }
    }
}
