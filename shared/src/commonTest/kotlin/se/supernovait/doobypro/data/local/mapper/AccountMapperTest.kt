package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.location.Address
import se.supernovait.doobypro.data.local.entity.AccountEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.company.Company
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [AccountMapper].
 */
class AccountMapperTest {
    private val accountId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)
    private val userId = SupernovaIdGenerator.generateId(IdType.USER.prefix)
    private val testDateTime = LocalDateTime(2026, 9, 12, 18, 35, 34)
    private val testInstant = testDateTime.toInstant(TimeZone.currentSystemDefault())

    private val testUser = User(
        id = userId,
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com",
        createdAt = testDateTime,
        updatedAt = testDateTime,
        statusChangedAt = testDateTime
    )

    private val testCompany = Company(
        id = accountId,
        legalName = "Legal Name",
        displayName = "Display Name",
        licenseNumber = "LIC-123",
        phoneNumber = "123456789",
        email = "info@company.com",
        address = Address(street = "Main", city = "Dubai", country = "UAE", createdAt = testDateTime, updatedAt = testDateTime),
        logoUrl = null,
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    private val testAccount = Account(
        id = accountId,
        user = testUser,
        company = testCompany,
        license = null,
        agreements = emptyList(),
        createdAt = testDateTime,
        updatedAt = testDateTime
    )

    private val testAccountEntity = AccountEntity(
        id = accountId,
        userId = userId,
        licenseId = null,
        agreementIds = emptyList(),
        createdAt = testInstant,
        updatedAt = testInstant
    )

    @Test
    fun `toDomain should correctly transform AccountEntity to Account model`() {
        val result = testAccountEntity.toDomain(testUser, testCompany, null, emptyList())

        assertEquals(testAccount, result)
    }

    @Test
    fun `toEntity should correctly transform Account model to AccountEntity`() {
        val result = testAccount.toEntity()

        assertEquals(testAccountEntity.copy(updatedAt = result.updatedAt), result)
    }
}
