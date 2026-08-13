package se.supernovait.doobypro.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.mapper.mapToEntity
import se.supernovait.app.core.data.persistence.mapper.mapToModel
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.AuthRepository.Companion.APP_USER_IDENTITY_KEY
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.DoobyIdType

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val prefs: DataStore<Preferences>
) : AuthRepository {

    override fun observeCurrentUserId(): Flow<String?> {
        return prefs.data.map { preferences ->
            preferences[stringPreferencesKey(APP_USER_IDENTITY_KEY)]
        }
    }

    override fun observeUserById(id: String): Flow<User?> {
        return userDao.observeUserById(id).map { it?.mapToModel() }
    }

    override suspend fun getCurrentUserId(): String? {
        return observeCurrentUserId().firstOrNull()
    }

    override suspend fun getUserById(id: String): User? {
        return userDao.getById(id)?.mapToModel()
    }

    override suspend fun signUp(user: User): Result<User> {
        return try {
            val id = SupernovaIdGenerator.generateId(DoobyIdType.USER.prefix)
            userDao.upsert(user.mapToEntity().copy(id = id))
            val savedUser = userDao.getById(id)
            if (savedUser != null) {
                saveUserToPrefs(id)
                Result.success(savedUser.mapToModel())
            } else {
                Result.failure(Exception("Failed to retrieve saved user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(username: String): Result<User> {
        val user = userDao.getByUsername(username)
        return if (user != null) {
            saveUserToPrefs(user.id)
            Result.success(user.mapToModel())
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun signOut() {
        prefs.edit { preferences ->
            preferences.remove(stringPreferencesKey(APP_USER_IDENTITY_KEY))
        }
    }

    private suspend fun saveUserToPrefs(id: String) {
        prefs.edit { preferences ->
            preferences[stringPreferencesKey(APP_USER_IDENTITY_KEY)] = id
        }
    }
}
