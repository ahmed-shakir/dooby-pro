package se.supernovait.doobypro.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import se.supernovait.app.core.data.persistence.dao.UserDao
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.domain.auth.User
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Order
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.repository.OrderRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [OrderRepository] using the Assembly Pattern.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrderRepositoryImpl(
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    private val serviceDao: ServiceDao
) : OrderRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getOrders(): Flow<List<Order>> {
        return orderDao.getAll().flatMapLatest { entities ->
            if (entities.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val userIds = entities.map { it.customerId }.distinct()
            val serviceIds = entities.map { it.serviceId }.distinct()

            combine(
                assembleUsersFlow(userIds),
                assembleServicesFlow(serviceIds)
            ) { usersMap, servicesMap ->
                entities.mapNotNull { entity ->
                    val user = usersMap[entity.customerId] ?: return@mapNotNull null
                    val service = servicesMap[entity.serviceId] ?: return@mapNotNull null
                    entity.toDomain(user, service)
                }
            }
        }
    }

    override suspend fun getOrderById(id: String): Order? {
        return withContext(ioContext) {
            val order = orderDao.getById(id) ?: return@withContext null
            val user = userDao.getById(order.customerId)?.toDomain()
                ?: return@withContext null
            val service = serviceDao.getById(order.serviceId)?.toDomain()
                ?: return@withContext null
            
            order.toDomain(user, service)
        }
    }

    override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> {
        return orderDao.getByCustomerId(customerId).flatMapLatest { entities ->
            if (entities.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val serviceIds = entities.map { it.serviceId }.distinct()

            combine(
                assembleUsersFlow(listOf(customerId)),
                assembleServicesFlow(serviceIds)
            ) { usersMap, servicesMap ->
                val user = usersMap[customerId] ?: return@combine emptyList()
                entities.mapNotNull { entity ->
                    val service = servicesMap[entity.serviceId] ?: return@mapNotNull null
                    entity.toDomain(user, service)
                }
            }
        }
    }

    override suspend fun upsertOrder(order: Order) {
        withContext(ioContext) {
            orderDao.upsert(order.toEntity())
        }
    }

    override suspend fun deleteOrder(order: Order) {
        withContext(ioContext) {
            orderDao.delete(order.toEntity())
        }
    }

    private fun assembleUsersFlow(ids: List<String>): Flow<Map<String, User>> = flow {
        val users = userDao.getAllByIds(ids).associateBy({ it.id }, { it.toDomain() })
        emit(users)
    }

    private fun assembleServicesFlow(ids: List<String>): Flow<Map<String, Service>> = flow {
        val services = serviceDao.getAllByIds(ids).associateBy({ it.id }, { it.toDomain() })
        emit(services)
    }
}
