package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.repository.AgreementRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [AgreementRepository] using [AgreementDao].
 */
class AgreementRepositoryImpl(
    private val agreementDao: AgreementDao
) : AgreementRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override suspend fun getAgreements(accountId: String): List<Agreement> {
        return withContext(ioContext) {
            agreementDao.getByAccountId(accountId).map { it.toDomain() }
        }
    }

    override suspend fun getAgreementsByIds(ids: List<String>): List<Agreement> {
        return withContext(ioContext) {
            agreementDao.getByIds(ids).map { it.toDomain() }
        }
    }

    override suspend fun getAgreementById(id: String): Result<Agreement, DataError> {
        return withContext(ioContext) {
            val agreement = agreementDao.getById(id)
            if (agreement != null) {
                Result.Success(agreement.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveAgreement(agreement: Agreement): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entityToSave = agreement.toEntity()
                agreementDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteAgreement(agreement: Agreement): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                agreementDao.delete(agreement.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
