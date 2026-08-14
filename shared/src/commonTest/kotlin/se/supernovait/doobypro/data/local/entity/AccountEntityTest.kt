package se.supernovait.doobypro.data.local.entity

import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [AccountEntity].
 */
class AccountEntityTest {

    @Test
    fun `AccountEntity should correctly hold component IDs`() {
        val accountId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)
        val userId = SupernovaIdGenerator.generateId(IdType.USER.prefix)
        val licenseId = SupernovaIdGenerator.generateId(IdType.LICENSE.prefix)
        val agreementId = SupernovaIdGenerator.generateId(IdType.AGREEMENT.prefix)

        val entity = AccountEntity(
            id = accountId,
            userId = userId,
            licenseId = licenseId,
            agreementId = agreementId
        )

        assertEquals(accountId, entity.id)
        assertEquals(userId, entity.userId)
        assertEquals(licenseId, entity.licenseId)
        assertEquals(agreementId, entity.agreementId)
    }
}
