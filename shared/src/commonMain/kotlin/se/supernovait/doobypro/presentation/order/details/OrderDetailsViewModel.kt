package se.supernovait.doobypro.presentation.order.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_error_not_found
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.presentation.navigation.Route

class OrderDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val orderRepository: OrderRepository,
    private val orderManager: OrderManager
) : ViewModel() {
    private val args = savedStateHandle.toRoute<Route.OrderDetails>()
    private val orderId = args.id

    private val _uiState = MutableStateFlow(OrderDetailsState())
    val uiState: StateFlow<OrderDetailsState> = _uiState.asStateFlow()
    
    private val _events = Channel<AppEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadOrder()
    }

    fun onEvent(event: OrderDetailsEvent) {
        when (event) {
            OrderDetailsEvent.LoadOrder -> loadOrder()
            OrderDetailsEvent.TransitionToNextStatus -> transitionStatus()
            OrderDetailsEvent.DeliveryFailed -> deliveryFailed()
            OrderDetailsEvent.CancelOrder -> cancelOrder()
            OrderDetailsEvent.DeleteOrder -> deleteOrder()
            OrderDetailsEvent.ReissueOrder -> { /* Handled by navigation */ }
            is OrderDetailsEvent.SaveOrder -> saveOrder(event.updatedOrder)
            is OrderDetailsEvent.ToggleEdit -> _uiState.update { it.copy(isEditing = event.editing) }
        }
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = orderRepository.getOrderById(orderId)
            if (result is Result.Success) {
                _uiState.update { it.copy(order = result.data, isLoading = false, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = Res.string.screen_Order_error_not_found) }
            }
        }
    }

    private fun transitionStatus() {
        viewModelScope.launch {
            val order = _uiState.value.order ?: return@launch
            orderManager.transitionToNextStatus(order)
            loadOrder() // Refresh
        }
    }

    private fun deliveryFailed() {
        viewModelScope.launch {
            val order = _uiState.value.order ?: return@launch
            val orderId = order.id ?: return@launch
            orderManager.updateOrderStatus(orderId, OrderStatus.READY)
            orderManager.notifyOrderNotDelivered(orderId)
            loadOrder() // Refresh
        }
    }

    private fun cancelOrder() {
        viewModelScope.launch {
            val order = _uiState.value.order ?: return@launch
            orderManager.cancelOrder(order)
            loadOrder() // Refresh
        }
    }

    private fun deleteOrder() {
        viewModelScope.launch {
            val order = _uiState.value.order ?: return@launch
            val result = orderManager.deleteOrder(order)
            if (result is Result.Success) {
                _events.send(AppEvent.NavigateBack)
            }
        }
    }

    private fun saveOrder(updatedOrder: Order) {
        viewModelScope.launch {
            orderRepository.saveOrder(updatedOrder)
            _uiState.update { it.copy(isEditing = false) }
            loadOrder()
        }
    }
}
