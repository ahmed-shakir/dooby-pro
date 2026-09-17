package se.supernovait.doobypro.presentation.order.details

import se.supernovait.doobypro.domain.model.order.Order

sealed interface OrderDetailsEvent {
    data object TransitionToNextStatus : OrderDetailsEvent
    data object LoadOrder : OrderDetailsEvent
    data object CancelOrder : OrderDetailsEvent
    data object DeleteOrder : OrderDetailsEvent
    data object ReissueOrder : OrderDetailsEvent
    data class SaveOrder(val updatedOrder: Order) : OrderDetailsEvent
    data class ToggleEdit(val editing: Boolean) : OrderDetailsEvent
}
