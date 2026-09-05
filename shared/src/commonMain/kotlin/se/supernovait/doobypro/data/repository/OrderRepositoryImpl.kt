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
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.doobypro.data.local.dao.OrderDao
import se.supernovait.doobypro.data.local.dao.ServiceDao
import se.supernovait.doobypro.data.local.dao.StorageLocationDao
import se.supernovait.doobypro.data.local.mapper.toDomain
import se.supernovait.doobypro.data.local.mapper.toEntity
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [OrderRepository] using the Assembly Pattern.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrderRepositoryImpl(
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    private val serviceDao: ServiceDao,
    private val storageLocationDao: StorageLocationDao
) : OrderRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getOrders(): Flow<List<Order>> {
        return orderDao.getAll().flatMapLatest { entities ->
            if (entities.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val userIds = entities.map { it.customerId }.distinct()
            val serviceIds = entities.map { it.serviceId }.distinct()
            val storageLocationIds = entities.map { it.storageLocationId }.distinct()

            combine(
                assembleUsersFlow(userIds),
                assembleServicesFlow(serviceIds),
                assembleStorageLocationsFlow(storageLocationIds)
            ) { usersMap, servicesMap, storageMap ->
                entities.mapNotNull { entity ->
                    val user = usersMap[entity.customerId] ?: return@mapNotNull null
                    val service = servicesMap[entity.serviceId] ?: return@mapNotNull null
                    val storageLocation = storageMap[entity.storageLocationId] ?: return@mapNotNull null
                    entity.toDomain(user, service, storageLocation)
                }
            }
        }
    }

    override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> {
        return orderDao.getByCustomerId(customerId).flatMapLatest { entities ->
            if (entities.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val serviceIds = entities.map { it.serviceId }.distinct()
            val storageLocationIds = entities.map { it.storageLocationId }.distinct()

            combine(
                assembleUsersFlow(listOf(customerId)),
                assembleServicesFlow(serviceIds),
                assembleStorageLocationsFlow(storageLocationIds)
            ) { usersMap, servicesMap, storageMap ->
                val user = usersMap[customerId] ?: return@combine emptyList()
                entities.mapNotNull { entity ->
                    val service = servicesMap[entity.serviceId] ?: return@mapNotNull null
                    val storageLocation = storageMap[entity.storageLocationId] ?: return@mapNotNull null
                    entity.toDomain(user, service, storageLocation)
                }
            }
        }
    }

    override suspend fun getOrderById(id: String): Result<Order, DataError> {
        return withContext(ioContext) {
            val order = orderDao.getById(id)

            if (order != null) {
                val user = userDao.getById(order.customerId)?.toDomain()
                val service = serviceDao.getById(order.serviceId)?.toDomain()
                val storageLocation = storageLocationDao.getById(order.storageLocationId)?.toDomain()

                if (user != null && service != null && storageLocation != null) {
                    Result.Success(order.toDomain(user, service, storageLocation))
                } else {
                    Result.Failure(DataError.NOT_FOUND)
                }
            } else {
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveOrder(order: Order): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                val entityToSave = order.toEntity()
                orderDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteOrder(order: Order): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                orderDao.delete(order.toEntity())
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                val order = orderDao.getById(orderId) ?: return@withContext Result.Failure(DataError.NOT_FOUND)
                
                // If transitioning to a terminal status, release the storage slot
                if (newStatus.isTerminal()) {
                    storageLocationDao.decrementOccupiedSlots(order.storageLocationId)
                }

                orderDao.updateOrderStatus(orderId, newStatus)
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Failure(DataError.DATABASE_ERROR)
            }
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

    private fun assembleStorageLocationsFlow(ids: List<String>): Flow<Map<String, StorageLocation>> = flow {
        val locations = storageLocationDao.getAllByIds(ids).associateBy({ it.id }, { it.toDomain() })
        emit(locations)
    }
}
