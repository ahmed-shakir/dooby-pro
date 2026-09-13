package se.supernovait.doobypro.presentation.order.details

import org.jetbrains.compose.resources.StringResource
import se.supernovait.doobypro.domain.model.order.Order

data class OrderDetailsState(
    val order: Order? = null,
    val error: StringResource? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false
)
