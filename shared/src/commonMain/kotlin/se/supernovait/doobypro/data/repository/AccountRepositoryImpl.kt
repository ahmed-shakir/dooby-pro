package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.flatMap
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.data.local.entity.AccountEntity
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.domain.repository.AgreementRepository
import se.supernovait.doobypro.domain.repository.CompanyRepository
import se.supernovait.doobypro.domain.repository.LicenseRepository
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock

/**
 * Implementation of [AccountRepository] using the Assembly Pattern.
 */
class AccountRepositoryImpl(
    private val authRepository: AuthRepository,
    private val companyRepository: CompanyRepository,
    private val licenseRepository: LicenseRepository,
    private val agreementRepository: AgreementRepository,
    private val accountDao: AccountDao,
    private val userDao: UserDao
) : AccountRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getAccount(id: String): Result<Account, DataError> {
        return withContext(ioContext) {
            val entity = accountDao.getById(id) ?: return@withContext Result.Failure(DataError.NOT_FOUND)
            assembleAccount(entity)
        }
    }

    override suspend fun getAccountByUserId(userId: String): Result<Account, DataError> {
        return withContext(ioContext) {
            val entity = accountDao.getByUserId(userId) ?: return@withContext Result.Failure(DataError.NOT_FOUND)
            assembleAccount(entity)
        }
    }

    private suspend fun assembleAccount(entity: AccountEntity): Result<Account, DataError> {
        // Assemble components from their respective domains
        val userResult = authRepository.getUserById(entity.userId)
        val companyResult = companyRepository.getCompanyById(entity.id)

        // Return early if critical components are missing
        if (userResult is Result.Failure) return Result.Failure(DataError.NOT_FOUND)
        if (companyResult is Result.Failure) return Result.Failure(DataError.NOT_FOUND)

        val user = (userResult as Result.Success).data
        val company = (companyResult as Result.Success).data

        // Optional components
        val license = entity.licenseId?.let { licenseRepository.getLicenseById(it).getOrNull() }
        val agreement = entity.agreementId?.let { agreementRepository.getAgreementById(it).getOrNull() }

        return Result.Success(entity.toDomain(user, company, license, agreement))
    }

    override suspend fun saveAccount(account: Account): Result<String, DataError> {
        return withContext(ioContext) {
            if (account.id.isNullOrBlank()) {
                saveNewAccount(account)
            } else {
                updateExistingAccount(account)
            }
        }
    }

    override suspend fun deleteAccount(id: String): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                val entity = accountDao.getById(id) ?: return@withContext Result.Failure(DataError.NOT_FOUND)
                val datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                // Soft delete: Mark for deletion and set deactivation timestamp
                accountDao.upsert(entity.copy(deactivatedAt = datetime, isMarkedForDeletion = true))

                val user = userDao.getById(entity.userId)?.toDomain()
                user?.let { userDao.upsert(it.softDelete().toEntity()) }

                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun purgeDeletedAccounts(): Result<Int, DataError> {
        return withContext(ioContext) {
            try {
                val accountsToPurge = accountDao.getAccountsMarkedForDeletion()
                val now = Clock.System.now()
                val threshold = now.minus(30, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                
                var purgeCount = 0
                accountsToPurge.forEach { entity ->
                    val deactivationInstant = entity.deactivatedAt?.toInstant(TimeZone.currentSystemDefault())
                    if (deactivationInstant != null && deactivationInstant < threshold) {
                        hardDeleteAccountStructure(entity)
                        purgeCount++
                    }
                }
                Result.Success(purgeCount)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    /**
     * Performs permanent deletion of all entities linked to the account.
     */
    private suspend fun hardDeleteAccountStructure(entity: AccountEntity) {
        // 1. Delete profile data
        companyRepository.getCompanyById(entity.id).getOrNull()?.let {
            companyRepository.deleteCompany(it)
        }
        
        // 2. Delete related license/agreements
        entity.licenseId?.let { id ->
            licenseRepository.getLicenseById(id).getOrNull()?.let { 
                licenseRepository.deleteLicense(it)
            }
        }

        entity.agreementId?.let { id ->
            agreementRepository.getAgreementById(id).getOrNull()?.let {
                agreementRepository.deleteAgreement(it)
            }
        }

        // 3. Delete user data
        userDao.getById(entity.userId)?.let {
            userDao.delete(it)
        }

        // 4. Finally delete link in Account table
        accountDao.delete(entity)
    }

    /**
     * Internal orchestration for creating a fresh account structure.
     */
    private suspend fun saveNewAccount(account: Account): Result<String, DataError> {
        // 1. Authenticate / Create User Identity
        val user = authRepository.signUp(account.user).getOrNull()
            ?: return Result.Failure(DataError.UNKNOWN)

        // 2. Persist Company Profile
        return companyRepository.saveCompany(account.company).flatMap { companyId ->

            // 3. Provision Default Free License
            val license = if (account.license != null) {
                account.license.copy(accountId = companyId)
            } else {
                AppDefaults.license(companyId)
            }

            licenseRepository.saveLicense(license).flatMap { licenseId ->
                // 4. Link everything in the root Account table
                linkAccount(user.id!!, companyId, licenseId)
            }
        }
    }

    private suspend fun updateExistingAccount(account: Account): Result<String, DataError> {
        return try {
            // 1. Update sub-components
            companyRepository.saveCompany(account.company)
            userDao.upsert(account.user.toEntity())
            
            // 2. Update the account link entity
            val entityToSave = account.toEntity()
            accountDao.upsert(entityToSave)
            Result.Success(entityToSave.id)
        } catch (_: Exception) {
            Result.Failure(DataError.DATABASE_ERROR)
        }
    }

    private suspend fun linkAccount(userId: String, companyId: String, licenseId: String?): Result<String, DataError> {
        return try {
            accountDao.upsert(
                AccountEntity(
                    id = companyId,
                    userId = userId,
                    licenseId = licenseId,
                    agreementId = null
                )
            )
            Result.Success(companyId)
        } catch (_: Exception) {
            Result.Failure(DataError.DATABASE_ERROR)
        }
    }
}
