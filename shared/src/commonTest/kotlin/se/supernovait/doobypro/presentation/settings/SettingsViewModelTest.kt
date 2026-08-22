package se.supernovait.doobypro.presentation.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
import kotlin.test.assertNull

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
    fun `onEvent ConnectPrinter should update state`() = runTest {
        val name = "Test Printer"
        val address = "192.168.1.100"
        viewModel.onEvent(SettingsScreenEvent.ConnectPrinter(name, address))
        
        val state = viewModel.uiState.filter { it.settings.printer.printerAddress == address }.first()
        assertEquals(address, state.settings.printer.printerAddress)
        assertEquals(name, state.settings.printer.printerName)
    }

    @Test
    fun `onEvent DisconnectPrinter should clear printer settings`() = runTest(testDispatcher) {
        // Collect in background to keep the StateFlow active and handle WhileSubscribed delay
        val collectJob = launch { viewModel.uiState.collect {} }

        // Wait for flow to be ready and NOT loading
        viewModel.uiState.first { !it.isLoading }

        // Trigger connect and wait for state to reflect the change
        viewModel.onEvent(SettingsScreenEvent.ConnectPrinter("Test", "1.1.1.1"))
        viewModel.uiState.first { it.settings.printer.printerAddress == "1.1.1.1" }

        // Trigger disconnect and wait for state to be null again
        viewModel.onEvent(SettingsScreenEvent.DisconnectPrinter)
        val stateWithoutPrinter = viewModel.uiState.first { it.settings.printer.printerAddress == null }
        assertNull(stateWithoutPrinter.settings.printer.printerAddress)
        
        collectJob.cancel()
    }

    @Test
    fun `onEvent ResetSettings should reset state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateCurrency(Currency.AED))
        viewModel.onEvent(SettingsScreenEvent.ResetSettings)
        
        val state = viewModel.uiState.filter { it.settings.common.currency == Currency.AED }.first()
        assertEquals(Settings(), state.settings)
    }

    @Test
    fun `onEvent UpdateIncludeCompanyLogo should update state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateIncludeCompanyLogo(false))
        
        val state = viewModel.uiState.filter { !it.settings.receipt.includeCompanyLogo }.first()
        assertEquals(false, state.settings.receipt.includeCompanyLogo)
    }

    @Test
    fun `onEvent UpdateIncludeOrderItems should update state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateIncludeOrderItems(false))
        
        val state = viewModel.uiState.filter { !it.settings.receipt.includeOrderItems }.first()
        assertEquals(false, state.settings.receipt.includeOrderItems)
    }

    @Test
    fun `onEvent UpdateLateOrdersNotification should update state`() = runTest {
        viewModel.onEvent(SettingsScreenEvent.UpdateLateOrdersNotification(false))
        
        val state = viewModel.uiState.filter { !it.settings.notifications.lateOrders }.first()
        assertEquals(false, state.settings.notifications.lateOrders)
    }
}
