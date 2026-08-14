package se.supernovait.doobypro.data.local.dao

import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.doobypro.data.local.entity.AccountEntity

/**
 * A fake implementation of [AccountDao] for unit testing.
 */
class FakeAccountDao : AccountDao {
    private val state = MutableStateFlow<Map<String, AccountEntity>>(emptyMap())

    override suspend fun getById(id: String): AccountEntity? {
        return state.value[id]
    }

    override suspend fun upsert(account: AccountEntity) {
        state.value += (account.id to account)
    }

    override suspend fun delete(account: AccountEntity) {
        state.value -= account.id
    }
}
