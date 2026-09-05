package se.supernovait.doobypro.domain.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.order.OrderSettings
import se.supernovait.doobypro.domain.model.storage.StorageAllocationMode
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StorageLocationManagerTest {
    private lateinit var manager: StorageLocationManager
    private lateinit var fakeStorageRepo: FakeStorageLocationRepository
    private lateinit var fakeSettingsRepo: FakeSettingsRepository
    private lateinit var fakeOrderRepo: FakeOrderRepository

    @BeforeTest
    fun setUp() {
        fakeStorageRepo = FakeStorageLocationRepository()
        fakeSettingsRepo = FakeSettingsRepository()
        fakeOrderRepo = FakeOrderRepository()
        manager = StorageLocationManager(fakeStorageRepo, fakeSettingsRepo, fakeOrderRepo)
    }

    @Test
    fun `assignStorageLocation - MANUAL mode - valid selection`() = runTest {
        val location = StorageLocation(id = "l1", label = "L1", capacity = 10)
        fakeStorageRepo.saveLocation(location)
        fakeSettingsRepo.updateOrderSettings(OrderSettings(storageAllocationMode = StorageAllocationMode.MANUAL))

        val assignedId = manager.assignStorageLocation("l1")

        assertEquals("l1", assignedId)
        assertEquals(1, fakeStorageRepo.incrementCount)
    }

    @Test
    fun `assignStorageLocation - MANUAL mode - missing selection fails`() = runTest {
        fakeSettingsRepo.updateOrderSettings(OrderSettings(storageAllocationMode = StorageAllocationMode.MANUAL))

        assertFailsWith<IllegalArgumentException> {
            manager.assignStorageLocation(null)
        }
    }

    @Test
    fun `assignStorageLocation - AUTO mode - picks first available`() = runTest {
        val l1 = StorageLocation(id = "l1", label = "L1", capacity = 1, occupiedSlots = 1)
        val l2 = StorageLocation(id = "l2", label = "L2", capacity = 5, occupiedSlots = 0)
        fakeStorageRepo.saveLocation(l1)
        fakeStorageRepo.saveLocation(l2)
        fakeSettingsRepo.updateOrderSettings(OrderSettings(storageAllocationMode = StorageAllocationMode.AUTO))

        val assignedId = manager.assignStorageLocation(null)

        assertEquals("l2", assignedId)
        assertEquals(1, fakeStorageRepo.incrementCount)
    }

    @Test
    fun `assignStorageLocation - AUTO mode - falls back to default if all full`() = runTest {
        val l1 = StorageLocation(id = "l1", label = "L1", capacity = 1, occupiedSlots = 1)
        val defaultLoc = StorageLocation(id = "default", label = "Def", isDefault = true)
        fakeStorageRepo.saveLocation(l1)
        fakeStorageRepo.saveLocation(defaultLoc)
        fakeSettingsRepo.updateOrderSettings(OrderSettings(storageAllocationMode = StorageAllocationMode.AUTO))

        val assignedId = manager.assignStorageLocation(null)

        assertEquals("default", assignedId)
    }

    private class FakeStorageLocationRepository : StorageLocationRepository {
        val locations = mutableMapOf<String, StorageLocation>()
        var incrementCount = 0

        override fun getActiveLocations() = flowOf(locations.values.toList())
        
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
        
        override suspend fun deleteLocation(location: StorageLocation) = Result.Success(Unit)
        override suspend fun incrementOccupiedSlots(id: String) { incrementCount++ }
        override suspend fun decrementOccupiedSlots(id: String) {}
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val _settings = MutableStateFlow(Settings())
        override val settings = _settings.asStateFlow()
        override suspend fun updateSettings(settings: Settings) { _settings.value = settings }
        override suspend fun resetSettings() { _settings.value = Settings() }
        fun updateOrderSettings(new: OrderSettings) { 
            _settings.update { it.copy(order = new) }
        }
    }

    private class FakeOrderRepository : OrderRepository {
        override fun getOrders() = flowOf(emptyList<Order>())
        override fun getOrdersByCustomerId(customerId: String) = flowOf(emptyList<Order>())
        override suspend fun getOrderById(id: String) = Result.Failure(DataError.NOT_FOUND)
        override suspend fun saveOrder(order: Order) = Result.Success("")
        override suspend fun deleteOrder(order: Order) = Result.Success(Unit)
        override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) = Result.Success(Unit)
    }
}
