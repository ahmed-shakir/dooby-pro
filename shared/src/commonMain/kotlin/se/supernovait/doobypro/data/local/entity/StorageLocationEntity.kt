package se.supernovait.doobypro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.storage.StorageType

/**
 * Database entity for storage areas in the persistent store.
 *
 * @property id Unique identifier for the storage location.
 * @property label Human-readable label for the storage area.
 * @property type The physical category of storage.
 * @property capacity The maximum number of orders/items this area can hold.
 * @property occupiedSlots The current number of slots filled by active orders.
 * @property isDefault Whether this is the fallback "Default Storage Area".
 * @property isActive Whether the location is operational.
 */
@Entity(
    tableName = "storage_locations",
    indices = [
        Index(value = ["isDefault"]),
        Index(value = ["isActive"])
    ]
)
data class StorageLocationEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(IdType.STORAGE_LOCATION.prefix),
    val label: String,
    val type: StorageType,
    val capacity: Int,
    val occupiedSlots: Int = 0,
    val isDefault: Boolean = false,
    val isActive: Boolean = true
)
