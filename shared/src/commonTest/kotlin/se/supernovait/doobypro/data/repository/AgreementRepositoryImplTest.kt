package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.doobypro.data.local.dao.FakeAgreementDao
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.model.agreement.AgreementStatus
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [AgreementRepositoryImpl].
 */
class AgreementRepositoryImplTest {
    private lateinit var fakeAgreementDao: FakeAgreementDao
    private lateinit var repository: AgreementRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val agreementId = SupernovaIdGenerator.generateId(IdType.AGREEMENT.prefix)
    private val accountId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)

    private val testAgreement = Agreement(
        id = agreementId,
        accountId = accountId,
        status = AgreementStatus.ACTIVE,
        equipmentId = "SN-123",
        equipmentModel = "Model X",
        title = "Lease Agreement",
        description = "Lease for Model X",
        fee = Amount(1000, "AED"),
        billingFrequency = BillingFrequency.MONTHLY,
        deposit = Amount(5000, "AED"),
        issueDate = LocalDate(2026, 8, 15),
        cancellationDate = null
    )

    @BeforeTest
    fun setUp() {
        fakeAgreementDao = FakeAgreementDao()
        repository = AgreementRepositoryImpl(
            agreementDao = fakeAgreementDao
        )
    }

    @Test
    fun `getAgreements should return all agreements for account mapped to models`() = runTest(testDispatcher) {
        repository.saveAgreement(testAgreement)

        val agreements = repository.getAgreements(accountId)

        assertEquals(1, agreements.size)
        assertEquals(testAgreement, agreements[0])
    }

    @Test
    fun `getAgreementById should return mapped model if found`() = runTest(testDispatcher) {
        repository.saveAgreement(testAgreement)

        val result = repository.getAgreementById(testAgreement.id!!).getOrNull()

        assertEquals(testAgreement, result)
    }

    @Test
    fun `getAgreementById should return null if not found`() = runTest(testDispatcher) {
        val result = repository.getAgreementById("non-existent").getOrNull()

        assertNull(result)
    }

    @Test
    fun `upsertAgreement should call dao upsert`() = runTest(testDispatcher) {
        repository.saveAgreement(testAgreement)

        val savedEntity = fakeAgreementDao.getById(testAgreement.id!!)
        assertEquals(testAgreement.id, savedEntity?.id)
    }

    @Test
    fun `deleteAgreement should call dao delete`() = runTest(testDispatcher) {
        repository.saveAgreement(testAgreement)
        assertEquals(1, repository.getAgreements(accountId).size)

        repository.deleteAgreement(testAgreement)

        assertEquals(0, repository.getAgreements(accountId).size)
    }
}
