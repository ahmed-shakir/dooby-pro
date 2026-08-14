package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.LocalDate
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.doobypro.data.local.entity.AgreementEntity
import se.supernovait.doobypro.domain.model.DoobyIdType
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.model.agreement.AgreementStatus
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [AgreementMapper].
 */
class AgreementMapperTest {
    private val agreementId = SupernovaIdGenerator.generateId(DoobyIdType.AGREEMENT.prefix)

    private val testAgreement = Agreement(
        id = agreementId,
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

    private val testAgreementEntity = AgreementEntity(
        id = agreementId,
        status = AgreementStatus.ACTIVE,
        equipmentId = "SN-123",
        equipmentModel = "Model X",
        title = "Lease Agreement",
        description = "Lease for Model X",
        fee = AmountEntity(1000, "AED"),
        billingFrequency = BillingFrequency.MONTHLY,
        deposit = AmountEntity(5000, "AED"),
        issueDate = LocalDate(2026, 8, 15),
        cancellationDate = null
    )

    @Test
    fun `toDomain should correctly transform AgreementEntity to Agreement model`() {
        val result = testAgreementEntity.toDomain()

        assertEquals(testAgreement, result)
    }

    @Test
    fun `toEntity should correctly transform Agreement model to AgreementEntity`() {
        val result = testAgreement.toEntity()

        assertEquals(testAgreementEntity, result)
    }
}
