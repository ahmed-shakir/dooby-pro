package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import se.supernovait.app.core.data.persistence.entity.AddressEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.DoobyIdType

/**
 * Database entity representing a company profile for Room persistence.
 *
 * @property id The unique identifier for the company, defaults to a generated ID.
 * @property legalName The official registered name.
 * @property displayName The branding name.
 * @property licenseNumber The trade license number.
 * @property phoneNumber The contact phone number.
 * @property email The contact email.
 * @property address The physical address, embedded in the table.
 * @property logoUrl The URL to the company logo.
 */
@Entity(tableName = "companies")
data class CompanyEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(DoobyIdType.COMPANY.prefix),
    val legalName: String,
    val displayName: String,
    val licenseNumber: String,
    val phoneNumber: String,
    val email: String,
    @Embedded(prefix = "company_address_")
    val address: AddressEntity?,
    val logoUrl: String?
)
