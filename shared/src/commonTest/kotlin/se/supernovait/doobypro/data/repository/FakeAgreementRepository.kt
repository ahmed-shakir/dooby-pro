package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.repository.AgreementRepository

class FakeAgreementRepository : AgreementRepository {
    private val agreements = MutableStateFlow<Map<String, Agreement>>(emptyMap())

    override suspend fun getAgreements(accountId: String): List<Agreement> {
        return agreements.value.values.filter { it.accountId == accountId }
    }

    override suspend fun getAgreementsByIds(ids: List<String>): List<Agreement> {
        return ids.mapNotNull { agreements.value[it] }
    }

    override suspend fun getAgreementById(id: String): Result<Agreement, DataError> {
        return agreements.value[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
    }

    override suspend fun saveAgreement(agreement: Agreement): Result<String, DataError> {
        val id = agreement.id ?: "agr-${agreements.value.size}"
        val agreementWithId = agreement.copy(id = id)
        agreements.value += (id to agreementWithId)
        return Result.Success(id)
    }

    override suspend fun deleteAgreement(agreement: Agreement): Result<Unit, DataError> {
        agreements.value -= (agreement.id ?: "")
        return Result.Success(Unit)
    }
}
