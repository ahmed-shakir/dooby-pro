package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.LicenseDao
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.doobypro.data.local.dao.AccountDao
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.repository.AccountRepository
import kotlin.coroutines.CoroutineContext
import se.supernovait.app.core.data.persistence.mapper.toDomain as mapLicenseToModel
import se.supernovait.app.core.data.persistence.mapper.toDomain as mapUserToModel

/**
 * Implementation of [AccountRepository] using the Assembly Pattern.
 */
class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    private val userDao: UserDao,
    private val companyDao: CompanyDao,
    private val licenseDao: LicenseDao,
    private val agreementDao: AgreementDao
) : AccountRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getAccount(id: String): Account? {
        return withContext(ioContext) {
            val entity = accountDao.getById(id) ?: return@withContext null
            val user = userDao.getById(entity.userId)?.mapUserToModel() ?: return@withContext null
            val company = companyDao.getById(entity.id)?.toDomain() ?: return@withContext null
            val license = entity.licenseId?.let { licenseDao.getById(it)?.mapLicenseToModel() }
            val agreement = entity.agreementId?.let { agreementDao.getById(it)?.toDomain() }

            entity.toDomain(user, company, license, agreement)
        }
    }

    override suspend fun upsertAccount(account: Account) {
        withContext(ioContext) {
            accountDao.upsert(account.toEntity())
        }
    }

    override suspend fun deleteAccount(account: Account) {
        withContext(ioContext) {
            accountDao.delete(account.toEntity())
        }
    }
}
