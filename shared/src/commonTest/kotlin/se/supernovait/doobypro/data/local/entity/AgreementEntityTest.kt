package se.supernovait.doobypro.data.local.entity

import kotlinx.datetime.LocalDate
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.doobypro.domain.model.AgreementStatus
import se.supernovait.doobypro.domain.model.DoobyIdType
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for [AgreementEntity].
 */
class AgreementEntityTest {

    @Test
    fun `AgreementEntity should generate a valid ID with correct prefix by default`() {
        val entity = AgreementEntity(
            status = AgreementStatus.ACTIVE,
            equipmentId = "SN-1",
            equipmentModel = "M1",
            title = "Title",
            description = "Desc",
            fee = AmountEntity(100, "AED"),
            billingFrequency = BillingFrequency.MONTHLY,
            deposit = AmountEntity(500, "AED"),
            issueDate = LocalDate(2026, 8, 15),
            cancellationDate = null
        )

        assertTrue(entity.id.startsWith(DoobyIdType.AGREEMENT.prefix), "ID should start with ${DoobyIdType.AGREEMENT.prefix}")
        assertTrue(entity.id.length > DoobyIdType.AGREEMENT.prefix.length, "ID should have more characters after prefix")
    }
}
