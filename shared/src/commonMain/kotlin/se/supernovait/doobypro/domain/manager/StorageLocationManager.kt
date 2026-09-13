package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.first
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository

/**
 * Manager responsible for orchestrating storage location resources across the system.
 */
class StorageLocationManager(
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository
) {
    /**
     * Assigns a storage location to an order based on the current allocation mode.
     */
    suspend fun assignStorageLocation(selectedLocationId: String?): String {
        val appSettings = settingsRepository.settings.first()
        val orderSettings = appSettings.order

        return when (orderSettings.storageAllocationMode) {
            StorageAllocationMode.MANUAL -> {
                if (selectedLocationId == null) {
                    throw IllegalArgumentException("Storage location is required in manual mode.")
                }
                
                val location = storageLocationRepository.getLocationById(selectedLocationId).getOrNull()
                    ?: throw IllegalArgumentException("Selected storage location not found.")

                // If selected is full, try the user's preferred default from settings
                if (!location.hasCapacity()) {
                    val preferredDefault = storageLocationRepository.getLocationById(orderSettings.defaultStorageLocationId).getOrNull()
                    
                    val fallback = if (preferredDefault != null && preferredDefault.hasCapacity()) {
                        preferredDefault
                    } else {
                        // Finally fall back to global default (Uncategorized)
                        storageLocationRepository.getDefaultLocation().getOrNull()
                    } ?: throw IllegalStateException("Selected storage location is full and no default is available.")
                    
                    storageLocationRepository.incrementOccupiedSlots(fallback.id!!)
                    return fallback.id
                }

                storageLocationRepository.incrementOccupiedSlots(selectedLocationId)
                selectedLocationId
            }
            StorageAllocationMode.AUTO -> {
                // 1. Try user's preferred default from settings if slots available
                val preferredDefault = storageLocationRepository.getLocationById(orderSettings.defaultStorageLocationId).getOrNull()
                
                var target = if (preferredDefault != null && preferredDefault.hasCapacity()) {
                    preferredDefault
                } else {
                    // 2. Try first available non-default location
                    storageLocationRepository.getActiveLocations().first()
                        .filter { !it.isDefault && it.id != orderSettings.defaultStorageLocationId }
                        .firstOrNull { it.hasCapacity() }
                }

                // 3. Finally fall back to global default (Uncategorized)
                if (target == null) {
                    target = storageLocationRepository.getDefaultLocation().getOrNull()
                }

                val finalTarget = target ?: throw IllegalStateException("No storage locations available.")
                
                storageLocationRepository.incrementOccupiedSlots(finalTarget.id!!)
                finalTarget.id
            }
        }
    }

    /**
     * Releases an occupied slot from the specified storage location.
     */
    suspend fun releaseStorageLocation(locationId: String) {
        storageLocationRepository.decrementOccupiedSlots(locationId)
    }

    /**
     * Initializes the system with a default "Uncategorized" storage area if none exists.
     */
    suspend fun initializeDefaultStorageLocation() {
        val result = storageLocationRepository.getDefaultLocation()
        if (result is Result.Failure && result.error == DataError.NOT_FOUND) {
            storageLocationRepository.saveLocation(
                StorageLocation(
                    id = "default",
                    label = "Uncategorized",
                    type = StorageType.OTHER,
                    capacity = 0, // Unlimited
                    isDefault = true
                )
            )
        }
    }
}
