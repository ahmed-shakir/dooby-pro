package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.doobypro.data.local.dao.FakeStorageLocationDao
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StorageLocationRepositoryImplTest {
    private lateinit var fakeDao: FakeStorageLocationDao
    private lateinit var repository: StorageLocationRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        fakeDao = FakeStorageLocationDao()
        repository = StorageLocationRepositoryImpl(fakeDao)
    }

    @Test
    fun `getActiveLocations should return only active locations`() = runTest(testDispatcher) {
        val l1 = StorageLocation(id = "1", label = "Active", isActive = true)
        val l2 = StorageLocation(id = "2", label = "Inactive", isActive = false)
        fakeDao.upsert(l1.toEntity())
        fakeDao.upsert(l2.toEntity())

        val result = repository.getActiveLocations().first()
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }

    @Test
    fun `getLocationById should return location if exists`() = runTest(testDispatcher) {
        val location = StorageLocation(id = "loc_1", label = "Test")
        fakeDao.upsert(location.toEntity())

        val result = repository.getLocationById("loc_1").getOrNull()
        assertNotNull(result)
        assertEquals("loc_1", result.id)
    }

    @Test
    fun `getDefaultLocation should return default location`() = runTest(testDispatcher) {
        val defaultLoc = StorageLocation(id = "def", label = "Default", isDefault = true)
        fakeDao.upsert(defaultLoc.toEntity())

        val result = repository.getDefaultLocation().getOrNull()
        assertNotNull(result)
        assertTrue(result.isDefault)
    }

    @Test
    fun `incrementOccupiedSlots should update dao`() = runTest(testDispatcher) {
        val location = StorageLocation(id = "loc_1", label = "Test", capacity = 10, occupiedSlots = 0)
        fakeDao.upsert(location.toEntity())

        repository.incrementOccupiedSlots("loc_1")
        
        val updated = fakeDao.getById("loc_1")
        assertEquals(1, updated?.occupiedSlots)
    }
}
