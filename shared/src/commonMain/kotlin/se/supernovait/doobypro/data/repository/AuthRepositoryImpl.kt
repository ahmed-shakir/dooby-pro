package se.supernovait.doobypro.data.repository

import androidx.datastore.core.DataStore
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
import se.supernovait.app.core.domain.auth.SessionRepository
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.AuthError
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.domain.model.IdType
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [AuthRepository] that manages user authentication operations.
 *
 * This implementation uses [UserDao] for user data persistence and [DataStore] for
 * managing the session of the currently logged-in user.
 *
 * @param userDao The data access object for user entities.
 * @param accountDao The data access object for account entities.
 * @param sessionRepository The repository for session state.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val accountDao: AccountDao,
    private val sessionRepository: SessionRepository,
) : AuthRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun observeCurrentUserId(): Flow<String?> = sessionRepository.observeCurrentUserId()

    override fun observeUserById(id: String): Flow<User?> = userDao.observeUserById(id).map { it?.toDomain() }

    override suspend fun getCurrentUserId(): Result<String, AuthError> {
        return withContext(ioContext) {
            val userId = observeCurrentUserId().firstOrNull()
            if (userId != null) {
                Result.Success(userId)
            } else {
                Result.Failure(AuthError.NOT_AUTHENTICATED)
            }
        }
    }

    override suspend fun getUserById(id: String): Result<User, DataError> {
        return withContext(ioContext) {
            val user = userDao.getById(id)
            if (user != null) {
                Result.Success(user.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun signUp(user: User): Result<User, AuthError> {
        return withContext(ioContext) {
            try {
                val id = SupernovaIdGenerator.generateId(IdType.USER.prefix)
                userDao.upsert(user.toEntity().copy(id = id))
                val savedUser = userDao.getById(id)
                if (savedUser != null) {
                    sessionRepository.setCurrentUserId(id)
                    Result.Success(savedUser.toDomain())
                } else {
                    Result.Failure(AuthError.USER_NOT_FOUND)
                }
            } catch (e: Exception) {
                Result.Failure(AuthError.UNKNOWN)
            }
        }
    }

    override suspend fun signIn(username: String): Result<User, AuthError> {
        return withContext(ioContext) {
            val user = userDao.getByUsername(username)?.toDomain() ?: return@withContext Result.Failure(AuthError.USER_NOT_FOUND)

            // Check if account is deactivated or marked for deletion
            val account = accountDao.getByUserId(user.id!!)
            if (account != null && (account.deactivatedAt != null || account.isMarkedForDeletion)) {
                return@withContext Result.Failure(AuthError.ACCOUNT_DEACTIVATED)
            }

            if (user.canLogin()) {
                sessionRepository.setCurrentUserId(user.id!!)
                Result.Success(user.lastLogin())
                Result.Success(user)
            } else {
                Result.Failure(AuthError.USER_DEACTIVATED)
            }
        }
    }

    override suspend fun signOut() {
        sessionRepository.clearCurrentUserId()
    }
}
