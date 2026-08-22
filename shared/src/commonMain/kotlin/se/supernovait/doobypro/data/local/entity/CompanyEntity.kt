package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import se.supernovait.app.core.data.persistence.entity.AddressEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import kotlin.time.Clock
import kotlin.time.Instant

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
 * @property createdAt The timestamp when the company was created.
 * @property updatedAt The timestamp when the company was last updated.
 */
@Entity(tableName = "companies")
data class CompanyEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix),
    val legalName: String,
    val displayName: String,
    val licenseNumber: String,
    val phoneNumber: String,
    val email: String,
    @Embedded(prefix = "company_address_")
    val address: AddressEntity?,
    val logoUrl: String?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
)
