package se.supernovait.doobypro.presentation.order.details

import org.jetbrains.compose.resources.StringResource
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeType

data class OrderDetailsState(
    val order: Order? = null,
    val expressFeeType: ExpressFeeType = ExpressFeeType.PERCENTAGE,
    val expressFeeValue: Long = 15,
    val error: StringResource? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false
)
