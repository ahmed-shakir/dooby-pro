package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.data.persistence.entity.UserEntity
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.data.local.dao.FakeOrderDao
import se.supernovait.doobypro.data.local.dao.FakeServiceDao
import se.supernovait.doobypro.data.local.dao.FakeUserDao
import se.supernovait.doobypro.data.local.entity.ServiceEntity
import se.supernovait.doobypro.domain.model.DoobyIdType
import se.supernovait.doobypro.domain.model.Order
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for [OrderRepositoryImpl].
 *
 * Verifies the assembly logic and batch hydration of [Order] domain models.
 */
class OrderRepositoryImplTest {
    private val orderId = SupernovaIdGenerator.generateId(DoobyIdType.ORDER.prefix)
    private val userId = SupernovaIdGenerator.generateId(DoobyIdType.USER.prefix)
    private val serviceId = SupernovaIdGenerator.generateId(DoobyIdType.SERVICE.prefix)

    private lateinit var fakeOrderDao: FakeOrderDao
    private lateinit var fakeServiceDao: FakeServiceDao
    private lateinit var fakeUserDao: FakeUserDao
    private lateinit var repository: OrderRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    private val testDateTime = LocalDateTime(2026, 8, 14, 20, 0, 0)
    
    private val testUser = User(
        id = userId,
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com"
    )

    private val testUserEntity = UserEntity(
        id = userId,
        username = "johndoe",
        firstname = "John",
        lastname = "Doe",
        birthdate = LocalDate(1990, 1, 1),
        email = "john@example.com",
        phoneNumber = null,
        address = null
    )

    private val testService = Service(
        id = serviceId,
        title = "Test Service",
        description = "Description",
        price = Amount(1000, "AED")
    )

    private val testServiceEntity = ServiceEntity(
        id = serviceId,
        title = "Test Service",
        description = "Description",
        price = AmountEntity(1000, "AED")
    )

    private val testOrder = Order(
        id = orderId,
        customer = testUser,
        service = testService,
        deliveryOption = DeliveryOption.EXPRESS,
        deliveryMethod = DeliveryMethod.HOME_DELIVERY,
        orderDate = testDateTime,
        deliveryDate = testDateTime,
        note = "Test note"
    )

    @BeforeTest
    fun setUp() {
        fakeOrderDao = FakeOrderDao()
        fakeServiceDao = FakeServiceDao()
        fakeUserDao = FakeUserDao()
        
        repository = OrderRepositoryImpl(
            userDao = fakeUserDao,
            orderDao = fakeOrderDao,
            serviceDao = fakeServiceDao,
            ioContext = testDispatcher
        )

        // Seed fakes
        runTest(testDispatcher) {
            fakeUserDao.upsert(testUserEntity)
            fakeServiceDao.upsert(testServiceEntity)
        }
    }

    @Test
    fun `getOrders should return assembled orders`() = runTest(testDispatcher) {
        repository.upsertOrder(testOrder)

        val orders = repository.getOrders().first()

        assertEquals(1, orders.size)
        val order = orders[0]
        assertEquals(testOrder.id, order.id)
        assertEquals(testUser.id, order.customer.id)
        assertEquals(testService.id, order.service.id)
    }

    @Test
    fun `getOrderById should return assembled order if found`() = runTest(testDispatcher) {
        repository.upsertOrder(testOrder)

        val result = repository.getOrderById(testOrder.id!!)

        assertNotNull(result)
        assertEquals(testOrder.id, result.id)
        assertEquals(testUser.username, result.customer.username)
    }

    @Test
    fun `getOrderById should return null if related data is missing`() = runTest(testDispatcher) {
        // Upsert order but don't seed service
        val orderWithMissingService = testOrder.copy(
            id = SupernovaIdGenerator.generateId(DoobyIdType.ORDER.prefix), 
            service = testService.copy(id = SupernovaIdGenerator.generateId(DoobyIdType.SERVICE.prefix))
        )
        repository.upsertOrder(orderWithMissingService)

        val result = repository.getOrderById(orderWithMissingService.id!!)

        assertNull(result)
    }

    @Test
    fun `getOrdersByCustomerId should return filtered assembled orders`() = runTest(testDispatcher) {
        repository.upsertOrder(testOrder)
        
        val anotherUserId = SupernovaIdGenerator.generateId(DoobyIdType.USER.prefix)
        val anotherUser = testUser.copy(id = anotherUserId, username = "jane")
        val anotherUserEntity = testUserEntity.copy(id = anotherUserId, username = "jane")
        fakeUserDao.upsert(anotherUserEntity)
        
        repository.upsertOrder(testOrder.copy(id = SupernovaIdGenerator.generateId(DoobyIdType.ORDER.prefix), customer = anotherUser))

        val orders = repository.getOrdersByCustomerId(testUser.id!!).first()

        assertEquals(1, orders.size)
        assertEquals(testUser.id, orders[0].customer.id)
    }
}
