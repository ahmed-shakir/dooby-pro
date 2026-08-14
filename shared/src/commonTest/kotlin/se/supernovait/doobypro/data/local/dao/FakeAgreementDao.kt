package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import se.supernovait.doobypro.data.local.entity.AgreementEntity

/**
 * A fake implementation of [AgreementDao] for testing.
 */
class FakeAgreementDao : AgreementDao {
    private val state = MutableStateFlow<Map<String, AgreementEntity>>(emptyMap())

    override fun getAll(): Flow<List<AgreementEntity>> {
        return state.map { it.values.toList() }
    }

    override suspend fun getById(id: String): AgreementEntity? {
        return state.value[id]
    }

    override suspend fun upsert(agreement: AgreementEntity) {
        state.value += (agreement.id to agreement)
    }

    override suspend fun delete(agreement: AgreementEntity) {
        state.value -= agreement.id
    }
}
