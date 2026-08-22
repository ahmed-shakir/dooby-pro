package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.doobypro.data.local.entity.AgreementEntity

/**
 * A fake implementation of [AgreementDao] for testing.
 */
class FakeAgreementDao : AgreementDao {
    private val state = MutableStateFlow<Map<String, AgreementEntity>>(emptyMap())

    override suspend fun getByAccountId(accountId: String): List<AgreementEntity> {
        return state.value.values.filter { it.accountId == accountId }
    }

    override suspend fun getById(id: String): AgreementEntity? {
        return state.value[id]
    }

    override suspend fun getByIds(ids: List<String>): List<AgreementEntity> {
        return ids.mapNotNull { state.value[it] }
    }

    override suspend fun upsert(agreement: AgreementEntity) {
        state.value += (agreement.id to agreement)
    }

    override suspend fun delete(agreement: AgreementEntity) {
        state.value -= agreement.id
    }
}
