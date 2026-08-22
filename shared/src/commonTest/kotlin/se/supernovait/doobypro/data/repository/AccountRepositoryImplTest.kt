package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.location.Address
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.app.core.domain.model.license.LicenseStatus
import se.supernovait.app.core.domain.model.license.Tier
import se.supernovait.doobypro.data.local.dao.FakeAccountDao
import se.supernovait.doobypro.data.local.dao.FakeUserDao
import se.supernovait.doobypro.data.local.entity.AccountEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.agreement.Agreement
import se.supernovait.doobypro.domain.model.agreement.AgreementStatus
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [AccountRepositoryImpl].
 */
class AccountRepositoryImplTest {
    private lateinit var fakeAccountDao: FakeAccountDao
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeCompanyRepository: FakeCompanyRepository
    private lateinit var fakeLicenseRepository: FakeLicenseRepository
    private lateinit var fakeAgreementRepository: FakeAgreementRepository
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
        license = License(
            id = "lic-123",
            accountId = accountId,
            licenseStatus = LicenseStatus.Active,
            tier = Tier.Free,
            title = "Test License",
            description = "Test Desc",
            issueDate = LocalDate(2026, 1, 1),
            expiryDate = LocalDate(2027, 1, 1)
        ),
        agreements = emptyList()
    )

    @BeforeTest
    fun setUp() {
        fakeAccountDao = FakeAccountDao()
        fakeUserDao = FakeUserDao()
        fakeAuthRepository = FakeAuthRepository()
        fakeCompanyRepository = FakeCompanyRepository()
        fakeLicenseRepository = FakeLicenseRepository()
        fakeAgreementRepository = FakeAgreementRepository()

        repository = AccountRepositoryImpl(
            authRepository = fakeAuthRepository,
            companyRepository = fakeCompanyRepository,
            licenseRepository = fakeLicenseRepository,
            agreementRepository = fakeAgreementRepository,
            accountDao = fakeAccountDao,
            userDao = fakeUserDao
        )
    }

    @Test
    fun `getAccount should return assembled account with components if found`() = runTest(testDispatcher) {
        // Seed component data
        fakeAuthRepository.signUp(testUser)
        fakeCompanyRepository.saveCompany(testCompany)

        // Mock account link in root table
        fakeAccountDao.upsert(AccountEntity(
            id = accountId,
            userId = userId,
            licenseId = null,
            agreementIds = emptyList()
        ))

        val result = repository.getAccount(accountId).getOrNull()

        assertNotNull(result)
        assertEquals(accountId, result.id)
        assertEquals(testUser.username, result.user.username)
        assertEquals(testCompany.legalName, result.company.legalName)
    }

    @Test
    fun `getAccount should return assembled account with multiple agreements`() = runTest(testDispatcher) {
        // Seed component data
        fakeAuthRepository.signUp(testUser)
        fakeCompanyRepository.saveCompany(testCompany)
        
        val agr1 = Agreement(
            id = "agr-1",
            accountId = accountId,
            status = AgreementStatus.ACTIVE,
            equipmentId = "EQ-1",
            equipmentModel = "M1",
            title = "Title 1",
            description = "Desc 1",
            billingFrequency = BillingFrequency.Monthly,
            fee = Amount(10000, "AED"),
            deposit = Amount(0, "AED"),
            issueDate = LocalDate(2026, 1, 1)
        )
        val agr2 = agr1.copy(id = "agr-2", title = "Title 2")
        fakeAgreementRepository.saveAgreement(agr1)
        fakeAgreementRepository.saveAgreement(agr2)

        // Mock account link in root table
        fakeAccountDao.upsert(AccountEntity(
            id = accountId,
            userId = userId,
            licenseId = null,
            agreementIds = listOf("agr-1", "agr-2")
        ))

        val result = repository.getAccount(accountId).getOrNull()

        assertNotNull(result)
        assertEquals(2, result.agreements.size)
        assertEquals("Title 1", result.agreements[0].title)
        assertEquals("Title 2", result.agreements[1].title)
    }

    @Test
    fun `saveAccount for new account should trigger full orchestration`() = runTest(testDispatcher) {
        // Create an account without an ID to trigger 'saveNewAccount'
        val newAccount = testAccount.copy(id = null)
        
        val result = repository.saveAccount(newAccount)
        
        assertTrue(result.isSuccess, "Save account should be successful")
        val savedAccountId = result.getOrNull()!!
        
        // Verify User was signed up
        val savedUser = fakeAuthRepository.getUserById(userId).getOrNull()
        assertNotNull(savedUser)
        
        // Verify Company was saved
        val savedCompany = fakeCompanyRepository.getCompanyById(savedAccountId).getOrNull()
        assertNotNull(savedCompany)
    }

    @Test
    fun `upsertAccount should call dao upsert`() = runTest(testDispatcher) {
        repository.saveAccount(testAccount)

        val savedEntity = fakeAccountDao.getById(accountId)
        assertNotNull(savedEntity)
        assertEquals(accountId, savedEntity.id)
        assertEquals(userId, savedEntity.userId)
    }

    @Test
    fun `deleteAccount should call dao delete`() = runTest(testDispatcher) {
        // Manually seed an account since getAccount needs sub-repo data
        fakeAuthRepository.signUp(testUser)
        fakeCompanyRepository.saveCompany(testCompany)
        fakeAccountDao.upsert(AccountEntity(
            id = accountId, 
            userId = userId,
            licenseId = null,
            agreementIds = emptyList()
        ))
        
        assertNotNull(repository.getAccount(accountId).getOrNull())

        repository.deleteAccount(accountId)

        val deletedAccount = repository.getAccount(accountId).getOrNull()
        assertNotNull(deletedAccount)
        assertTrue(deletedAccount.isMarkedForDeletion)
    }
}
