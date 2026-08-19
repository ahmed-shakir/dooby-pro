package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.repository.CompanyRepository

class FakeCompanyRepository : CompanyRepository {
    private val companies = MutableStateFlow<Map<String, Company>>(emptyMap())

    override fun getCompanies(): Flow<List<Company>> = companies.map { it.values.toList() }

    override suspend fun getCompanyById(id: String): Result<Company, DataError> {
        return companies.value[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun saveCompany(company: Company): Result<String, DataError> {
        val id = company.id ?: "company-${companies.value.size}"
        val companyWithId = company.copy(id = id)
        companies.value += (id to companyWithId)
        return Result.Success(id)
    }

    override suspend fun deleteCompany(company: Company): Result<Unit, DataError> {
        companies.value -= (company.id ?: "")
        return Result.Success(Unit)
    }
}
