package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
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

/**
 * Implementation of [AccountRepository] using the Assembly Pattern.
 *
 * This repository orchestrates multiple sub-repositories to provide a unified
 * view of the [Account] aggregate.
 */
class AccountRepositoryImpl(
    private val authRepository: AuthRepository,
    private val companyRepository: CompanyRepository,
    private val licenseRepository: LicenseRepository,
    private val agreementRepository: AgreementRepository,
    private val accountDao: AccountDao,
) : AccountRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getAccount(id: String): Result<Account, DataError> {
        return withContext(ioContext) {
            val entity = accountDao.getById(id) ?: return@withContext Result.Failure(DataError.NOT_FOUND)

            // Assemble components from their respective domains
            val userResult = authRepository.getUserById(entity.userId)
            val companyResult = companyRepository.getCompanyById(entity.id)

            // Return early if critical components are missing
            if (userResult is Result.Failure) return@withContext Result.Failure(DataError.NOT_FOUND)
            if (companyResult is Result.Failure) return@withContext Result.Failure(DataError.NOT_FOUND)

            val user = (userResult as Result.Success).data
            val company = (companyResult as Result.Success).data

            // Optional components
            val license = entity.licenseId?.let { licenseRepository.getLicenseById(it).getOrNull() }
            val agreement = entity.agreementId?.let { agreementRepository.getAgreementById(it).getOrNull() }

            Result.Success(entity.toDomain(user, company, license, agreement))
        }
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

    override suspend fun deleteAccount(account: Account): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                accountDao.delete(account.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    /**
     * Internal orchestration for creating a fresh account structure.
     * Sequences: Auth (user) -> Company -> License -> Account Link.
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
