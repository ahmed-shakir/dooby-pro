package se.supernovait.doobypro.presentation.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import se.supernovait.doobypro.data.local.preferences.FakeDataStore
import se.supernovait.doobypro.data.repository.FakeServiceRepository
import se.supernovait.doobypro.data.repository.SettingsRepositoryImpl
import se.supernovait.doobypro.domain.model.settings.Settings
import se.supernovait.doobypro.domain.model.settings.common.Currency
import se.supernovait.doobypro.presentation.settings.event.SettingsScreenEvent
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeDataStore: FakeDataStore
    private lateinit var settingsRepository: SettingsRepositoryImpl
    private lateinit var serviceRepository: FakeServiceRepository
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDataStore = FakeDataStore()
        settingsRepository = SettingsRepositoryImpl(fakeDataStore, Json { 
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
        serviceRepository = FakeServiceRepository()
        viewModel = SettingsViewModel(settingsRepository, serviceRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should reflect default settings initially`() = runTest {
        val state = viewModel.uiState.value
        assertEquals(Settings(), state.settings)
    }

    @Test
    fun `onEvent UpdateCurrency should update state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateCurrency(Currency.AED))
        
        val state = viewModel.uiState.filter { it.settings.common.currency == Currency.AED }.first()
        assertEquals(Currency.AED, state.settings.common.currency)
    }

    @Test
    fun `onEvent UpdateDefaultHandlingTimeDays should update state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateDefaultHandlingTimeDays(5))
        
        val state = viewModel.uiState.filter { it.settings.order.defaultHandlingTimeDays == 5 }.first()
        assertEquals(5, state.settings.order.defaultHandlingTimeDays)
    }

    @Test
    fun `onEvent UpdateDefaultServiceId should update state`() = runTest {
        val serviceId = "service_123"
        viewModel.onEvent(SettingsScreenEvent.UpdateDefaultServiceId(serviceId))
        
        val state = viewModel.uiState.filter { it.settings.order.defaultServiceId == serviceId }.first()
        assertEquals(serviceId, state.settings.order.defaultServiceId)
    }

    @Test
    fun `onEvent ResetSettings should reset state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateCurrency(Currency.AED))
        viewModel.onEvent(SettingsScreenEvent.ResetSettings)
        
        val state = viewModel.uiState.filter { it.settings.common.currency == Currency.AED }.first()
        assertEquals(Settings(), state.settings)
    }
}
