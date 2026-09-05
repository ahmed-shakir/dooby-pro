package se.supernovait.doobypro.presentation.storage

import org.jetbrains.compose.resources.StringResource
import se.supernovait.doobypro.domain.model.storage.StorageLocation

data class StorageState(
    val locations: List<StorageLocation> = emptyList(),
    val editingLocation: StorageLocation? = null,
    val error: StringResource? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)
