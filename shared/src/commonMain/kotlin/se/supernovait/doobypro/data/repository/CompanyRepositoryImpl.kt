package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
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
    private val companyDao: CompanyDao
) : CompanyRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getCompanies(): Flow<List<Company>> {
        return companyDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCompanyById(id: String): Result<Company, DataError> {
        return withContext(ioContext) {
            val company = companyDao.getById(id)
            if (company != null) {
                Result.Success(company.toDomain())
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveCompany(company: Company): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entityToSave = company.toEntity()
                companyDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteCompany(company: Company): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                companyDao.delete(company.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }
}
