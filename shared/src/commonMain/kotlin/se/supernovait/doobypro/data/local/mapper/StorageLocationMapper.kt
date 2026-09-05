package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.StorageLocationEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.storage.StorageLocation

fun StorageLocationEntity.toDomain() = StorageLocation(
    id = id,
    label = label,
    type = type,
    capacity = capacity,
    occupiedSlots = occupiedSlots,
    isDefault = isDefault,
    isActive = isActive
)

fun StorageLocation.toEntity() = StorageLocationEntity(
    id = id ?: SupernovaIdGenerator.generateId(IdType.STORAGE_LOCATION.prefix),
    label = label,
    type = type,
    capacity = capacity,
    occupiedSlots = occupiedSlots,
    isDefault = isDefault,
    isActive = isActive
)
