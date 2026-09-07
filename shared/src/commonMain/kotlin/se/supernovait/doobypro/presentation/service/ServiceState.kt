package se.supernovait.doobypro.presentation.service

import org.jetbrains.compose.resources.StringResource
import se.supernovait.doobypro.domain.model.Service

data class ServiceState(
    val services: List<Service> = emptyList(),
    val currency: String = "AED",
    val editingService: Service? = null,
    val error: StringResource? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false
)
