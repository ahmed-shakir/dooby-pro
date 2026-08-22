package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.location.Address
import se.supernovait.doobypro.data.local.dao.FakeCompanyDao
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [CompanyRepositoryImpl].
 */
class CompanyRepositoryImplTest {
    private lateinit var fakeCompanyDao: FakeCompanyDao
    private lateinit var repository: CompanyRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val testCompany = Company(
        id = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix),
        legalName = "Legal Name",
        displayName = "Display Name",
        licenseNumber = "LIC-123",
        phoneNumber = "123456789",
        email = "info@company.com",
        address = Address(
            id = SupernovaIdGenerator.generateId(),
            street = "Main",
            city = "Dubai",
            country = "UAE"
        ),
        logoUrl = null
    )

    @BeforeTest
    fun setUp() {
        fakeCompanyDao = FakeCompanyDao()
        repository = CompanyRepositoryImpl(
            companyDao = fakeCompanyDao
        )
    }

    @Test
    fun `getCompanies should return all companies mapped to models`() = runTest(testDispatcher) {
        repository.saveCompany(testCompany)

        val companies = repository.getCompanies().first()

        assertEquals(1, companies.size)
        assertEquals(testCompany.id, companies[0].id)
        assertEquals(testCompany.legalName, companies[0].legalName)
    }

    @Test
    fun `getCompanyById should return mapped model if found`() = runTest(testDispatcher) {
        repository.saveCompany(testCompany)

        val result = repository.getCompanyById(testCompany.id!!)

        assertEquals(testCompany.id, result.getOrNull()?.id)
        assertEquals(testCompany.legalName, result.getOrNull()?.legalName)
    }

    @Test
    fun `getCompanyById should return null if not found`() = runTest(testDispatcher) {
        val result = repository.getCompanyById("non-existent")

        assertTrue(result.isFailure)
        assertNull(result.getOrNull())
    }

    @Test
    fun `upsertCompany should call dao upsert`() = runTest(testDispatcher) {
        repository.saveCompany(testCompany)

        val savedEntity = fakeCompanyDao.getById(testCompany.id!!)
        assertEquals(testCompany.id, savedEntity?.id)
    }

    @Test
    fun `deleteCompany should call dao delete`() = runTest(testDispatcher) {
        repository.saveCompany(testCompany)
        assertEquals(1, repository.getCompanies().first().size)

        repository.deleteCompany(testCompany)

        assertEquals(0, repository.getCompanies().first().size)
    }
}
