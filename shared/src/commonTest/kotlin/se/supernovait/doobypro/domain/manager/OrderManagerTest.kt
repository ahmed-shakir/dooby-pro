package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.notification.Notification
import se.supernovait.app.core.domain.notification.NotificationManager
import se.supernovait.app.core.domain.notification.NotificationRepository
import se.supernovait.app.core.domain.notification.PlatformNotificationHandler
import se.supernovait.app.core.domain.sharing.DeepLinkHandler
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.app.core.domain.sharing.SharedData
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.notification.NotificationSettings
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

@OptIn(ExperimentalCoroutinesApi::class)
class OrderManagerTest : PlatformTestConfig() {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeOrderRepo: FakeOrderRepository
    private lateinit var fakeServiceRepo: FakeServiceRepository
    private lateinit var fakeStorageRepo: FakeStorageLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeNotificationRepo: FakeNotificationRepository

    private val testUser = User(
        id = "user_123",
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    private val testService = Service(
        id = "service_123",
        title = "Test Service",
        description = "Description",
        price = Amount(1000, "AED")
    )

    private val testStorage = StorageLocation(
        id = "default",
        label = "Uncategorized",
        isDefault = true
    )

    private val pastDateTime = LocalDateTime(2000, 1, 1, 0, 0, 0)
    private val futureDateTime = LocalDateTime(2099, 1, 1, 0, 0, 0)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeOrderRepo = FakeOrderRepository()
        fakeServiceRepo = FakeServiceRepository()
        fakeStorageRepo = FakeStorageLocationRepository()
        fakeSettingsRepo = FakeSettingsRepository()
        fakeNotificationRepo = FakeNotificationRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createOrderManager(scope: CoroutineScope): OrderManager {
        val storageManager = StorageLocationManager(fakeStorageRepo, fakeSettingsRepo)
        val notificationManager = NotificationManager(
            repository = fakeNotificationRepo,
            platformHandler = FakePlatformNotificationHandler(),
            deepLinkHandler = FakeDeepLinkHandler(),
            managerScope = scope
        )

        return OrderManager(
            orderRepository = fakeOrderRepo,
            serviceRepository = fakeServiceRepo,
            storageLocationManager = storageManager,
            storageLocationRepository = fakeStorageRepo,
            settingsRepository = fakeSettingsRepo,
            notificationManager = notificationManager,
            shareConfiguration = ShareConfiguration.custom("doobypro")
        )
    }

    private fun createOrder(
        id: String? = "order_1",
        status: OrderStatus = OrderStatus.NEW,
        deliveryMethod: DeliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
        deliveryDatetime: LocalDateTime = futureDateTime
    ) = Order(
        id = id,
        customer = testUser,
        service = testService,
        storageLocation = testStorage,
        status = status,
        orderDatetime = pastDateTime,
        deliveryDatetime = deliveryDatetime,
        deliveryOption = DeliveryOption.STANDARD,
        deliveryMethod = deliveryMethod,
        isPaymentDone = false,
        notes = null
    )

    @Test
    fun `createOrder - sends new order notification when creating brand new order and newOrders setting is true`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(newOrders = true))
        fakeStorageRepo.saveLocation(testStorage)

