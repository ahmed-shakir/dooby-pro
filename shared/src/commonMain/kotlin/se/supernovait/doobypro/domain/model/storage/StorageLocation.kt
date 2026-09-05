package se.supernovait.doobypro.domain.model.storage

import kotlinx.serialization.Serializable

/**
 * Domain model representing a physical storage area in the laundry facility.
 *
 * Each location (e.g., "Shelf A", "Hanger Rack 1") has a defined capacity of slots.
 * The system tracks occupancy to ensure that only available slots are assigned to orders.
 *
 * @property id Unique identifier for the storage location.
 * @property label Human-readable label for the storage area.
 * @property type The physical category of storage, influencing how items are stored.
 * @property capacity The maximum number of orders/items this area can hold. 0 indicates unlimited capacity.
 * @property occupiedSlots The current number of slots filled by active orders.
 * @property isDefault If true, this location acts as the global fallback ("Default Storage Area") 
 * when no other slots are available or assigned. The default area typically has unlimited capacity.
 * @property isActive Whether this storage area is currently operational and available for new assignments.
 */
@Serializable
data class StorageLocation(
    val id: String? = null,
    val label: String = "",
    val type: StorageType = StorageType.OTHER,
    val capacity: Int = 0,
    val occupiedSlots: Int = 0,
    val isDefault: Boolean = false,
    val isActive: Boolean = true
) {
    /**
     * Returns true if the location has at least one slot available for a new order.
     */
    fun hasCapacity(): Boolean = capacity == 0 || occupiedSlots < capacity

    /**
     * Calculates the number of slots remaining in this storage area.
     */
    val remainingSlots: Int
        get() = if (capacity == 0) Int.MAX_VALUE else (capacity - occupiedSlots).coerceAtLeast(0)
}
