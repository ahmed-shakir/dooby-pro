package se.supernovait.doobypro.presentation.order.details

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
import se.supernovait.app.core.data.observability.analytics.NoOpAnalyticsTracker
import se.supernovait.app.core.data.observability.crash.NoOpCrashReporter
import se.supernovait.app.core.data.observability.performance.NoOpPerformanceMonitor
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.event.AppEvent
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.notification.Notification
import se.supernovait.app.core.domain.notification.NotificationManager
import se.supernovait.app.core.domain.notification.NotificationRepository
import se.supernovait.app.core.domain.notification.PlatformNotificationHandler
import se.supernovait.app.core.domain.observability.logging.LogLevel
import se.supernovait.app.core.domain.observability.logging.Logger
import se.supernovait.app.core.domain.sharing.DeepLinkHandler
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.app.core.domain.sharing.SharedData
import se.supernovait.doobypro.data.repository.fake.FakeAccountRepository
import se.supernovait.doobypro.data.repository.fake.FakeAuthRepository
import se.supernovait.doobypro.data.repository.fake.FakeBusinessHoursRepository
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.manager.StorageLocationManager
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderServiceItem
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.util.PlatformTestConfig
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailsViewModelTest : PlatformTestConfig() {
    private lateinit var viewModel: OrderDetailsViewModel
    private lateinit var fakeOrderRepo: FakeOrderRepository
    private lateinit var fakeServiceRepo: FakeServiceRepository
    private lateinit var fakeStorageRepo: FakeStorageLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeNotificationRepo: FakeNotificationRepository
    private lateinit var orderManager: OrderManager
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testUser = User(id = "u1", username = "u1", firstname = "First", lastname = "Last", birthdate = LocalDate(1990, 1, 1), email = "u1@test.com")
    private val testService = Service(id = "s1", title = "Dry Cleaning", description = "", price = Amount(1500, "AED"))
    private val testStorage = StorageLocation(id = "l1", label = "Rack A", capacity = 10)
    private val testOrder = Order(
        id = "o1",
        customer = testUser,
        items = listOf(OrderServiceItem(testService, 2)),
        storageLocation = testStorage,
        status = OrderStatus.NEW,
        orderDatetime = LocalDateTime(2026, 1, 1, 0, 0),
        deliveryDatetime = LocalDateTime(2026, 1, 1, 0, 0),
        deliveryOption = DeliveryOption.EXPRESS,
        deliveryMethod = DeliveryMethod.HOME_DELIVERY,
        isPaymentDone = false,
        expressFee = 500L,
        deliveryFee = 1000L,
        notes = "Handle with care",
        createdAt = LocalDateTime(2026, 1, 1, 0, 0),
        updatedAt = LocalDateTime(2026, 1, 1, 0, 0),
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeOrderRepo = FakeOrderRepository()
        fakeOrderRepo.savedOrders["o1"] = testOrder

        fakeServiceRepo = FakeServiceRepository()
        fakeStorageRepo = FakeStorageLocationRepository()
        fakeStorageRepo.saveLocationSync(testStorage)

        fakeSettingsRepo = FakeSettingsRepository()
        fakeNotificationRepo = FakeNotificationRepository()

        val storageManager = StorageLocationManager(
            FakeLogger(), NoOpCrashReporter, NoOpPerformanceMonitor, fakeStorageRepo, fakeSettingsRepo
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
            crashReporter = NoOpCrashReporter,
            performanceMonitor = NoOpPerformanceMonitor
        )

        val savedStateHandle = SavedStateHandle(mapOf("id" to "o1"))
        viewModel = OrderDetailsViewModel(
            logger = FakeLogger(),
            crashReporter = NoOpCrashReporter,
            analyticsTracker = NoOpAnalyticsTracker,
            performanceMonitor = NoOpPerformanceMonitor,
            orderRepository = fakeOrderRepo,
            orderManager = orderManager,
            settingsRepository = fakeSettingsRepo,
            savedStateHandle = savedStateHandle,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads order details successfully`() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        val order = state.order
        assertNotNull(order)
        assertEquals("o1", order.id)
        assertEquals("First Last", "${order.customer.firstname} ${order.customer.lastname}")
        assertEquals("Rack A", order.storageLocation.label)
        assertEquals(1, order.items.size)
        assertEquals(3000L, order.items.first().subtotal.raw)
        assertEquals(4500L, order.totalPrice.raw)
    }

    @Test
    fun `loadOrder failure sets error state when order not found`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle(mapOf("id" to "non_existent"))
        val notFoundVm = OrderDetailsViewModel(
            logger = FakeLogger(),
            crashReporter = NoOpCrashReporter,
            analyticsTracker = NoOpAnalyticsTracker,
            performanceMonitor = NoOpPerformanceMonitor,
            orderRepository = fakeOrderRepo,
            orderManager = orderManager,
            settingsRepository = fakeSettingsRepo,
            savedStateHandle = savedState,
        )

        val state = notFoundVm.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.order)
        assertNotNull(state.error)
    }

    @Test
    fun `TransitionToNextStatus transitions status and reloads order`() = runTest(testDispatcher) {
        viewModel.onEvent(OrderDetailsEvent.TransitionToNextStatus)

        val updatedOrder = fakeOrderRepo.savedOrders["o1"]
        assertEquals(OrderStatus.IN_PROGRESS, updatedOrder?.status)
        assertEquals(OrderStatus.IN_PROGRESS, viewModel.uiState.value.order?.status)
    }

    @Test
    fun `CancelOrder cancels order and updates state`() = runTest(testDispatcher) {
        viewModel.onEvent(OrderDetailsEvent.CancelOrder)

        val updatedOrder = fakeOrderRepo.savedOrders["o1"]
        assertEquals(OrderStatus.CANCELLED, updatedOrder?.status)
        assertEquals(OrderStatus.CANCELLED, viewModel.uiState.value.order?.status)
    }

    @Test
    fun `DeleteOrder deletes order and emits NavigateBack event`() = runTest(testDispatcher) {
        val eventsList = mutableListOf<AppEvent>()
        val job = launch {
            viewModel.events.collect { eventsList.add(it) }
        }

        viewModel.onEvent(OrderDetailsEvent.DeleteOrder)

        assertNull(fakeOrderRepo.savedOrders["o1"])
        assertEquals(1, eventsList.size)
        assertEquals(AppEvent.NavigateBack, eventsList.first())

        job.cancel()
    }

    @Test
    fun `SaveOrder saves updated order and exits editing state`() = runTest(testDispatcher) {
        viewModel.onEvent(OrderDetailsEvent.ToggleEdit(editing = true))
        assertTrue(viewModel.uiState.value.isEditing)

        val editedOrder = testOrder.copy(notes = "New Notes")
        viewModel.onEvent(OrderDetailsEvent.SaveOrder(editedOrder))

        assertFalse(viewModel.uiState.value.isEditing)
        assertEquals("New Notes", fakeOrderRepo.savedOrders["o1"]?.notes)
        assertEquals("New Notes", viewModel.uiState.value.order?.notes)
    }

    @Test
    fun `ToggleEdit updates isEditing in state`() = runTest(testDispatcher) {
        assertFalse(viewModel.uiState.value.isEditing)
        viewModel.onEvent(OrderDetailsEvent.ToggleEdit(editing = true))
        assertTrue(viewModel.uiState.value.isEditing)
        viewModel.onEvent(OrderDetailsEvent.ToggleEdit(editing = false))
        assertFalse(viewModel.uiState.value.isEditing)
    }

    private class FakeOrderRepository : OrderRepository {
        val savedOrders = mutableMapOf<String, Order>()

        override fun getOrders(): Flow<List<Order>> = MutableStateFlow(savedOrders.values.toList())
        override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> = MutableStateFlow(emptyList())

        override suspend fun getOrderById(id: String): Result<Order, DataError> {
            return savedOrders[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }

        override suspend fun saveOrder(order: Order): Result<String, DataError> {
            val id = order.id ?: "gen_id"
            val newOrder = order.copy(id = id)
            savedOrders[id] = newOrder
            return Result.Success(id)
        }

        override suspend fun deleteOrder(order: Order): Result<Unit, DataError> {
            order.id?.let { savedOrders.remove(it) }
            return Result.Success(Unit)
        }

        override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
            savedOrders[orderId]?.let {
                savedOrders[orderId] = it.copy(status = newStatus)
            }
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

        fun saveLocationSync(location: StorageLocation) {
            val id = location.id ?: "gen_id"
            locations[id] = location.copy(id = id)
        }

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
