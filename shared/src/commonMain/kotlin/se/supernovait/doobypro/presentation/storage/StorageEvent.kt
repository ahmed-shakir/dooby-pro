package se.supernovait.doobypro.presentation.storage

import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.model.storage.StorageType

/**
 * Events for the storage management domain.
 */
sealed interface StorageEvent {
    /** Re-fetches the list of all storage locations. */
    data object LoadLocations : StorageEvent

    /** Selects a storage location for details view or clears selection. */
    data class SelectLocation(val location: StorageLocation?) : StorageEvent

    /** Sets whether the edit form sheet is active. */
    data class SetEditing(val isEditing: Boolean) : StorageEvent

    /** Sets the storage location that is currently being created or updated. */
    data class EditLocation(val location: StorageLocation) : StorageEvent

    /** Saves a storage location to the database. */
    data class SaveLocation(
        val label: String,
        val type: StorageType,
        val capacity: Int
    ) : StorageEvent

    /** Permanently deletes a storage location. */
    data class DeleteLocation(val location: StorageLocation) : StorageEvent
}
