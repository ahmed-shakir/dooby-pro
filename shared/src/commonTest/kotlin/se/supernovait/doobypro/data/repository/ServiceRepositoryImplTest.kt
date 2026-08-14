package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.data.local.dao.FakeServiceDao
import se.supernovait.doobypro.domain.model.DoobyIdType
import se.supernovait.doobypro.domain.model.Service
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [ServiceRepositoryImpl].
 */
class ServiceRepositoryImplTest {

    private lateinit var fakeServiceDao: FakeServiceDao
    private lateinit var repository: ServiceRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val testService = Service(
        id = SupernovaIdGenerator.generateId(DoobyIdType.SERVICE.prefix),
        title = "Haircut",
        description = "Basic haircut service",
        price = Amount(2500, "AED")
    )

    @BeforeTest
    fun setUp() {
        fakeServiceDao = FakeServiceDao()
        repository = ServiceRepositoryImpl(
            serviceDao = fakeServiceDao,
            ioContext = testDispatcher
        )
    }

    @Test
    fun `getServices should return all services from dao mapped to models`() = runTest(testDispatcher) {
        repository.upsertService(testService)

        val services = repository.getServices().first()

        assertEquals(1, services.size)
        assertEquals(testService, services[0])
    }

    @Test
    fun `getServiceById should return mapped model if found`() = runTest(testDispatcher) {
        repository.upsertService(testService)

        val result = repository.getServiceById(testService.id!!)

        assertEquals(testService, result)
    }

    @Test
    fun `getServiceById should return null if not found`() = runTest(testDispatcher) {
        val result = repository.getServiceById("non-existent")

        assertNull(result)
    }

    @Test
    fun `upsertService should call dao upsert with mapped entity`() = runTest(testDispatcher) {
        repository.upsertService(testService)

        val savedEntity = fakeServiceDao.getById(testService.id!!)
        assertEquals(testService.id, savedEntity?.id)
        assertEquals(testService.title, savedEntity?.title)
    }

    @Test
    fun `deleteService should call dao delete with mapped entity`() = runTest(testDispatcher) {
        repository.upsertService(testService)
        assertEquals(1, repository.getServices().first().size)

        repository.deleteService(testService)

        assertEquals(0, repository.getServices().first().size)
    }
}
