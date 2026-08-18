package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.data.persistence.entity.AddressEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.location.Address
import se.supernovait.app.core.domain.location.AddressType
import se.supernovait.doobypro.data.local.entity.CompanyEntity
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.IdType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [CompanyMapper].
 */
class CompanyMapperTest {
    private val companyId = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix)
    private val addressId = SupernovaIdGenerator.generateId()

    private val testAddress = Address(
        id = addressId,
        street = "Main St",
        city = "Dubai",
        country = "UAE"
    )

    private val testAddressEntity = AddressEntity(
        id = addressId,
        type = AddressType.RESIDENTIAL,
        street = "Main St",
        city = "Dubai",
        subdivision = "Dubai",
        postalCode = null,
        country = "UAE",
        notes = null,
        coordinates = null
    )

    private val testCompany = Company(
        id = companyId,
        legalName = "Legal Name",
        displayName = "Display Name",
        licenseNumber = "LIC-123",
        phoneNumber = "123456789",
        email = "info@company.com",
        address = testAddress,
        logoUrl = "https://logo.com/image.png"
    )

    private val testCompanyEntity = CompanyEntity(
        id = companyId,
        legalName = "Legal Name",
        displayName = "Display Name",
        licenseNumber = "LIC-123",
        phoneNumber = "123456789",
        email = "info@company.com",
        address = testAddressEntity,
        logoUrl = "https://logo.com/image.png"
    )

    @Test
    fun `toDomain should correctly transform CompanyEntity to Company model`() {
        val result = testCompanyEntity.toDomain()

        assertEquals(testCompany.id, result.id)
        assertEquals(testCompany.legalName, result.legalName)
        assertEquals(testCompany.displayName, result.displayName)
        assertEquals(testCompany.licenseNumber, result.licenseNumber)
        assertEquals(testCompany.phoneNumber, result.phoneNumber)
        assertEquals(testCompany.email, result.email)
        assertEquals(testCompany.address?.street, result.address?.street)
        assertEquals(testCompany.logoUrl, result.logoUrl)
    }

    @Test
    fun `toEntity should correctly transform Company model to CompanyEntity`() {
        val result = testCompany.toEntity()

        assertEquals(testCompanyEntity.id, result.id)
        assertEquals(testCompanyEntity.legalName, result.legalName)
        assertEquals(testCompanyEntity.displayName, result.displayName)
        assertEquals(testCompanyEntity.licenseNumber, result.licenseNumber)
        assertEquals(testCompanyEntity.phoneNumber, result.phoneNumber)
        assertEquals(testCompanyEntity.email, result.email)
        assertEquals(testCompanyEntity.address?.street, result.address?.street)
        assertEquals(testCompanyEntity.logoUrl, result.logoUrl)
    }

    @Test
    fun `toDomain should handle null address`() {
        val entityWithoutAddress = testCompanyEntity.copy(address = null)
        val result = entityWithoutAddress.toDomain()

        assertEquals(null, result.address)
    }

    @Test
    fun `toEntity should handle null address`() {
        val modelWithoutAddress = testCompany.copy(address = null)
        val result = modelWithoutAddress.toEntity()

        assertEquals(null, result.address)
    }
}
