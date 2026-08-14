package se.supernovait.doobypro.data.local.entity

import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit tests for [CompanyEntity].
 */
class CompanyEntityTest {

    @Test
    fun `CompanyEntity should generate a valid ID with correct prefix by default`() {
        val entity = CompanyEntity(
            legalName = "Legal",
            displayName = "Display",
            licenseNumber = "123",
            phoneNumber = "123",
            email = "test@test.com",
            address = null,
            logoUrl = null
        )

        assertTrue(entity.id.startsWith(IdType.COMPANY.prefix), "ID should start with ${IdType.COMPANY.prefix}")
        assertTrue(entity.id.length > IdType.COMPANY.prefix.length, "ID should have more characters after prefix")
    }
}
