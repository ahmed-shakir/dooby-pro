package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.CompanyEntity
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.IdType
import kotlin.time.Clock

/**
 * Extension function to map [CompanyEntity] to [Company] domain model.
 *
 * @return The mapped [Company] model.
 */
fun CompanyEntity.toDomain() = Company(
    id = id,
    legalName = legalName,
    displayName = displayName,
    licenseNumber = licenseNumber,
    phoneNumber = phoneNumber,
    email = email,
    address = address?.toDomain(),
    logoUrl = logoUrl,
    createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
    updatedAt = updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
)

/**
 * Extension function to map [Company] domain model to [CompanyEntity].
 *
 * @return The mapped [CompanyEntity].
 */
fun Company.toEntity() = CompanyEntity(
    id = id ?: SupernovaIdGenerator.generateId(IdType.COMPANY.prefix),
    legalName = legalName,
    displayName = displayName,
    licenseNumber = licenseNumber,
    phoneNumber = phoneNumber,
    email = email,
    address = address?.toEntity(),
    logoUrl = logoUrl,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
)
