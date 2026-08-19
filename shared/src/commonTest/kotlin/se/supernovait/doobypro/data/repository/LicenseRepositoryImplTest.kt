package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.app.core.domain.model.license.LicenseStatus
import se.supernovait.app.core.domain.model.license.Tier
import se.supernovait.doobypro.data.local.dao.FakeLicenseDao
import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [LicenseRepositoryImpl].
 */
class LicenseRepositoryImplTest {
    private lateinit var fakeLicenseDao: FakeLicenseDao
    private lateinit var repository: LicenseRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val licenseId = SupernovaIdGenerator.generateId(IdType.LICENSE.prefix)
    private val accountId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)

    private val testLicense = License(
        id = licenseId,
        accountId = accountId,
        licenseStatus = LicenseStatus.ACTIVE,
        tier = Tier.FREE,
        title = "Professional License",
        description = "Full access to pro features",
        issueDate = LocalDate(2026, 8, 15),
        expiryDate = LocalDate(2027, 8, 15)
    )

    @BeforeTest
    fun setUp() {
        fakeLicenseDao = FakeLicenseDao()
        repository = LicenseRepositoryImpl(
            licenseDao = fakeLicenseDao
        )
    }

    @Test
    fun `getLicenses should return all licenses for account mapped to models`() = runTest(testDispatcher) {
        repository.saveLicense(testLicense)
        repository.saveLicense(testLicense.copy(
            id = SupernovaIdGenerator.generateId(IdType.LICENSE.prefix), 
            accountId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)
        ))

        val licenses = repository.getLicenses(accountId)

        assertEquals(1, licenses.size)
        assertEquals(testLicense, licenses[0])
    }

    @Test
    fun `getLicenseById should return mapped model if found`() = runTest(testDispatcher) {
        repository.saveLicense(testLicense)

        val result = repository.getLicenseById(testLicense.id)

        assertEquals(testLicense, result.getOrNull())
    }

    @Test
    fun `getLicenseById should return null if not found`() = runTest(testDispatcher) {
        val result = repository.getLicenseById("non-existent")

        assertTrue(result.isFailure)
        assertNull(result.getOrNull())
    }

    @Test
    fun `upsertLicense should call dao upsert`() = runTest(testDispatcher) {
        repository.saveLicense(testLicense)

        val savedEntity = fakeLicenseDao.getById(testLicense.id)
        assertEquals(testLicense.id, savedEntity?.id)
        assertEquals(testLicense.title, savedEntity?.title)
    }

    @Test
    fun `deleteLicense should call dao delete`() = runTest(testDispatcher) {
        repository.saveLicense(testLicense)
        assertEquals(1, repository.getLicenses(accountId).size)

        repository.deleteLicense(testLicense)

        assertEquals(0, repository.getLicenses(accountId).size)
    }
}
