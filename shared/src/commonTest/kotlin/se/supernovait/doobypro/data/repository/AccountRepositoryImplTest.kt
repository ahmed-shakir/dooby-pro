package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.location.Address
import se.supernovait.doobypro.data.local.dao.FakeAccountDao
import se.supernovait.doobypro.data.local.dao.FakeAgreementDao
import se.supernovait.doobypro.data.local.dao.FakeCompanyDao
import se.supernovait.doobypro.data.local.dao.FakeLicenseDao
import se.supernovait.doobypro.data.local.dao.FakeUserDao
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import se.supernovait.app.core.data.persistence.mapper.toEntity as mapUserToEntity

/**
 * Unit tests for [AccountRepositoryImpl].
 */
class AccountRepositoryImplTest {
    private lateinit var fakeAccountDao: FakeAccountDao
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakeCompanyDao: FakeCompanyDao
    private lateinit var fakeLicenseDao: FakeLicenseDao
    private lateinit var fakeAgreementDao: FakeAgreementDao
    private lateinit var repository: AccountRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

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

    @BeforeTest
    fun setUp() {
        fakeAccountDao = FakeAccountDao()
        fakeUserDao = FakeUserDao()
        fakeCompanyDao = FakeCompanyDao()
        fakeLicenseDao = FakeLicenseDao()
        fakeAgreementDao = FakeAgreementDao()

        repository = AccountRepositoryImpl(
            accountDao = fakeAccountDao,
            userDao = fakeUserDao,
            companyDao = fakeCompanyDao,
            licenseDao = fakeLicenseDao,
            agreementDao = fakeAgreementDao
        )

        // Seed component data
        runTest(testDispatcher) {
            fakeUserDao.upsert(testUser.mapUserToEntity())
            fakeCompanyDao.upsert(testCompany.toEntity())
        }
    }

    @Test
    fun `getAccount should return assembled account if found`() = runTest(testDispatcher) {
        repository.upsertAccount(testAccount)

        val result = repository.getAccount(accountId)

        assertNotNull(result)
        assertEquals(accountId, result.id)
        assertEquals(testUser.username, result.user.username)
        assertEquals(testCompany.legalName, result.company.legalName)
    }

    @Test
    fun `getAccount should return null if account entity not found`() = runTest(testDispatcher) {
        val result = repository.getAccount("unknown")

        assertNull(result)
    }

    @Test
    fun `upsertAccount should call dao upsert`() = runTest(testDispatcher) {
        repository.upsertAccount(testAccount)

        val savedEntity = fakeAccountDao.getById(accountId)
        assertNotNull(savedEntity)
        assertEquals(accountId, savedEntity.id)
        assertEquals(userId, savedEntity.userId)
    }

    @Test
    fun `deleteAccount should call dao delete`() = runTest(testDispatcher) {
        repository.upsertAccount(testAccount)
        assertNotNull(repository.getAccount(accountId))

        repository.deleteAccount(testAccount)

        assertNull(repository.getAccount(accountId))
    }
}
