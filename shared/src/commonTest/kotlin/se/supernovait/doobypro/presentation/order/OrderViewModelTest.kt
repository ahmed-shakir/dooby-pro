package se.supernovait.doobypro.presentation.order

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.data.crash.NoOpCrashReporter
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.observability.logging.LogLevel
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.notification.Notification
import se.supernovait.app.core.domain.notification.NotificationManager
import se.supernovait.app.core.domain.notification.NotificationRepository
import se.supernovait.app.core.domain.notification.PlatformNotificationHandler
import se.supernovait.app.core.domain.sharing.DeepLinkHandler
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.app.core.domain.sharing.SharedData
import se.supernovait.doobypro.data.local.dao.FakeUserDao
import se.supernovait.doobypro.data.repository.fake.FakeAccountRepository
import se.supernovait.doobypro.data.repository.fake.FakeAuthRepository
import se.supernovait.doobypro.data.repository.fake.FakeBusinessHoursRepository
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.manager.OrderQueryManager
import se.supernovait.doobypro.domain.manager.StorageLocationManager
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.CustomerRepository
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.util.PlatformTestConfig
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest : PlatformTestConfig() {
    private lateinit var viewModel: OrderViewModel
    private lateinit var fakeOrderRepo: FakeOrderRepository
    private lateinit var fakeServiceRepo: FakeServiceRepository
    private lateinit var fakeStorageRepo: FakeStorageLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeCustomerRepo: FakeCustomerRepository
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var fakeNotificationRepo: FakeNotificationRepository
    private lateinit var orderManager: OrderManager
    private lateinit var orderQueryManager: OrderQueryManager
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testUser = User(id = "u1", username = "u1", firstname = "F", lastname = "L", birthdate = LocalDate(1990, 1, 1), email = "")
    private val testService = Service(id = "s1", title = "S1", description = "", price = Amount(0, "AED"))
    private val testStorage = StorageLocation(id = "l1", label = "L1", capacity = 10)
    private val testOrder = Order(
        id = "o1",
        customer = testUser,
        service = testService,
        storageLocation = testStorage,
        status = OrderStatus.NEW,
        orderDatetime = LocalDateTime(2026, 1, 1, 0, 0),
        deliveryDatetime = LocalDateTime(2026, 1, 1, 0, 0),
        deliveryOption = DeliveryOption.STANDARD,
        deliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
        isPaymentDone = false,
        notes = null,
        createdAt = LocalDateTime(2026, 1, 1, 0, 0),
        updatedAt = LocalDateTime(2026, 1, 1, 0, 0)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeOrderRepo = FakeOrderRepository()
        fakeServiceRepo = FakeServiceRepository()
        fakeStorageRepo = FakeStorageLocationRepository()
        fakeSettingsRepo = FakeSettingsRepository()
        fakeCustomerRepo = FakeCustomerRepository()
        fakeUserDao = FakeUserDao()
        fakeNotificationRepo = FakeNotificationRepository()
        
        val storageManager = StorageLocationManager(
            FakeLogger(), NoOpCrashReporter, fakeStorageRepo, fakeSettingsRepo
        )

        val notificationManager = NotificationManager(
            logger = FakeLogger(),
            repository = fakeNotificationRepo,
            platformHandler = FakePlatformNotificationHandler(),
            deepLinkHandler = FakeDeepLinkHandler(),
            managerScope = CoroutineScope(testDispatcher)
        )
        
        val fakeAuth = FakeAuthRepository()
        val authManager = AuthenticationManager(
            logger = FakeLogger(),
            authRepository = fakeAuth,
            crashReporter = NoOpCrashReporter,
            managerScope = CoroutineScope(testDispatcher)
        )

        orderManager = OrderManager(
            orderRepository = fakeOrderRepo,
            serviceRepository = fakeServiceRepo,
            storageLocationManager = storageManager,
            storageLocationRepository = fakeStorageRepo,
            settingsRepository = fakeSettingsRepo,
            notificationManager = notificationManager,
            shareConfiguration = ShareConfiguration.custom("doobypro"),
            authenticationManager = authManager,
            authRepository = fakeAuth,
            accountRepository = FakeAccountRepository(),
            businessHoursRepository = FakeBusinessHoursRepository(),
            logger = FakeLogger(),
            crashReporter = se.supernovait.app.core.data.crash.NoOpCrashReporter
        )
        orderQueryManager = OrderQueryManager(fakeOrderRepo, FakeLogger())
        
        viewModel = OrderViewModel(
            savedStateHandle = SavedStateHandle(),
            serviceRepository = fakeServiceRepo,
            storageLocationRepository = fakeStorageRepo,
            settingsRepository = fakeSettingsRepo,
            customerRepository = fakeCustomerRepo,
            orderManager = orderManager,
            orderQueryManager = orderQueryManager,
            logger = FakeLogger(),
            crashReporter = se.supernovait.app.core.data.crash.NoOpCrashReporter
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads orders from repository`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        fakeOrderRepo.emit(listOf(testOrder))

        val state = viewModel.uiState.value
        assertEquals(1, state.orders.size)
        assertEquals("o1", state.orders[0].id)
        
        collectJob.cancel()
    }

    @Test
    fun `SaveOrder should call repository and clear editing state`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        // Seed storage for OrderManager to succeed
        fakeStorageRepo.saveLocation(testStorage)
        
        viewModel.onEvent(OrderEvent.EditOrder(testOrder))
        viewModel.onEvent(OrderEvent.SaveOrder(testOrder))

        assertNotNull(fakeOrderRepo.savedOrder)
        assertEquals("o1", fakeOrderRepo.savedOrder?.id)
        assertEquals(null, viewModel.uiState.value.editingOrder)
        
        collectJob.cancel()
    }

    @Test
    fun `UpdateStatus should call repository`() = runTest(testDispatcher) {
        viewModel.onEvent(OrderEvent.UpdateStatus("o1", OrderStatus.READY))
        assertEquals(OrderStatus.READY, fakeOrderRepo.updatedStatus)
    }

    @Test
    fun `ReissueOrder should update editing state`() = runTest(testDispatcher) {
        val collectJob = launch { viewModel.uiState.collect {} }
        
        viewModel.onEvent(OrderEvent.ReissueOrder(testOrder))
        
        assertNotNull(viewModel.uiState.value.editingOrder)
        assertEquals(testOrder.customer.id, viewModel.uiState.value.editingOrder?.customer?.id)
        assertEquals(testOrder.service.id, viewModel.uiState.value.editingOrder?.service?.id)
        
        collectJob.cancel()
    }

    @Test
    fun `SavedStateHandle reissue_order should trigger editing state`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("reissue_order" to testOrder))
        val vm = OrderViewModel(
            savedStateHandle = savedStateHandle,
            serviceRepository = fakeServiceRepo,
            storageLocationRepository = fakeStorageRepo,
            settingsRepository = fakeSettingsRepo,
            customerRepository = fakeCustomerRepo,
            orderManager = orderManager,
            orderQueryManager = orderQueryManager,
            logger = FakeLogger(),
            crashReporter = NoOpCrashReporter
        )
        
        val collectJob = launch { vm.uiState.collect {} }
        
        assertNotNull(vm.uiState.value.editingOrder)
        assertEquals(testOrder.customer.id, vm.uiState.value.editingOrder?.customer?.id)
        
        collectJob.cancel()
    }

    private class FakeOrderRepository : OrderRepository {
        private val _orders = MutableStateFlow<List<Order>>(emptyList())
        var savedOrder: Order? = null
        var updatedStatus: OrderStatus? = null

        fun emit(orders: List<Order>) { _orders.value = orders }
        override fun getOrders(): Flow<List<Order>> = _orders
        override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> = MutableStateFlow(emptyList())
        override suspend fun getOrderById(id: String): Result<Order, DataError> = Result.Failure(DataError.NOT_FOUND)
        
        override suspend fun saveOrder(order: Order): Result<String, DataError> {
            savedOrder = order
            return Result.Success(order.id ?: "gen")
        }

        override suspend fun deleteOrder(order: Order): Result<Unit, DataError> = Result.Success(Unit)

        override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
            updatedStatus = newStatus
            return Result.Success(Unit)
        }
    }

    private class FakeServiceRepository : ServiceRepository {
        override fun getServices(): Flow<List<Service>> = flowOf(emptyList())
        override suspend fun getServiceById(id: String): Result<Service, DataError> = Result.Failure(DataError.NOT_FOUND)
        override suspend fun saveService(service: Service): Result<String, DataError> = Result.Success("")
        override suspend fun deleteService(service: Service): Result<Unit, DataError> = Result.Success(Unit)
    }

    private class FakeStorageLocationRepository : StorageLocationRepository {
        private val locations = mutableMapOf<String, StorageLocation>()

        override fun getActiveLocations(): Flow<List<StorageLocation>> = flowOf(locations.values.toList())
        override suspend fun getLocationById(id: String): Result<StorageLocation, DataError> {
            return locations[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }
        override suspend fun getDefaultLocation(): Result<StorageLocation, DataError> {
            return locations.values.firstOrNull { it.isDefault }?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }
        override suspend fun saveLocation(location: StorageLocation): Result<String, DataError> {
            val id = location.id ?: "gen_id"
            locations[id] = location.copy(id = id)
            return Result.Success(id)
        }
        override suspend fun deleteLocation(location: StorageLocation): Result<Unit, DataError> = Result.Success(Unit)
        override suspend fun incrementOccupiedSlots(id: String) {}
        override suspend fun decrementOccupiedSlots(id: String) {}
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val _settings = MutableStateFlow(Settings())
        override val settings: Flow<Settings> = _settings.asStateFlow()
        override suspend fun updateSettings(settings: Settings) { _settings.value = settings }
        override suspend fun resetSettings() { _settings.value = Settings() }
    }

    private class FakeCustomerRepository : CustomerRepository {
        override fun getCustomers(): Flow<List<User>> = flowOf(emptyList())
        override suspend fun getCustomerById(id: String): Result<User, DataError> = Result.Failure(DataError.NOT_FOUND)
        override suspend fun saveCustomer(customer: User): Result<String, DataError> = Result.Success("")
        override suspend fun deleteCustomer(customer: User): Result<Unit, DataError> = Result.Success(Unit)
    }

    private class FakeNotificationRepository : NotificationRepository {
        val savedNotifications = mutableListOf<Notification>()

        override fun getNotifications(): Flow<List<Notification>> = flowOf(savedNotifications)
        override fun getUnreadCount(): Flow<Int> = flowOf(savedNotifications.count { !it.isRead })
        override suspend fun markAsRead(id: String): Result<Unit, DataError> = Result.Success(Unit)
        override suspend fun markAllAsRead(): Result<Unit, DataError> = Result.Success(Unit)
        override suspend fun save(notification: Notification): Result<String, DataError> {
            savedNotifications.add(notification)
            return Result.Success(notification.id)
        }
        override suspend fun delete(notification: Notification): Result<Unit, DataError> = Result.Success(Unit)
        override suspend fun deleteAll(): Result<Unit, DataError> = Result.Success(Unit)
    }

    private class FakePlatformNotificationHandler : PlatformNotificationHandler {
        override fun showNotification(notification: Notification) {}
    }

    private class FakeDeepLinkHandler : DeepLinkHandler {
        override val events: SharedFlow<SharedData> = MutableSharedFlow()
        override fun handleDeepLink(url: String) {}
    }

    private class FakeLogger : Logger {
        override fun trace(message: String, throwable: Throwable?, tag: String?) {}
        override fun debug(message: String, throwable: Throwable?, tag: String?) {}
        override fun info(message: String, throwable: Throwable?, tag: String?) {}
        override fun warn(message: String, throwable: Throwable?, tag: String?) {}
        override fun error(message: String, throwable: Throwable?, tag: String?) {}
        override fun log(level: LogLevel, message: String, throwable: Throwable?, tag: String?) {}
    }
}
