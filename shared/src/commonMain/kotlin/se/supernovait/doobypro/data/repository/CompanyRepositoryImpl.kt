package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.doobypro.data.local.dao.CompanyDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.repository.CompanyRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [CompanyRepository] using [CompanyDao].
 */
class CompanyRepositoryImpl(
    private val companyDao: CompanyDao,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : CompanyRepository {

    override fun getCompanies(): Flow<List<Company>> {
        return companyDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCompanyById(id: String): Company? {
        return withContext(ioContext) {
            companyDao.getById(id)?.toDomain()
        }
    }

    override suspend fun upsertCompany(company: Company) {
        withContext(ioContext) {
            companyDao.upsert(company.toEntity())
        }
    }

    override suspend fun deleteCompany(company: Company) {
        withContext(ioContext) {
            companyDao.delete(company.toEntity())
        }
    }
}
