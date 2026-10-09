package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.first
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.observability.performance.PerformanceMonitor
import se.supernovait.app.core.domain.observability.performance.traceAsync
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.domain.util.LogTags

/**
 * Manager responsible for orchestrating storage location resources across the system.
 */
class StorageLocationManager(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val performanceMonitor: PerformanceMonitor,
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository
) {
    /**
     * Assigns a storage location to an order based on the current allocation mode.
     */
    suspend fun assignStorageLocation(selectedLocationId: String?): String {
        return performanceMonitor.traceAsync("StorageLocationManager.assignStorageLocation") {
            logger.info("Assigning storage location (selectedLocationId=$selectedLocationId)", tag = LogTags.STORAGE_LOCATION_MANAGER)
            val appSettings = settingsRepository.settings.first()
            val storageSettings = appSettings.storage

            when (storageSettings.storageAllocationMode) {
                StorageAllocationMode.MANUAL -> {
                    if (selectedLocationId == null) {
                        logger.warn("Storage location required in MANUAL mode but selectedLocationId is null", tag = LogTags.STORAGE_LOCATION_MANAGER)
                        throw IllegalArgumentException("Storage location is required in manual mode.")
                    }
                    
                    val location = storageLocationRepository.getLocationById(selectedLocationId).getOrNull()
                        if (location == null) {
                            logger.warn("Selected storage location not found with ID: $selectedLocationId", tag = LogTags.STORAGE_LOCATION_MANAGER)
                            throw IllegalArgumentException("Selected storage location not found.")
                        }

                    // If selected is full, try the user's preferred default from settings
                    if (!location.hasCapacity()) {
                        logger.warn("Selected storage location with ID: $selectedLocationId is full, trying default fallback", tag = LogTags.STORAGE_LOCATION_MANAGER)
                        val preferredDefault = storageLocationRepository.getLocationById(storageSettings.defaultStorageLocationId).getOrNull()
                        
                        val fallback = if (preferredDefault != null && preferredDefault.hasCapacity()) {
                            preferredDefault
                        } else {
                            // Finally fall back to global default (Uncategorized)
                            storageLocationRepository.getDefaultLocation().getOrNull()
                        } ?: run {
                            logger.error("Selected storage location with ID: $selectedLocationId is full and no fallback default available", tag = LogTags.STORAGE_LOCATION_MANAGER)
                            val exc = IllegalStateException("Selected storage location is full and no default is available.")
                            crashReporter.recordException(exc, mapOf("selectedLocationId" to selectedLocationId))
                            throw exc
                        }
                        
                        storageLocationRepository.incrementOccupiedSlots(fallback.id!!)
                        logger.info("Allocated fallback storage location with ID: ${fallback.id}", tag = LogTags.STORAGE_LOCATION_MANAGER)
                        return@traceAsync fallback.id
                    }

                    storageLocationRepository.incrementOccupiedSlots(selectedLocationId)
                    logger.info("Allocated storage location with ID: $selectedLocationId", tag = LogTags.STORAGE_LOCATION_MANAGER)
                    selectedLocationId
                }
                StorageAllocationMode.AUTO -> {
                    // 1. Try user's preferred default from settings if slots available
                    val preferredDefault = storageLocationRepository.getLocationById(storageSettings.defaultStorageLocationId).getOrNull()
                    
                    var target = if (preferredDefault != null && preferredDefault.hasCapacity()) {
                        preferredDefault
                    } else {
                        // 2. Try first available non-default location
                        storageLocationRepository.getActiveLocations().first()
                            .filter { !it.isDefault && it.id != storageSettings.defaultStorageLocationId }
                            .firstOrNull { it.hasCapacity() }
                    }

                    // 3. Finally fall back to global default (Uncategorized)
                    if (target == null) {
                        target = storageLocationRepository.getDefaultLocation().getOrNull()
                    }

                    val finalTarget = target ?: run {
                        logger.error("Auto allocation failed: No storage locations available", tag = LogTags.STORAGE_LOCATION_MANAGER)
                        throw IllegalStateException("No storage locations available.")
                    }
                    
                    storageLocationRepository.incrementOccupiedSlots(finalTarget.id!!)
                    logger.info("Auto-allocated storage location with ID: ${finalTarget.id}", tag = LogTags.STORAGE_LOCATION_MANAGER)
                    finalTarget.id
                }
            }
        }
    }

    /**
     * Releases an occupied slot from the specified storage location.
     */
    suspend fun releaseStorageLocation(locationId: String) {
        performanceMonitor.traceAsync("StorageLocationManager.releaseStorageLocation") {
            logger.info("Releasing storage location slot for location with ID: $locationId", tag = LogTags.STORAGE_LOCATION_MANAGER)
            storageLocationRepository.decrementOccupiedSlots(locationId)
        }
    }
}
