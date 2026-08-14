package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.doobypro.data.local.dao.FakeAgreementDao
import se.supernovait.doobypro.domain.model.Agreement
import se.supernovait.doobypro.domain.model.AgreementStatus
import se.supernovait.doobypro.domain.model.DoobyIdType
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

    private val testAgreement = Agreement(
        id = SupernovaIdGenerator.generateId(DoobyIdType.AGREEMENT.prefix),
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
            agreementDao = fakeAgreementDao,
            ioContext = testDispatcher
        )
    }

    @Test
    fun `getAgreements should return all agreements mapped to models`() = runTest(testDispatcher) {
        repository.upsertAgreement(testAgreement)

        val agreements = repository.getAgreements().first()

        assertEquals(1, agreements.size)
        assertEquals(testAgreement, agreements[0])
    }

    @Test
    fun `getAgreementById should return mapped model if found`() = runTest(testDispatcher) {
        repository.upsertAgreement(testAgreement)

        val result = repository.getAgreementById(testAgreement.id!!)

        assertEquals(testAgreement, result)
    }

    @Test
    fun `getAgreementById should return null if not found`() = runTest(testDispatcher) {
        val result = repository.getAgreementById("non-existent")

        assertNull(result)
    }

    @Test
    fun `upsertAgreement should call dao upsert`() = runTest(testDispatcher) {
        repository.upsertAgreement(testAgreement)

        val savedEntity = fakeAgreementDao.getById(testAgreement.id!!)
        assertEquals(testAgreement.id, savedEntity?.id)
    }

    @Test
    fun `deleteAgreement should call dao delete`() = runTest(testDispatcher) {
        repository.upsertAgreement(testAgreement)
        assertEquals(1, repository.getAgreements().first().size)

        repository.deleteAgreement(testAgreement)

        assertEquals(0, repository.getAgreements().first().size)
    }
}
