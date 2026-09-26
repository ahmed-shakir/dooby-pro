package se.supernovait.doobypro.presentation.businesshours

import org.jetbrains.compose.resources.StringResource
import se.supernovait.doobypro.domain.model.company.BusinessHours

data class BusinessHoursEditorState(
    val businessHours: BusinessHours? = null,
    val error: StringResource? = null,
    val successMessage: StringResource? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)
