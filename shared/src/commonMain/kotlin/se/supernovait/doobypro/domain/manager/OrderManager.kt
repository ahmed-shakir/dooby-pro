package se.supernovait.doobypro.domain.manager

import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.extension.truncateToMinutes
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import kotlin.time.Clock

/**
 * Manager responsible for complex order-related business operations.
 */
class OrderManager(
    private val orderRepository: OrderRepository,
    private val serviceRepository: ServiceRepository,
    private val storageLocationManager: StorageLocationManager,
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository
) {

    /**
     * Creates a new order template populated with default values from settings.
     */
    suspend fun createOrderTemplate(customer: User): Order {
        val orderSettings = settingsRepository.settings.first().order
        val storageSettings = settingsRepository.settings.first().storage
        val orderDatetime = LocalDateTime.now().truncateToMinutes()
        val deliveryDatetime = Clock.System.now()
            .plus(orderSettings.defaultDeliveryDaysOffset, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .truncateToMinutes()

        val defaultService = orderSettings.defaultServiceId?.let { id ->
            serviceRepository.getServiceById(id).let { result ->
                if (result is Result.Success) result.data else null
            }
        } ?: Service()

        val defaultStorage = storageLocationRepository.getLocationById(storageSettings.defaultStorageLocationId).getOrNull()
            ?: storageLocationRepository.getDefaultLocation().getOrNull()
            ?: StorageLocation()

        return Order(
            customer = customer,
            service = defaultService,
            storageLocation = defaultStorage,
            status = OrderStatus.NEW,
            orderDatetime = orderDatetime,
            deliveryDatetime = deliveryDatetime,
            deliveryOption = orderSettings.defaultDeliveryOption,
            deliveryMethod = orderSettings.defaultDeliveryMethod,
            isPaymentDone = false,
            notes = null
        )
    }

    /**
     * Creates a new order based on an existing order (re-issue pattern).
     */
    suspend fun reissueOrder(originalOrder: Order): Order {
        val template = createOrderTemplate(originalOrder.customer)
        return template.copy(
            service = originalOrder.service,
            deliveryOption = originalOrder.deliveryOption,
            deliveryMethod = originalOrder.deliveryMethod,
            notes = originalOrder.notes
        )
    }

    /**
     * Creates a new order, assigning storage and calculating expected times.
     */
    suspend fun createOrder(order: Order): Result<String, DataError> {
        return try {
            val assignedLocationId = storageLocationManager.assignStorageLocation(order.storageLocation.id)
            
            // Hydrate the full storage location object before saving
            val fullLocation = storageLocationRepository.getLocationById(assignedLocationId).getOrNull()
                ?: throw IllegalStateException("Assigned storage location not found.")

            val finalizedOrder = order.copy(
                storageLocation = fullLocation
            )
            
            orderRepository.saveOrder(finalizedOrder)
        } catch (e: Exception) {
            Result.Failure(DataError.UNKNOWN)
        }
    }

    /**
     * Transitions an order to its next logical status.
     */
    suspend fun transitionToNextStatus(order: Order): Result<Unit, DataError> {
        val nextStatus = order.getNextStatus() ?: return Result.Failure(DataError.UNKNOWN)
        return updateOrderStatus(order.id!!, nextStatus)
    }

    /**
     * Updates an order's status and handles side effects (like storage release).
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
        val result = orderRepository.updateOrderStatus(orderId, newStatus)
        
        if (result is Result.Success && newStatus.isTerminal()) {
            val order = orderRepository.getOrderById(orderId)
            if (order is Result.Success) {
                storageLocationManager.releaseStorageLocation(order.data.storageLocation.id!!)
            }
        }
        
        return result
    }

    /**
     * Cancels an order and releases its storage slot.
     */
    suspend fun cancelOrder(order: Order): Result<Unit, DataError> {
        if (order.status.isTerminal()) return Result.Failure(DataError.UNKNOWN)
        
        return updateOrderStatus(order.id!!, OrderStatus.CANCELLED)
    }

    /**
     * Deletes an order if it is in the NEW status.
     */
    suspend fun deleteOrder(order: Order): Result<Unit, DataError> {
        if (!order.canDelete()) return Result.Failure(DataError.UNKNOWN)
        
        val result = orderRepository.deleteOrder(order)
        if (result is Result.Success) {
            storageLocationManager.releaseStorageLocation(order.storageLocation.id!!)
        }
        return result
    }
}
