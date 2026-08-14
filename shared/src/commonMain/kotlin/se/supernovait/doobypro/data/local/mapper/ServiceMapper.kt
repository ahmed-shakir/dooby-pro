package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.ServiceEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.Service

/**
 * Extension function to map [ServiceEntity] to [Service] domain model.
 *
 * @return The mapped [Service] model.
 */
fun ServiceEntity.toDomain(): Service {
    return Service(
        id = id,
        title = title,
        description = description,
        price = price.toDomain()
    )
}

/**
 * Extension function to map [Service] domain model to [ServiceEntity].
 *
 * @return The mapped [ServiceEntity].
 */
fun Service.toEntity(): ServiceEntity {
    return ServiceEntity(
        id = id ?: SupernovaIdGenerator.generateId(IdType.SERVICE.prefix),
        title = title,
        description = description,
        price = price.toEntity()
    )
}
