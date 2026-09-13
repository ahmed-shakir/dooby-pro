package se.supernovait.doobypro.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_error_delete_failed
import doobypro.shared.generated.resources.screen_Order_error_save_failed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.manager.OrderQueryManager
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.CustomerRepository
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val serviceRepository: ServiceRepository,
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository,
    private val customerRepository: CustomerRepository,
    private val orderManager: OrderManager,
    private val orderQueryManager: OrderQueryManager
) : ViewModel() {
    private val _activeTab = MutableStateFlow(OrderTab.NEW)
    private val _orderSearchQuery = MutableStateFlow("")
    private val _customerSearchQuery = MutableStateFlow("")
    private val _isSaving = MutableStateFlow(false)
    private val _editingOrder = MutableStateFlow<Order?>(null)
    private val _isAddingCustomer = MutableStateFlow(false)
    private val _error = MutableStateFlow<StringResource?>(null)

    val uiState: StateFlow<OrderState> = combine(
        _activeTab.flatMapLatest { orderQueryManager.getOrdersForTab(it) },
        _activeTab,
        _orderSearchQuery,
        _customerSearchQuery,
        orderQueryManager.getOrderCountPerTab(),
        settingsRepository.settings,
        serviceRepository.getServices(),
        storageLocationRepository.getActiveLocations(),
        customerRepository.getCustomers(),
        _editingOrder,
        _isAddingCustomer,
        _isSaving,
        _error
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val orders = args[0] as List<Order>
        val tab = args[1] as OrderTab
        val orderQuery = args[2] as String
        val customerQuery = args[3] as String
        val counts = args[4] as Map<OrderTab, Int>
        val settings = args[5] as Settings
        val services = args[6] as List<Service>
        val locations = args[7] as List<StorageLocation>
        val allCustomers = args[8] as List<User>
        val editing = args[9] as Order?
        val addingCustomer = args[10] as Boolean
        val saving = args[11] as Boolean
        val error = args[12] as StringResource?

        val filteredOrders = if (orderQuery.isBlank()) {
            orders
        } else {
            orders.filter {
                it.id?.contains(orderQuery, ignoreCase = true) == true ||
                it.notes?.contains(orderQuery, ignoreCase = true) == true ||
                it.customer.firstname.contains(orderQuery, ignoreCase = true) ||
                it.customer.lastname.contains(orderQuery, ignoreCase = true)
            }
        }

        val filteredCustomers = if (customerQuery.isBlank()) {
            emptyList()
        } else {
            allCustomers.filter {
                it.firstname.contains(customerQuery, ignoreCase = true) ||
                it.lastname.contains(customerQuery, ignoreCase = true) ||
                it.username.contains(customerQuery, ignoreCase = true) ||
                it.email.contains(customerQuery, ignoreCase = true) ||
                it.id?.contains(customerQuery, ignoreCase = true) == true ||
                it.phoneNumber?.contains(customerQuery, ignoreCase = true) == true
            }
        }

        OrderState(
            orders = filteredOrders,
            activeTab = tab,
            searchQuery = orderQuery,
            customerSearchQuery = customerQuery,
            orderCountPerTab = counts,
            settings = settings.order,
            services = services,
            storageLocations = locations,
            customers = filteredCustomers,
            editingOrder = editing,
            error = error,
            isAddingCustomer = addingCustomer,
            isSaving = saving,
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
            OrderEvent.CreateNewOrder -> startNewOrder()
            is OrderEvent.EditOrder -> _editingOrder.value = event.order
            is OrderEvent.SaveOrder -> saveOrder(event.order)
            is OrderEvent.DeleteOrder -> deleteOrder(event.order)
            is OrderEvent.UpdateStatus -> updateStatus(event.orderId, event.newStatus)
            is OrderEvent.SelectTab -> _activeTab.value = event.tab
            is OrderEvent.SearchOrders -> _orderSearchQuery.value = event.query
            is OrderEvent.SearchCustomers -> _customerSearchQuery.value = event.query
            is OrderEvent.SelectCustomer -> selectCustomer(event.customer)
            OrderEvent.StartAddingCustomer -> _isAddingCustomer.value = true
            is OrderEvent.SaveNewCustomer -> saveNewCustomer(event.customer)
        }
    }

    private fun startNewOrder() {
        _editingOrder.value = null
        _customerSearchQuery.value = ""
        _isAddingCustomer.value = false
    }

    private fun selectCustomer(customer: User) {
        viewModelScope.launch {
            val template = orderManager.createOrderTemplate(customer = customer)
            _editingOrder.value = template
        }
    }

    private fun saveNewCustomer(customer: User) {
        viewModelScope.launch {
            _isSaving.value = true
            val result = customerRepository.saveCustomer(customer)
            if (result is Result.Success) {
                val newUser = customer.copy(id = result.data)
                _isAddingCustomer.value = false
                selectCustomer(newUser)
            } else {
                // TODO: Handle customer save error
            }
            _isSaving.value = false
        }
    }

    private fun saveOrder(order: Order) {
        viewModelScope.launch {
            _isSaving.value = true
            val result = orderManager.createOrder(order)
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
            val result = orderManager.deleteOrder(order)
            if (result !is Result.Success) {
                _error.value = Res.string.screen_Order_error_delete_failed
            }
            _isSaving.value = false
        }
    }

    private fun updateStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            orderManager.updateOrderStatus(orderId, newStatus)
        }
    }
}
