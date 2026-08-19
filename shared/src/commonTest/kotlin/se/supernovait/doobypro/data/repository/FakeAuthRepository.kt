package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.AuthError
import se.supernovait.app.core.domain.error.DataError
import kotlin.time.Duration.Companion.milliseconds

class FakeAuthRepository : AuthRepository {
    private val users = MutableStateFlow<Map<String, User>>(emptyMap())
    private val currentUserId = MutableStateFlow<String?>(null)

    override fun observeCurrentUserId(): Flow<String?> = currentUserId
    override fun observeUserById(id: String): Flow<User?> = users.map { it[id] }

    override suspend fun getCurrentUserId(): Result<String, AuthError> {
        return currentUserId.value?.let { Result.Success(it) } ?: Result.Failure(AuthError.NOT_AUTHENTICATED)
    }

    override suspend fun getUserById(id: String): Result<User, DataError> {
        return users.value[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun signIn(username: String): Result<User, AuthError> {
        delay(1.milliseconds)
        val user = users.value.values.find { it.username == username }
        return if (user != null) {
            currentUserId.value = user.id
            Result.Success(user)
        } else {
            Result.Failure(AuthError.USER_NOT_FOUND)
        }
    }

    override suspend fun signUp(user: User): Result<User, AuthError> {
        val id = user.id ?: "user-${user.username}"
        val userWithId = user.copy(id = id)
        users.value += (id to userWithId)
        currentUserId.value = id
        return Result.Success(userWithId)
    }

    override suspend fun signOut() {
        currentUserId.value = null
    }

    fun setCurrentUserId(id: String?) {
        currentUserId.value = id
    }
}
