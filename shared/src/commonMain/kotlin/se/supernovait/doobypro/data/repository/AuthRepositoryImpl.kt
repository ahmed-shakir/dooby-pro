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
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.util.LogTags
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
 * @param logger The logger for authentication operations.
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val accountDao: AccountDao,
    private val sessionRepository: SessionRepository,
    private val logger: Logger
) : AuthRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun observeCurrentUserId(): Flow<String?> = sessionRepository.observeCurrentUserId()

    override fun observeUserById(id: String): Flow<User?> = userDao.observeUserById(id).map { it?.toDomain() }

    override suspend fun getCurrentUserId(): Result<String, AuthError> {
        return withContext(ioContext) {
            val userId = observeCurrentUserId().firstOrNull()
            if (userId != null) {
                logger.debug("Current user with ID retrieved: $userId", tag = LogTags.AUTH_REPO)
                Result.Success(userId)
            } else {
                logger.debug("No active user session found", tag = LogTags.AUTH_REPO)
                Result.Failure(AuthError.NOT_AUTHENTICATED)
            }
        }
    }

    override suspend fun getUserById(id: String): Result<User, DataError> {
        return withContext(ioContext) {
            val user = userDao.getById(id)
            if (user != null) {
                logger.debug("Fetching user with ID: $id", tag = LogTags.AUTH_REPO)
                Result.Success(user.toDomain())
            } else {
                logger.warn("User not found with ID: $id", tag = LogTags.AUTH_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun signUp(user: User): Result<User, AuthError> {
        return withContext(ioContext) {
            try {
                logger.info("Signing up new user: ${user.username}", tag = LogTags.AUTH_REPO)
                val id = SupernovaIdGenerator.generateId(IdType.USER.prefix)
                userDao.upsert(user.toEntity().copy(id = id))
                val savedUser = userDao.getById(id)
                if (savedUser != null) {
                    sessionRepository.setCurrentUserId(id)
                    logger.info("User sign up successful with ID: $id", tag = LogTags.AUTH_REPO)
                    Result.Success(savedUser.toDomain())
                } else {
                    logger.error("User not found after insert for username: ${user.username}", tag = LogTags.AUTH_REPO)
                    Result.Failure(AuthError.USER_NOT_FOUND)
                }
            } catch (e: Exception) {
                logger.error("Error signing up user: ${user.username}", e, tag = LogTags.AUTH_REPO)
                Result.Failure(AuthError.UNKNOWN)
            }
        }
    }

    override suspend fun signIn(username: String): Result<User, AuthError> {
        return withContext(ioContext) {
            logger.info("Attempting sign in for username: $username", tag = LogTags.AUTH_REPO)
            val user = userDao.getByUsername(username)?.toDomain() ?: run {
                logger.warn("Sign in failed: Username $username not found", tag = LogTags.AUTH_REPO)
                return@withContext Result.Failure(AuthError.USER_NOT_FOUND)
            }

            // Check if account is deactivated or marked for deletion
            val account = accountDao.getByUserId(user.id!!)
            if (account != null && (account.deactivatedAt != null || account.isMarkedForDeletion)) {
                logger.warn("Sign in failed: Account for user with ID: ${user.id} is deactivated/deleted", tag = LogTags.AUTH_REPO)
                return@withContext Result.Failure(AuthError.ACCOUNT_DEACTIVATED)
            }

            if (user.canLogin()) {
                sessionRepository.setCurrentUserId(user.id!!)
                logger.info("User sign in successful for username: $username", tag = LogTags.AUTH_REPO)
                Result.Success(user)
            } else {
                logger.warn("Sign in failed: User with ID: ${user.id} is deactivated", tag = LogTags.AUTH_REPO)
                Result.Failure(AuthError.USER_DEACTIVATED)
            }
        }
    }

    override suspend fun signOut() {
        logger.info("Signing out current user", tag = LogTags.AUTH_REPO)
        sessionRepository.clearCurrentUserId()
    }
}
