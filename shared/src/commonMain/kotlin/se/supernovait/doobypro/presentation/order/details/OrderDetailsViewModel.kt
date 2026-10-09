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
import se.supernovait.app.core.domain.observability.analytics.AnalyticsTracker
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.observability.performance.PerformanceMonitor
import se.supernovait.app.core.domain.observability.performance.traceAsync
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.util.LogTags
import se.supernovait.doobypro.presentation.navigation.Route

class OrderDetailsViewModel(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
    private val performanceMonitor: PerformanceMonitor,
    private val orderRepository: OrderRepository,
    private val orderManager: OrderManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val args = savedStateHandle.toRoute<Route.OrderDetails>()
    private val orderId = args.id

    private val _uiState = MutableStateFlow(OrderDetailsState())
    val uiState: StateFlow<OrderDetailsState> = _uiState.asStateFlow()
    
    private val _events = Channel<AppEvent>()
    val events = _events.receiveAsFlow()

    init {
        logger.info("OrderDetailsViewModel initialized for order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
        loadOrder()
    }

    fun onEvent(event: OrderDetailsEvent) {
        logger.debug("Handling event: $event", tag = LogTags.ORDER_DETAILS_VM)
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
            performanceMonitor.traceAsync("OrderDetailsViewModel.loadOrder") {
                logger.info("Loading order details for order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
                _uiState.update { it.copy(isLoading = true) }
                val result = orderRepository.getOrderById(orderId)
                if (result is Result.Success) {
                    _uiState.update { it.copy(order = result.data, isLoading = false, error = null) }
                } else {
                    logger.warn("Order details not found for order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
                    _uiState.update { it.copy(isLoading = false, error = Res.string.screen_Order_error_not_found) }
                }
            }
        }
    }

    private fun transitionStatus() {
        viewModelScope.launch {
            performanceMonitor.traceAsync("OrderDetailsViewModel.transitionStatus") {
                val order = _uiState.value.order ?: return@traceAsync
                logger.info("Transitioning order with ID: $orderId from ${order.status} to next status", tag = LogTags.ORDER_DETAILS_VM)
                analyticsTracker.trackEvent("order_status_transitioned", mapOf("order_id" to orderId))
                orderManager.transitionToNextStatus(order)
                loadOrder() // Refresh
            }
        }
    }

    private fun deliveryFailed() {
        viewModelScope.launch {
            performanceMonitor.traceAsync("OrderDetailsViewModel.deliveryFailed") {
                val order = _uiState.value.order ?: return@traceAsync
                val orderId = order.id ?: return@traceAsync
                logger.warn("Delivery failed for order with ID: $orderId, updating status and notifying", tag = LogTags.ORDER_DETAILS_VM)
                analyticsTracker.trackEvent("order_delivery_failed", mapOf("order_id" to orderId))
                orderManager.updateOrderStatus(orderId, OrderStatus.READY)
                orderManager.notifyOrderNotDelivered(orderId)
                loadOrder() // Refresh
            }
        }
    }

    private fun cancelOrder() {
        viewModelScope.launch {
            performanceMonitor.traceAsync("OrderDetailsViewModel.cancelOrder") {
                val order = _uiState.value.order ?: return@traceAsync
                logger.info("Cancelling order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
                analyticsTracker.trackEvent("order_cancelled", mapOf("order_id" to orderId))
                orderManager.cancelOrder(order)
                loadOrder() // Refresh
            }
        }
    }

    private fun deleteOrder() {
        viewModelScope.launch {
            performanceMonitor.traceAsync("OrderDetailsViewModel.deleteOrder") {
                val order = _uiState.value.order ?: return@traceAsync
                logger.info("Deleting order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
                val result = orderManager.deleteOrder(order)
                if (result is Result.Success) {
                    analyticsTracker.trackEvent("order_deleted_from_details", mapOf("order_id" to orderId))
                    _events.send(AppEvent.NavigateBack)
                } else {
                    logger.error("Failed to delete order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
                    crashReporter.log("Failed to delete order with ID: $orderId in OrderDetailsViewModel")
                }
            }
        }
    }

    private fun saveOrder(updatedOrder: Order) {
        viewModelScope.launch {
            performanceMonitor.traceAsync("OrderDetailsViewModel.saveOrder") {
                logger.info("Saving edited order with ID: $orderId", tag = LogTags.ORDER_DETAILS_VM)
                analyticsTracker.trackEvent("order_updated", mapOf("order_id" to orderId))
                orderRepository.saveOrder(updatedOrder)
                _uiState.update { it.copy(isEditing = false) }
                loadOrder()
            }
        }
    }
}
