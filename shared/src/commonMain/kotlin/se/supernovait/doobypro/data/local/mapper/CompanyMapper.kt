package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.CompanyEntity
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.DoobyIdType

/**
 * Extension function to map [CompanyEntity] to [Company] domain model.
 *
 * @return The mapped [Company] model.
 */
fun CompanyEntity.toDomain(): Company {
    return Company(
        id = id,
        legalName = legalName,
        displayName = displayName,
        licenseNumber = licenseNumber,
        phoneNumber = phoneNumber,
        email = email,
        address = address?.toDomain(),
        logoUrl = logoUrl
    )
}

/**
 * Extension function to map [Company] domain model to [CompanyEntity].
 *
 * @return The mapped [CompanyEntity].
 */
fun Company.toEntity(): CompanyEntity {
    return CompanyEntity(
        id = id ?: SupernovaIdGenerator.generateId(DoobyIdType.COMPANY.prefix),
        legalName = legalName,
        displayName = displayName,
        licenseNumber = licenseNumber,
        phoneNumber = phoneNumber,
        email = email,
        address = address?.toEntity(),
        logoUrl = logoUrl
    )
}
