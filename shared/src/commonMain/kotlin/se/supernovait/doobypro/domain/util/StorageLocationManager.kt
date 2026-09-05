package se.supernovait.doobypro.domain.util

import kotlinx.coroutines.flow.first
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository

/**
 * Manager responsible for orchestrating storage location resources across the system.
 */
class StorageLocationManager(
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository,
    private val orderRepository: OrderRepository
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
                
                if (!location.hasCapacity()) {
                    throw IllegalStateException("Selected storage location is full.")
                }
                
                storageLocationRepository.incrementOccupiedSlots(selectedLocationId)
                selectedLocationId
            }
            StorageAllocationMode.AUTO -> {
                val availableLocations = storageLocationRepository.getActiveLocations().first()
                val targetLocation = availableLocations
                    .filter { !it.isDefault }
                    .firstOrNull { it.hasCapacity() }
                    ?: storageLocationRepository.getDefaultLocation().getOrNull()
                    ?: throw IllegalStateException("No storage locations available, including default.")
                
                storageLocationRepository.incrementOccupiedSlots(targetLocation.id!!)
                targetLocation.id
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
