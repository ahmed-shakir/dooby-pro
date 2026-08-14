package se.supernovait.doobypro.data.local.mapper

import se.supernovait.doobypro.data.local.entity.ServiceEntity
import se.supernovait.doobypro.domain.model.Service

fun ServiceEntity.mapToModel(): Service {
    return Service(
        id = id,
        title = title,
        description = description,
        price = price.mapToModel()
    )
}

fun Service.mapToEntity(): ServiceEntity {
    return ServiceEntity(
        id = id,
        title = title,
        description = description,
        price = price.mapToEntity()
    )
}
