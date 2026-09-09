package se.supernovait.doobypro.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_error_delete_failed
import doobypro.shared.generated.resources.screen_Order_error_save_failed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val serviceRepository: ServiceRepository,
    private val storageLocationRepository: StorageLocationRepository,
    private val userDao: UserDao
) : ViewModel() {
    private val _isSaving = MutableStateFlow(false)
    private val _editingOrder = MutableStateFlow<Order?>(null)
    private val _error = MutableStateFlow<StringResource?>(null)

    val uiState: StateFlow<OrderState> = combine(
        orderRepository.getOrders(),
        serviceRepository.getServices(),
        storageLocationRepository.getActiveLocations(),
        userDao.getAll().map { entities -> entities.map { it.toDomain() } },
        _editingOrder,
        _isSaving,
        _error
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        OrderState(
            orders = args[0] as List<Order>,
            services = args[1] as List<Service>,
            storageLocations = args[2] as List<StorageLocation>,
            customers = args[3] as List<User>,
            editingOrder = args[4] as Order?,
            isSaving = args[5] as Boolean,
            error = args[6] as StringResource?,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = OrderState(isLoading = true)
    )

    fun onEvent(event: OrderEvent) {
        when (event) {
            OrderEvent.LoadOrders -> { /* Handled by Flow */ }
            is OrderEvent.EditOrder -> _editingOrder.value = event.order
            is OrderEvent.SaveOrder -> saveOrder(event.order)
            is OrderEvent.DeleteOrder -> deleteOrder(event.order)
            is OrderEvent.UpdateStatus -> updateStatus(event.orderId, event.newStatus)
        }
    }

    private fun saveOrder(order: Order) {
        viewModelScope.launch {
            _isSaving.value = true
            val result = orderRepository.saveOrder(order)
            if (result is Result.Success) {
                _editingOrder.value = null
            } else {
                _error.value = Res.string.screen_Order_error_save_failed
            }
            _isSaving.value = false
        }
    }

    private fun deleteOrder(order: Order) {
        viewModelScope.launch {
            _isSaving.value = true
            val result = orderRepository.deleteOrder(order)
            if (result !is Result.Success) {
                _error.value = Res.string.screen_Order_error_delete_failed
            }
            _isSaving.value = false
        }
    }

    private fun updateStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, newStatus)
        }
    }
}
