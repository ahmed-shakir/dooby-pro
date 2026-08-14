package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.AgreementEntity
import se.supernovait.doobypro.domain.model.Agreement
import se.supernovait.doobypro.domain.model.DoobyIdType

/**
 * Extension function to map [AgreementEntity] to [Agreement] domain model.
 *
 * @return The mapped [Agreement] model.
 */
fun AgreementEntity.toDomain(): Agreement {
    return Agreement(
        id = id,
        status = status,
        equipmentId = equipmentId,
        equipmentModel = equipmentModel,
        title = title,
        description = description,
        billingFrequency = billingFrequency,
        fee = fee.toDomain(),
        deposit = deposit.toDomain(),
        issueDate = issueDate,
        cancellationDate = cancellationDate
    )
}

/**
 * Extension function to map [Agreement] domain model to [AgreementEntity].
 *
 * @return The mapped [AgreementEntity].
 */
fun Agreement.toEntity(): AgreementEntity {
    return AgreementEntity(
        id = id ?: SupernovaIdGenerator.generateId(DoobyIdType.AGREEMENT.prefix),
        status = status,
        equipmentId = equipmentId,
        equipmentModel = equipmentModel,
        title = title,
        description = description,
        billingFrequency = billingFrequency,
        fee = fee.toEntity(),
        deposit = deposit.toEntity(),
        issueDate = issueDate,
        cancellationDate = cancellationDate
    )
}
