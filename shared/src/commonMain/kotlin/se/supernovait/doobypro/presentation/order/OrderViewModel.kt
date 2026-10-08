package se.supernovait.doobypro.presentation.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_error_customer_save_failed
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
import se.supernovait.app.core.domain.observability.analytics.AnalyticsTracker
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.manager.OrderQueryManager
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.order.OrderTab
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.CustomerRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.domain.util.LogTags

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModel(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
    private val savedStateHandle: SavedStateHandle,
    private val serviceRepository: ServiceRepository,
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository,
    private val customerRepository: CustomerRepository,
    private val orderManager: OrderManager,
    private val orderQueryManager: OrderQueryManager
) : ViewModel() {
    private val _activeTab = MutableStateFlow(OrderTab.NEW)
    private val _isArchive = MutableStateFlow(false)
    private val _orderSearchQuery = MutableStateFlow("")
    private val _customerSearchQuery = MutableStateFlow("")
    private val _isSaving = MutableStateFlow(false)
    private val _editingOrder = MutableStateFlow<Order?>(null)
    private val _isAddingCustomer = MutableStateFlow(false)
    private val _error = MutableStateFlow<StringResource?>(null)

    init {
        logger.info("OrderViewModel initialized", tag = LogTags.ORDER_VM)
        // Observe reissue requests from navigation results
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Order?>("reissue_order", null).collect { order ->
                if (order != null) {
                    logger.info("Received reissue request from navigation result for order with ID: ${order.id}", tag = LogTags.ORDER_VM)
                    reissueOrder(order)
                    savedStateHandle["reissue_order"] = null // Clear result
                }
            }
        }
    }

    val uiState: StateFlow<OrderState> = combine(
        combine(_activeTab, _isArchive) { tab, archive ->
            if (archive) orderQueryManager.getCancelledOrders() else orderQueryManager.getOrdersForTab(tab)
        }.flatMapLatest { it },
        _activeTab,
        _isArchive,
        _orderSearchQuery,
        _customerSearchQuery,
        orderQueryManager.getLateOrderCountPerTab(),
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
        val archive = args[2] as Boolean
        val orderQuery = args[3] as String
        val customerQuery = args[4] as String
        val lateCounts = args[5] as Map<OrderTab, Int>
        val settings = args[6] as Settings
        val services = args[7] as List<Service>
        val locations = args[8] as List<StorageLocation>
        val allCustomers = args[9] as List<User>
        val editing = args[10] as Order?
        val addingCustomer = args[11] as Boolean
        val saving = args[12] as Boolean
        val error = args[13] as StringResource?

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
            isArchive = archive,
            searchQuery = orderQuery,
            customerSearchQuery = customerQuery,
            lateOrderCountPerTab = lateCounts,
            services = services,
            storageLocations = locations,
            isManualStorageMode = settings.storage.storageAllocationMode == StorageAllocationMode.MANUAL,
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
        logger.debug("Handling event: $event", tag = LogTags.ORDER_VM)
        when (event) {
            OrderEvent.LoadOrders -> { /* Handled by Flow */ }
            OrderEvent.CreateNewOrder -> startNewOrder()
            is OrderEvent.ReissueOrder -> reissueOrder(event.order)
            is OrderEvent.EditOrder -> _editingOrder.value = event.order
            is OrderEvent.SaveOrder -> saveOrder(event.order)
            is OrderEvent.DeleteOrder -> deleteOrder(event.order)
            is OrderEvent.UpdateStatus -> updateStatus(event.orderId, event.newStatus)
            is OrderEvent.SelectTab -> {
                analyticsTracker.trackEvent("order_tab_selected", mapOf("tab" to event.tab.name))
                _activeTab.value = event.tab
            }
            is OrderEvent.SearchOrders -> {
                if (event.query.isNotBlank()) {
                    analyticsTracker.trackEvent("order_searched", mapOf("query" to event.query))
                }
                _orderSearchQuery.value = event.query
            }
            is OrderEvent.SearchCustomers -> _customerSearchQuery.value = event.query
            is OrderEvent.SelectCustomer -> selectCustomer(event.customer)
            OrderEvent.StartAddingCustomer -> _isAddingCustomer.value = true
            is OrderEvent.SaveNewCustomer -> saveNewCustomer(event.customer)
            is OrderEvent.ViewOrderDetails -> { /* Handled by navigation */ }
            OrderEvent.ViewCancelledOrders -> { /* Handled by navigation */ }
            is OrderEvent.ToggleArchive -> {
                analyticsTracker.trackEvent("order_archive_toggled", mapOf("is_archive" to event.isArchive))
                _isArchive.value = event.isArchive
            }
        }
    }

    private fun startNewOrder() {
        logger.debug("Starting new order flow", tag = LogTags.ORDER_VM)
        analyticsTracker.trackEvent("order_creation_started")
        _editingOrder.value = null
        _customerSearchQuery.value = ""
        _isAddingCustomer.value = false
    }

    private fun reissueOrder(order: Order) {
        viewModelScope.launch {
            logger.info("Reissuing order template from order with ID: ${order.id}", tag = LogTags.ORDER_VM)
            analyticsTracker.trackEvent("order_reissued", mapOf("order_id" to (order.id ?: "")))
            val template = orderManager.reissueOrder(order)
            _editingOrder.value = template
        }
    }

    private fun selectCustomer(customer: User) {
        viewModelScope.launch {
            logger.info("Selected customer '${customer.username}' for order creation", tag = LogTags.ORDER_VM)
            val template = orderManager.createOrderTemplate(customer = customer)
            _editingOrder.value = template
        }
    }

    private fun saveNewCustomer(customer: User) {
        viewModelScope.launch {
            logger.info("Saving new customer: ${customer.username}", tag = LogTags.ORDER_VM)
            _isSaving.value = true
            val result = customerRepository.saveCustomer(customer)
            if (result is Result.Success) {
                logger.info("Customer '${customer.username}' saved successfully with ID: ${result.data}", tag = LogTags.ORDER_VM)
                analyticsTracker.trackEvent("customer_created", mapOf("username" to customer.username))
                val newUser = customer.copy(id = result.data)
                _isAddingCustomer.value = false
                selectCustomer(newUser)
            } else {
                logger.error("Failed to save customer '${customer.username}'", tag = LogTags.ORDER_VM)
                crashReporter.log("Failed to save customer '${customer.username}' in OrderViewModel")
                _error.value = Res.string.screen_Order_error_customer_save_failed
            }
            _isSaving.value = false
        }
    }

    private fun saveOrder(order: Order) {
        viewModelScope.launch {
            logger.info("Saving order for customer '${order.customer.username}'", tag = LogTags.ORDER_VM)
            _isSaving.value = true
            val result = orderManager.createOrder(order)
            if (result is Result.Success) {
                logger.info("Order saved successfully with ID: ${result.data}", tag = LogTags.ORDER_VM)
                analyticsTracker.trackEvent("order_saved", mapOf("customer" to order.customer.username, "status" to order.status.name))
                _editingOrder.value = null
            } else {
                logger.error("Failed to save order", tag = LogTags.ORDER_VM)
                crashReporter.log("Failed to save order for customer '${order.customer.username}' in OrderViewModel")
                _error.value = Res.string.screen_Order_error_save_failed
            }
            _isSaving.value = false
        }
    }

    private fun deleteOrder(order: Order) {
        viewModelScope.launch {
            logger.info("Deleting order with ID: ${order.id}", tag = LogTags.ORDER_VM)
            _isSaving.value = true
            val result = orderManager.deleteOrder(order)
            if (result is Result.Success) {
                analyticsTracker.trackEvent("order_deleted", mapOf("order_id" to (order.id ?: "")))
            } else {
                logger.error("Failed to delete order with ID: ${order.id}", tag = LogTags.ORDER_VM)
                crashReporter.log("Failed to delete order with ID: ${order.id} in OrderViewModel")
                _error.value = Res.string.screen_Order_error_delete_failed
            }
            _isSaving.value = false
        }
    }

    private fun updateStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            logger.info("Updating status for order with ID: $orderId to $newStatus", tag = LogTags.ORDER_VM)
            analyticsTracker.trackEvent("order_status_updated", mapOf("order_id" to orderId, "new_status" to newStatus.name))
            orderManager.updateOrderStatus(orderId, newStatus)
        }
    }
}
