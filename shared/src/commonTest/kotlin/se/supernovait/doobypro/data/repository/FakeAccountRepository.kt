package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.repository.AccountRepository

class FakeAccountRepository : AccountRepository {
    private val accounts = MutableStateFlow<Map<String, Account>>(emptyMap())

    override suspend fun getAccount(id: String): Result<Account, DataError> {
        val account = accounts.value[id]
        return if (account != null) {
            Result.Success(account)
        } else {
            Result.Failure(DataError.NOT_FOUND)
        }
    }

    override suspend fun saveAccount(account: Account): Result<String, DataError> {
        val id = account.id ?: account.company.id ?: "generated-id"
        val accountToSave = account.copy(id = id)
        accounts.update { it + (id to accountToSave) }
        return Result.Success(id)
    }

    override suspend fun deleteAccount(account: Account): Result<Unit, DataError> {
        accounts.update { it - (account.id ?: "") }
        return Result.Success(Unit)
    }
}
