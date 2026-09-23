package se.supernovait.doobypro.domain.manager

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.notification_order_late_message
import doobypro.shared.generated.resources.notification_order_late_title
import doobypro.shared.generated.resources.notification_order_not_delivered_message
import doobypro.shared.generated.resources.notification_order_not_delivered_title
import doobypro.shared.generated.resources.notification_order_not_picked_up_message
import doobypro.shared.generated.resources.notification_order_not_picked_up_title
import doobypro.shared.generated.resources.notification_order_updated_message
import doobypro.shared.generated.resources.notification_order_updated_title
import doobypro.shared.generated.resources.screen_Order_label_new_order
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.extension.truncateToMinutes
import se.supernovait.app.core.domain.model.notification.NotificationType
import se.supernovait.app.core.domain.navigation.toUrl
import se.supernovait.app.core.domain.notification.NotificationManager
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.presentation.navigation.Route
import kotlin.time.Clock

/**
 * Manager responsible for complex order-related business operations.
 */
class OrderManager(
    private val orderRepository: OrderRepository,
    private val serviceRepository: ServiceRepository,
    private val storageLocationManager: StorageLocationManager,
    private val storageLocationRepository: StorageLocationRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationManager: NotificationManager,
    private val shareConfiguration: ShareConfiguration
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
            serviceRepository.getServiceById(id).getOrNull()
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
            
            val result = orderRepository.saveOrder(finalizedOrder)

            if (result is Result.Success) {
                val settings = settingsRepository.settings.first()
                if (settings.notification.newOrders && order.id == null) {
                    notificationManager.notify(
                        title = getString(Res.string.screen_Order_label_new_order),
                        message = getString(Res.string.notification_order_updated_message, result.data, getString(OrderStatus.NEW.label)),
                        type = NotificationType.SUCCESS,
                        deepLink = Route.OrderDetails(result.data).toUrl(shareConfiguration)
                    )
                }
            }
            result
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
        val currentOrder = (orderRepository.getOrderById(orderId) as? Result.Success)?.data
        val result = orderRepository.updateOrderStatus(orderId, newStatus)

        if (result is Result.Success) {
            val settings = settingsRepository.settings.first()

            val shouldNotify = when (newStatus) {
                OrderStatus.READY -> settings.notification.readyOrders
                else -> false
            }

            if (shouldNotify) {
                val title = getString(Res.string.notification_order_updated_title)
                val statusName = getString(newStatus.label)
                val message = getString(Res.string.notification_order_updated_message, orderId, statusName)

                notificationManager.notify(
                    title = title,
                    message = message,
                    type = NotificationType.INFO,
                    deepLink = Route.OrderDetails(orderId).toUrl(shareConfiguration)
                )
            }

            if (newStatus.isTerminal()) {
                if (currentOrder != null) {
                    storageLocationManager.releaseStorageLocation(currentOrder.storageLocation.id!!)
                }
            }
        }

        return result
    }

    /**
     * Triggers a "Not Delivered" warning notification if enabled in notification settings.
     */
    suspend fun notifyOrderNotDelivered(orderId: String) {
        val settings = settingsRepository.settings.first()
        if (settings.notification.orderNotDelivered) {
            val title = getString(Res.string.notification_order_not_delivered_title)
            val message = getString(Res.string.notification_order_not_delivered_message, orderId)

            notificationManager.notify(
                title = title,
                message = message,
                type = NotificationType.WARNING,
                deepLink = Route.OrderDetails(orderId).toUrl(shareConfiguration)
            )
        }
    }

    /**
     * Checks active orders and sends notifications for late orders, orders not picked up, and orders not delivered
     * based on notification settings. Each alert type per order is sent at most once per day until resolved.
     */
    suspend fun checkAndNotifyOrderAlerts() {
        val settings = settingsRepository.settings.first()
        val orders = orderRepository.getOrders().first()
        val allNotifications = notificationManager.notifications.first()
        val today = LocalDateTime.now().date

        orders.forEach { order ->
            val orderId = order.id ?: return@forEach
            val deepLink = Route.OrderDetails(orderId).toUrl(shareConfiguration)

            fun isAlreadyNotifiedToday(title: String): Boolean {
                return allNotifications.any { notification ->
                    notification.title == title &&
                        notification.deepLink == deepLink &&
                        notification.timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date == today
                }
            }

            if (settings.notification.lateOrders && order.isLate()) {
                val title = getString(Res.string.notification_order_late_title)
                if (!isAlreadyNotifiedToday(title)) {
                    val message = getString(Res.string.notification_order_late_message, orderId)
                    notificationManager.notify(
                        title = title,
                        message = message,
                        type = NotificationType.WARNING,
                        deepLink = deepLink
                    )
                }
            } else if (settings.notification.orderNotPickedUp && order.isNotPickedUp()) {
                val title = getString(Res.string.notification_order_not_picked_up_title)
                if (!isAlreadyNotifiedToday(title)) {
                    val message = getString(Res.string.notification_order_not_picked_up_message, orderId)
                    notificationManager.notify(
                        title = title,
                        message = message,
                        type = NotificationType.WARNING,
                        deepLink = deepLink
                    )
                }
            } else if (settings.notification.orderNotDelivered && order.isNotDelivered()) {
                val title = getString(Res.string.notification_order_not_delivered_title)
                if (!isAlreadyNotifiedToday(title)) {
                    val message = getString(Res.string.notification_order_not_delivered_message, orderId)
                    notificationManager.notify(
                        title = title,
                        message = message,
                        type = NotificationType.WARNING,
                        deepLink = deepLink
                    )
                }
            }
        }
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
