package se.supernovait.doobypro.domain.model.settings.storage

import kotlinx.serialization.Serializable
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode

/**
 * Settings related to storage management and processing.
 *
 * @property storageAllocationMode The current mode for assigning storage slots to orders.
 * @property defaultStorageLocationId The ID of the location used when no other slot is available.
 */
@Serializable
data class StorageSettings(
    val storageAllocationMode: StorageAllocationMode = StorageAllocationMode.AUTO,
    val defaultStorageLocationId: String = "default"
)