        val newOrderTemplate = createOrder(id = null, status = OrderStatus.NEW)
        manager.createOrder(newOrderTemplate)
        testScheduler.advanceUntilIdle()

        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `createOrder - does not send new order notification when updating existing order`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(newOrders = true))
        fakeStorageRepo.saveLocation(testStorage)

        val existingOrder = createOrder(id = "existing_123", status = OrderStatus.NEW)
        manager.createOrder(existingOrder)

        assertEquals(0, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `updateOrderStatus - READY status notifies when readyOrders notification setting is true`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(readyOrders = true))
        fakeOrderRepo.saveOrder(createOrder(id = "order_1", status = OrderStatus.IN_PROGRESS))

        manager.updateOrderStatus("order_1", OrderStatus.READY)

        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `updateOrderStatus - READY status does not notify when readyOrders notification setting is false`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(readyOrders = false))
        fakeOrderRepo.saveOrder(createOrder(id = "order_1", status = OrderStatus.IN_PROGRESS))

        manager.updateOrderStatus("order_1", OrderStatus.READY)

        assertEquals(0, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `notifyOrderNotDelivered - sends orderNotDelivered notification when enabled`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(orderNotDelivered = true))
        fakeOrderRepo.saveOrder(createOrder(id = "order_1", status = OrderStatus.OUT_FOR_DELIVERY, deliveryMethod = DeliveryMethod.HOME_DELIVERY))

        manager.notifyOrderNotDelivered("order_1")

        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `updateOrderStatus - IN_PROGRESS status does not trigger status update notification`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(readyOrders = true, newOrders = true))
        fakeOrderRepo.saveOrder(createOrder(id = "order_1", status = OrderStatus.NEW))

        manager.updateOrderStatus("order_1", OrderStatus.IN_PROGRESS)

        assertEquals(0, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `checkAndNotifyOrderAlerts - sends notification for late orders when enabled`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(lateOrders = true))
        val lateOrder = createOrder(id = "late_1", status = OrderStatus.NEW, deliveryDatetime = pastDateTime)
        fakeOrderRepo.saveOrder(lateOrder)

        manager.checkAndNotifyOrderAlerts()

        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `checkAndNotifyOrderAlerts - does not send duplicate notifications on the same day`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(lateOrders = true))
        val lateOrder = createOrder(id = "late_1", status = OrderStatus.NEW, deliveryDatetime = pastDateTime)
        fakeOrderRepo.saveOrder(lateOrder)

        manager.checkAndNotifyOrderAlerts()
        assertEquals(1, fakeNotificationRepo.savedNotifications.size)

        // Calling checkAndNotifyOrderAlerts again on the same day should skip duplicate
        manager.checkAndNotifyOrderAlerts()
        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `checkAndNotifyOrderAlerts - does not send notification for late orders when disabled`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(lateOrders = false))
        val lateOrder = createOrder(id = "late_1", status = OrderStatus.NEW, deliveryDatetime = pastDateTime)
        fakeOrderRepo.saveOrder(lateOrder)

        manager.checkAndNotifyOrderAlerts()

        assertEquals(0, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `checkAndNotifyOrderAlerts - sends notification for not picked up orders when enabled`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(lateOrders = false, orderNotPickedUp = true))
        val uncollectedOrder = createOrder(
            id = "uncollected_1",
            status = OrderStatus.READY,
            deliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
            deliveryDatetime = pastDateTime
        )
        fakeOrderRepo.saveOrder(uncollectedOrder)

        manager.checkAndNotifyOrderAlerts()

        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    @Test
    fun `checkAndNotifyOrderAlerts - sends notification for not delivered orders when enabled`() = runTest {
        val manager = createOrderManager(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
        fakeSettingsRepo.updateNotificationSettings(NotificationSettings(lateOrders = false, orderNotDelivered = true))
        val undeliveredOrder = createOrder(
            id = "undelivered_1",
            status = OrderStatus.READY,
            deliveryMethod = DeliveryMethod.HOME_DELIVERY,
            deliveryDatetime = pastDateTime
        )
        fakeOrderRepo.saveOrder(undeliveredOrder)

        manager.checkAndNotifyOrderAlerts()

        assertEquals(1, fakeNotificationRepo.savedNotifications.size)
    }

    private class FakeOrderRepository : OrderRepository {
        private val ordersMap = mutableMapOf<String, Order>()
        private val ordersFlow = MutableStateFlow<List<Order>>(emptyList())

        override fun getOrders(): Flow<List<Order>> = ordersFlow.asStateFlow()
        override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> = flowOf(ordersMap.values.filter { it.customer.id == customerId })

        override suspend fun getOrderById(id: String): Result<Order, DataError> {
            return ordersMap[id]?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }

        override suspend fun saveOrder(order: Order): Result<String, DataError> {
            val id = order.id ?: "gen_id"
            val saved = order.copy(id = id)
            ordersMap[id] = saved
            ordersFlow.value = ordersMap.values.toList()
            return Result.Success(id)
        }

        override suspend fun deleteOrder(order: Order): Result<Unit, DataError> {
            ordersMap.remove(order.id)
            ordersFlow.value = ordersMap.values.toList()
            return Result.Success(Unit)
        }

        override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
            val existing = ordersMap[orderId] ?: return Result.Failure(DataError.NOT_FOUND)
            ordersMap[orderId] = existing.copy(status = newStatus)
            ordersFlow.value = ordersMap.values.toList()
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
        fun updateNotificationSettings(new: NotificationSettings) {
            _settings.update { it.copy(notification = new) }
        }
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
}
