package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.doobypro.data.local.dao.AgreementDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Agreement
import se.supernovait.doobypro.domain.repository.AgreementRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [AgreementRepository] using [AgreementDao].
 */
class AgreementRepositoryImpl(
    private val agreementDao: AgreementDao,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : AgreementRepository {

    override fun getAgreements(): Flow<List<Agreement>> {
        return agreementDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAgreementById(id: String): Agreement? {
        return withContext(ioContext) {
            agreementDao.getById(id)?.toDomain()
        }
    }

    override suspend fun upsertAgreement(agreement: Agreement) {
        withContext(ioContext) {
            agreementDao.upsert(agreement.toEntity())
        }
    }

    override suspend fun deleteAgreement(agreement: Agreement) {
        withContext(ioContext) {
            agreementDao.delete(agreement.toEntity())
        }
    }
}
