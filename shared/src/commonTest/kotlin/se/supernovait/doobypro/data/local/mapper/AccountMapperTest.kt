package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.location.Address
import se.supernovait.doobypro.data.local.entity.AccountEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [AccountMapper].
 */
class AccountMapperTest {
    private val accountId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)
    private val userId = SupernovaIdGenerator.generateId(IdType.USER.prefix)

    private val testUser = User(
        id = userId,
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    private val testCompany = Company(
        id = accountId,
        legalName = "Legal Name",
        displayName = "Display Name",
        licenseNumber = "LIC-123",
        phoneNumber = "123456789",
        email = "info@company.com",
        address = Address(street = "Main", city = "Dubai", country = "UAE"),
        logoUrl = null
    )

    private val testAccount = Account(
        id = accountId,
        user = testUser,
        company = testCompany,
        license = null,
        agreement = null
    )

    private val testAccountEntity = AccountEntity(
        id = accountId,
        userId = userId,
        licenseId = null,
        agreementId = null
    )

    @Test
    fun `toDomain should correctly transform AccountEntity to Account model`() {
        val result = testAccountEntity.toDomain(testUser, testCompany, null, null)

        assertEquals(testAccount, result)
    }

    @Test
    fun `toEntity should correctly transform Account model to AccountEntity`() {
        val result = testAccount.toEntity()

        assertEquals(testAccountEntity, result)
    }
}
