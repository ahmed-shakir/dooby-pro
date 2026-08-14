package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.doobypro.data.local.entity.CompanyEntity

/**
 * A fake implementation of [CompanyDao] for testing.
 */
class FakeCompanyDao : CompanyDao {
    private val state = MutableStateFlow<Map<String, CompanyEntity>>(emptyMap())

    override fun getAll(): Flow<List<CompanyEntity>> {
        return state.map { it.values.toList() }
    }

    override suspend fun getById(id: String): CompanyEntity? {
        return state.value[id]
    }

    override suspend fun upsert(company: CompanyEntity) {
        state.value += (company.id to company)
    }

    override suspend fun delete(company: CompanyEntity) {
        state.value -= company.id
    }
}
