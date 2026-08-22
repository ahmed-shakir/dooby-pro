package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.app.core.domain.auth.SessionRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.AuthError

class FakeSessionRepository : SessionRepository {
    private val currentUserId = MutableStateFlow<String?>(null)

    override fun observeCurrentUserId(): Flow<String?> = currentUserId

    override suspend fun getCurrentUserId(): Result<String, AuthError> {
        return currentUserId.value?.let { Result.Success(it) } ?: Result.Failure(AuthError.NOT_AUTHENTICATED)
    }

    override suspend fun setCurrentUserId(id: String) {
        currentUserId.value = id
    }

    override suspend fun clearCurrentUserId() {
        currentUserId.value = null
    }
}
