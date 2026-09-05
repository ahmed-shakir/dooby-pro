package se.supernovait.doobypro.presentation.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StorageViewModelTest {
    private lateinit var viewModel: StorageViewModel
    private lateinit var fakeRepository: FakeStorageLocationRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeStorageLocationRepository()
        viewModel = StorageViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLocations should sort locations with default on top and others by label`() = runTest {
        val locations = listOf(
            StorageLocation(id = "2", label = "Z Rack", isDefault = false),
            StorageLocation(id = "1", label = "A Shelf", isDefault = false),
            StorageLocation(id = "default", label = "Uncategorized", isDefault = true),
            StorageLocation(id = "3", label = "B Cabinet", isDefault = false)
        )
        fakeRepository.emit(locations)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(4, state.locations.size)
        assertEquals("default", state.locations[0].id) // Default on top
        assertEquals("1", state.locations[1].id)       // A Shelf
        assertEquals("3", state.locations[2].id)       // B Cabinet
        assertEquals("2", state.locations[3].id)       // Z Rack
    }

    private class FakeStorageLocationRepository : StorageLocationRepository {
        private val _locations = MutableStateFlow<List<StorageLocation>>(emptyList())
        
        fun emit(locations: List<StorageLocation>) {
            _locations.value = locations
        }

        override fun getActiveLocations(): Flow<List<StorageLocation>> = _locations

        override suspend fun getLocationById(id: String): Result<StorageLocation, DataError> {
            return _locations.value.find { it.id == id }?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }

        override suspend fun getDefaultLocation(): Result<StorageLocation, DataError> {
            return _locations.value.find { it.isDefault }?.let { Result.Success(it) } ?: Result.Failure(DataError.NOT_FOUND)
        }

        override suspend fun saveLocation(location: StorageLocation): Result<String, DataError> {
            return Result.Success(location.id ?: "gen")
        }

        override suspend fun deleteLocation(location: StorageLocation): Result<Unit, DataError> {
            return Result.Success(Unit)
        }

        override suspend fun incrementOccupiedSlots(id: String) {}
        override suspend fun decrementOccupiedSlots(id: String) {}
    }
}
