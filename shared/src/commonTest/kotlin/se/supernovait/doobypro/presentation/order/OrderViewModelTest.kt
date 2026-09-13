package se.supernovait.doobypro.presentation.order

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.entity.UserEntity
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.model.billing.Amount
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {
    private lateinit var viewModel: OrderViewModel
    private lateinit var fakeOrderRepo: FakeOrderRepository
    private lateinit var fakeServiceRepo: FakeServiceRepository
    private lateinit var fakeStorageRepo: FakeStorageLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeCustomerRepo: FakeCustomerRepository
    private lateinit var fakeUserDao: FakeUserDao
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
        
        val storageManager = StorageLocationManager(
            fakeStorageRepo, fakeSettingsRepo
        )
        
        orderManager = OrderManager(fakeOrderRepo, fakeServiceRepo, storageManager, fakeStorageRepo, fakeSettingsRepo)
        orderQueryManager = OrderQueryManager(fakeOrderRepo)
        
        viewModel = OrderViewModel(fakeOrderRepo, fakeServiceRepo, fakeStorageRepo, fakeSettingsRepo, fakeCustomerRepo, orderManager, orderQueryManager)
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
        override fun getServices(): Flow<List<Service>> = MutableStateFlow(emptyList())
        override suspend fun getServiceById(id: String) = Result.Failure(DataError.NOT_FOUND)
        override suspend fun saveService(service: Service) = Result.Success("")
        override suspend fun deleteService(service: Service) = Result.Success(Unit)
    }

    private class FakeStorageLocationRepository : StorageLocationRepository {
        private val _locations = MutableStateFlow<List<StorageLocation>>(emptyList())
        override fun getActiveLocations(): Flow<List<StorageLocation>> = _locations
        
        override suspend fun getLocationById(id: String): Result<StorageLocation, DataError> {
            return _locations.value.find { it.id == id }?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }
        
        override suspend fun getDefaultLocation(): Result<StorageLocation, DataError> {
            return _locations.value.find { it.isDefault }?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }
        
        override suspend fun saveLocation(location: StorageLocation): Result<String, DataError> {
            _locations.value = _locations.value + location
            return Result.Success(location.id!!)
        }
        
        override suspend fun deleteLocation(location: StorageLocation) = Result.Success(Unit)
        override suspend fun incrementOccupiedSlots(id: String) {}
        override suspend fun decrementOccupiedSlots(id: String) {}
    }

    private class FakeSettingsRepository : SettingsRepository {
        override val settings: Flow<Settings> = MutableStateFlow(Settings())
        override suspend fun updateSettings(settings: Settings) {}
        override suspend fun resetSettings() {}
    }

    private class FakeCustomerRepository : CustomerRepository {
        private val _customers = MutableStateFlow<List<User>>(emptyList())
        override fun getCustomers(): Flow<List<User>> = _customers
        override suspend fun getCustomerById(id: String): Result<User, DataError> = Result.Failure(DataError.NOT_FOUND)
        override suspend fun saveCustomer(customer: User): Result<String, DataError> {
            _customers.value = _customers.value + customer
            return Result.Success(customer.id ?: "gen")
        }
        override suspend fun deleteCustomer(customer: User): Result<Unit, DataError> = Result.Success(Unit)
    }

    private class FakeUserDao : UserDao {
        override suspend fun getCount(): Long = 0
        override fun observeUserById(id: String): Flow<UserEntity?> = MutableStateFlow(null)
        override fun getAll(): Flow<List<UserEntity>> = MutableStateFlow(emptyList())
        override suspend fun getAllByIds(ids: List<String>): List<UserEntity> = emptyList()
        override suspend fun getById(id: String): UserEntity? = null
        override suspend fun getByUsername(username: String): UserEntity? = null
        override suspend fun upsert(user: UserEntity) {}
        override suspend fun delete(user: UserEntity) {}
    }
}
