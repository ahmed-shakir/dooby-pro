package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.StorageLocationEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import kotlin.time.Clock

/**
 * Extension function to map [StorageLocationEntity] to [StorageLocation] domain model.
 *
 * @return The mapped [StorageLocation] model.
 */
fun StorageLocationEntity.toDomain() = StorageLocation(
    id = id,
    label = label,
    type = type,
    capacity = capacity,
    occupiedSlots = occupiedSlots,
    isDefault = isDefault,
    isActive = isActive,
    createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
    updatedAt = updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
)

/**
 * Extension function to map [StorageLocation] domain model to [StorageLocationEntity].
 *
 * @return The mapped [StorageLocationEntity] model.
 */
fun StorageLocation.toEntity() = StorageLocationEntity(
    id = id ?: SupernovaIdGenerator.generateId(IdType.STORAGE_LOCATION.prefix),
    label = label,
    type = type,
    capacity = capacity,
    occupiedSlots = occupiedSlots,
    isDefault = isDefault,
    isActive = isActive,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
)
