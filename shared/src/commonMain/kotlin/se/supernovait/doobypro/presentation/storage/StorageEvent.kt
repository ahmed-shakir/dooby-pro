package se.supernovait.doobypro.presentation.storage

import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType

sealed interface StorageEvent {
    data object LoadLocations : StorageEvent
    data class EditLocation(val location: StorageLocation) : StorageEvent
    data class SaveLocation(
        val label: String,
        val type: StorageType,
        val capacity: Int
    ) : StorageEvent
    data class DeleteLocation(val location: StorageLocation) : StorageEvent
}