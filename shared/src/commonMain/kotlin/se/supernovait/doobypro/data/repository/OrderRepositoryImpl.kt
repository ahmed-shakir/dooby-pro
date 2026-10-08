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
import se.supernovait.app.core.domain.observability.crash.CrashReporter
import se.supernovait.app.core.domain.observability.logging.Logger
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
import se.supernovait.doobypro.domain.util.LogTags
import kotlin.coroutines.CoroutineContext
import kotlin.time.Clock

/**
 * Implementation of [OrderRepository] using the Assembly Pattern.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrderRepositoryImpl(
    private val logger: Logger,
    private val crashReporter: CrashReporter,
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    private val serviceDao: ServiceDao,
    private val storageLocationDao: StorageLocationDao
) : OrderRepository {
    private val ioContext: CoroutineContext = Dispatchers.IO

    override fun getOrders(): Flow<List<Order>> {
        logger.debug("Observing all orders stream", tag = LogTags.ORDER_REPO)
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
        logger.debug("Observing orders for customer with ID: $customerId", tag = LogTags.ORDER_REPO)
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
            logger.debug("Fetching order with ID: $id", tag = LogTags.ORDER_REPO)
            val order = orderDao.getById(id)

            if (order != null) {
                val user = userDao.getById(order.customerId)?.toDomain()
                val service = serviceDao.getById(order.serviceId)?.toDomain()
                val storageLocation = storageLocationDao.getById(order.storageLocationId)?.toDomain()

                if (user != null && service != null && storageLocation != null) {
                    Result.Success(order.toDomain(user, service, storageLocation))
                } else {
                    logger.warn("Order with ID: $id is missing dependent components (user=$user, service=$service, storage=$storageLocation)", tag = LogTags.ORDER_REPO)
                    Result.Failure(DataError.NOT_FOUND)
                }
            } else {
                logger.warn("Order entity not found with ID: $id", tag = LogTags.ORDER_REPO)
                Result.Failure(DataError.NOT_FOUND)
            }
        }
    }

    override suspend fun saveOrder(order: Order): Result<String, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Saving order with ID: ${order.id}", tag = LogTags.ORDER_REPO)
                val entityToSave = order.toEntity()
                orderDao.upsert(entityToSave)
                Result.Success(entityToSave.id)
            } catch (e: Exception) {
                logger.error("Error saving order with ID: ${order.id}", e, tag = LogTags.ORDER_REPO)
                crashReporter.recordException(e, mapOf("action" to "saveOrder", "orderId" to (order.id ?: "new")))
                Result.Failure(DataError.DATABASE_ERROR)
            }
        }
    }

    override suspend fun deleteOrder(order: Order): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Deleting order with ID: ${order.id}", tag = LogTags.ORDER_REPO)
                orderDao.delete(order.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error deleting order with ID: ${order.id}", e, tag = LogTags.ORDER_REPO)
                crashReporter.recordException(e, mapOf("action" to "deleteOrder", "orderId" to (order.id ?: "unknown")))
                Result.Failure(DataError.UNKNOWN)
            }
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
        return withContext(ioContext) {
            try {
                logger.info("Updating status for order with ID: $orderId to $newStatus", tag = LogTags.ORDER_REPO)
                val order = orderDao.getById(orderId)
                if (order == null) {
                    logger.warn("Cannot update status: Order not found with ID: $orderId", tag = LogTags.ORDER_REPO)
                    return@withContext Result.Failure(DataError.NOT_FOUND)
                }

                // If transitioning to a terminal status, release the storage slot
                if (newStatus.isTerminal()) {
                    logger.info("Order with ID: $orderId reached terminal status $newStatus, releasing storage slot for location with ID: ${order.storageLocationId}", tag = LogTags.ORDER_REPO)
                    storageLocationDao.decrementOccupiedSlots(order.storageLocationId)
                }

                orderDao.updateOrderStatus(orderId, newStatus, Clock.System.now())
                Result.Success(Unit)
            } catch (e: Exception) {
                logger.error("Error updating status for order with ID: $orderId", e, tag = LogTags.ORDER_REPO)
                crashReporter.recordException(e, mapOf("action" to "updateOrderStatus", "orderId" to orderId, "newStatus" to newStatus.name))
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
