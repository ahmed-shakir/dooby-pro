package se.supernovait.doobypro.presentation.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.common.Currency
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class ServiceViewModelTest {
    private lateinit var viewModel: ServiceViewModel
    private lateinit var fakeServiceRepository: FakeServiceRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeServiceRepository = FakeServiceRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = ServiceViewModel(fakeServiceRepository, fakeSettingsRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadServices should load and sort services by title`() = runTest(testDispatcher) {
        val services = listOf(
            Service(id = "2", title = "Wash", description = "", price = Amount(0, "AED")),
            Service(id = "1", title = "Dry", description = "", price = Amount(0, "AED"))
        )
        fakeServiceRepository.emit(services)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.services.size)
        assertEquals("Dry", state.services[0].title)
        assertEquals("Wash", state.services[1].title)
    }

    @Test
    fun `SaveService should create new service when editingService has no ID`() = runTest(testDispatcher) {
        viewModel.onEvent(ServiceEvent.EditService(Service())) 
        viewModel.onEvent(ServiceEvent.SaveService("Iron", "Ironing only", 500))
        advanceUntilIdle()

        assertNotNull(fakeServiceRepository.savedService)
        assertEquals("Iron", fakeServiceRepository.savedService?.title)
        assertEquals(500L, fakeServiceRepository.savedService?.price?.raw)
        assertEquals("AED", fakeServiceRepository.savedService?.price?.currency)
    }

    @Test
    fun `SaveService should update existing service when editingService is set`() = runTest(testDispatcher) {
        val existing = Service(id = "s1", title = "Old", description = "", price = Amount(100, "AED"))
        viewModel.onEvent(ServiceEvent.EditService(existing))
        viewModel.onEvent(ServiceEvent.SaveService("New", "Updated", 200))
        advanceUntilIdle()

        assertEquals("s1", fakeServiceRepository.savedService?.id)
        assertEquals("New", fakeServiceRepository.savedService?.title)
        assertEquals(200L, fakeServiceRepository.savedService?.price?.raw)
    }

    @Test
    fun `DeleteService should remove service from repository`() = runTest(testDispatcher) {
        val service = Service(id = "s1", title = "Delete me", description = "", price = Amount(0, "AED"))
        viewModel.onEvent(ServiceEvent.DeleteService(service))
        advanceUntilIdle()

        assertEquals("s1", fakeServiceRepository.deletedServiceId)
    }

    @Test
    fun `observeSettings should update currency in state`() = runTest(testDispatcher) {
        fakeSettingsRepository.emitCurrency(Currency.USD)
        advanceUntilIdle()

        assertEquals("USD", viewModel.uiState.value.currency)
    }

    private class FakeServiceRepository : ServiceRepository {
        private val _services = MutableStateFlow<List<Service>>(emptyList())
        var savedService: Service? = null
        var deletedServiceId: String? = null

        fun emit(services: List<Service>) {
            _services.value = services
        }

        override fun getServices(): Flow<List<Service>> = _services

        override suspend fun getServiceById(id: String): Result<Service, DataError> {
            return _services.value.find { it.id == id }?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }

        override suspend fun saveService(service: Service): Result<String, DataError> {
            savedService = service
            return Result.Success(service.id ?: "gen")
        }

        override suspend fun deleteService(service: Service): Result<Unit, DataError> {
            deletedServiceId = service.id
            return Result.Success(Unit)
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        private val _settings = MutableStateFlow(Settings())
        override val settings = _settings.asStateFlow()

        fun emitCurrency(currency: Currency) {
            _settings.update { it.copy(common = it.common.copy(currency = currency)) }
        }

        override suspend fun updateSettings(settings: Settings) {}
        override suspend fun resetSettings() {}
    }
}
