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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import se.supernovait.app.core.domain.auth.AuthRepository
import se.supernovait.app.core.domain.auth.AuthenticationManager
import se.supernovait.app.core.domain.auth.AuthenticationState
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.common.Result
import se.supernovait.app.core.domain.common.getOrNull
import se.supernovait.app.core.domain.error.DataError
import se.supernovait.app.core.domain.extension.isAlreadyNotifiedToday
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.extension.toUrl
import se.supernovait.app.core.domain.extension.truncateToMinutes
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.app.core.domain.model.notification.NotificationType
import se.supernovait.app.core.domain.notification.NotificationManager
import se.supernovait.app.core.domain.sharing.ShareConfiguration
import se.supernovait.doobypro.domain.model.Service
import se.supernovait.doobypro.domain.model.company.Company
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.settings.notification.NotificationSchedule
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import se.supernovait.doobypro.domain.repository.AccountRepository
import se.supernovait.doobypro.domain.repository.BusinessHoursRepository
import se.supernovait.doobypro.domain.repository.OrderRepository
import se.supernovait.doobypro.domain.repository.ServiceRepository
import se.supernovait.doobypro.domain.repository.SettingsRepository
import se.supernovait.doobypro.domain.repository.StorageLocationRepository
import se.supernovait.doobypro.domain.util.LogTags
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
    private val shareConfiguration: ShareConfiguration,
    private val authenticationManager: AuthenticationManager,
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val businessHoursRepository: BusinessHoursRepository,
    private val logger: Logger
) : KoinComponent {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        logger.info("OrderManager initialized", tag = LogTags.ORDER_MANAGER)
        managerScope.launch {
            try {
                authenticationManager.authState
                    .map { it is AuthenticationState.Authenticated }
                    .distinctUntilChanged()
                    .collect { isAuthenticated ->
                        if (isAuthenticated) {
                            try {
                                checkAndNotifyOrderAlerts()
                            } catch (e: Exception) {
                                logger.error("Error processing order alerts on auth state change", e, tag = LogTags.ORDER_MANAGER)
                            }
                        }
                    }
            } catch (e: Exception) {
                logger.error("Error observing auth state in OrderManager", e, tag = LogTags.ORDER_MANAGER)
            }
        }
    }

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
        logger.info("Reissuing order with ID: ${originalOrder.id}", tag = LogTags.ORDER_MANAGER)
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
        logger.info("Creating order for customer: ${order.customer.username}", tag = LogTags.ORDER_MANAGER)
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
                logger.info("Order created successfully with ID: ${result.data}", tag = LogTags.ORDER_MANAGER)
                val settings = settingsRepository.settings.first()
                if (settings.notification.newOrders && order.id == null) {
                    notificationManager.notify(
                        title = getString(Res.string.screen_Order_label_new_order),
                        message = getString(Res.string.notification_order_updated_message, result.data, getString(OrderStatus.NEW.label)),
                        type = NotificationType.SUCCESS,
                        showNativeAlert = shouldSendPlatformNotification(),
                        deepLink = Route.OrderDetails(result.data).toUrl(shareConfiguration)
                    )
                }
            }
            result
        } catch (e: Exception) {
            logger.error("Failed to create order", e, tag = LogTags.ORDER_MANAGER)
            Result.Failure(DataError.UNKNOWN)
        }
    }

    /**
     * Transitions an order to its next logical status.
     */
    suspend fun transitionToNextStatus(order: Order): Result<Unit, DataError> {
        val nextStatus = order.getNextStatus() ?: return Result.Failure(DataError.UNKNOWN)
        logger.info("Transitioning order with ID: ${order.id} from ${order.status} to next status: $nextStatus", tag = LogTags.ORDER_MANAGER)
        return updateOrderStatus(order.id!!, nextStatus)
    }

    /**
     * Updates an order's status and handles side effects (like storage release).
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit, DataError> {
        logger.info("Updating status for order with ID: $orderId to $newStatus", tag = LogTags.ORDER_MANAGER)
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
                    showNativeAlert = shouldSendPlatformNotification(),
                    deepLink = Route.OrderDetails(orderId).toUrl(shareConfiguration)
                )
            }

            if (newStatus.isTerminal()) {
                if (currentOrder != null) {
                    logger.info("Order with ID: $orderId reached terminal status. Releasing storage location with ID: ${currentOrder.storageLocation.id}", tag = LogTags.ORDER_MANAGER)
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
        logger.warn("Notifying order not delivered for order with ID: $orderId", tag = LogTags.ORDER_MANAGER)
        val settings = settingsRepository.settings.first()
        if (settings.notification.orderNotDelivered) {
            val title = getString(Res.string.notification_order_not_delivered_title)
            val message = getString(Res.string.notification_order_not_delivered_message, orderId)

            notificationManager.notify(
                title = title,
                message = message,
                type = NotificationType.WARNING,
                showNativeAlert = shouldSendPlatformNotification(),
                deepLink = Route.OrderDetails(orderId).toUrl(shareConfiguration)
            )
        }
    }

    /**
     * Checks active orders and sends notifications for late orders, orders not picked up, and orders not delivered
     * based on notification settings. Each alert type per order is sent at most once per day until resolved.
     */
    suspend fun checkAndNotifyOrderAlerts() {
        logger.debug("Checking and processing order alerts", tag = LogTags.ORDER_MANAGER)
        if (!shouldSendPlatformNotification()) return

        val settings = settingsRepository.settings.first()
        val orders = orderRepository.getOrders().first()
        val allNotifications = notificationManager.notifications.first()

        orders.forEach { order ->
            val orderId = order.id ?: return@forEach
            val deepLink = Route.OrderDetails(orderId).toUrl(shareConfiguration)

            if (settings.notification.lateOrders && order.isLate()) {
                val title = getString(Res.string.notification_order_late_title)
                if (!allNotifications.isAlreadyNotifiedToday(title, deepLink)) {
                    logger.warn("Order with ID: $orderId is late, sending notification", tag = LogTags.ORDER_MANAGER)
                    val message = getString(Res.string.notification_order_late_message, orderId)
                    notificationManager.notify(
                        title = title,
                        message = message,
                        type = NotificationType.WARNING,
                        showNativeAlert = true,
                        deepLink = deepLink
                    )
                }
            } else if (settings.notification.orderNotPickedUp && order.isNotPickedUp()) {
                val title = getString(Res.string.notification_order_not_picked_up_title)
                if (!allNotifications.isAlreadyNotifiedToday(title, deepLink)) {
                    logger.warn("Order with ID: $orderId is not picked up, sending notification", tag = LogTags.ORDER_MANAGER)
                    val message = getString(Res.string.notification_order_not_picked_up_message, orderId)
                    notificationManager.notify(
                        title = title,
                        message = message,
                        type = NotificationType.WARNING,
                        showNativeAlert = true,
                        deepLink = deepLink
                    )
                }
            } else if (settings.notification.orderNotDelivered && order.isNotDelivered()) {
                val title = getString(Res.string.notification_order_not_delivered_title)
                if (!allNotifications.isAlreadyNotifiedToday(title, deepLink)) {
                    logger.warn("Order with ID: $orderId is not delivered, sending notification", tag = LogTags.ORDER_MANAGER)
                    val message = getString(Res.string.notification_order_not_delivered_message, orderId)
                    notificationManager.notify(
                        title = title,
                        message = message,
                        type = NotificationType.WARNING,
                        showNativeAlert = true,
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
        logger.info("Cancelling order with ID: ${order.id}", tag = LogTags.ORDER_MANAGER)
        return updateOrderStatus(order.id!!, OrderStatus.CANCELLED)
    }

    /**
     * Deletes an order if it is in the NEW status.
     */
    suspend fun deleteOrder(order: Order): Result<Unit, DataError> {
        if (!order.canDelete()) return Result.Failure(DataError.UNKNOWN)
        logger.info("Deleting order with ID: ${order.id}", tag = LogTags.ORDER_MANAGER)
        val result = orderRepository.deleteOrder(order)
        if (result is Result.Success) {
            storageLocationManager.releaseStorageLocation(order.storageLocation.id!!)
        }
        return result
    }

    private suspend fun shouldSendPlatformNotification(): Boolean {
        val settings = settingsRepository.settings.first()
        return when (settings.notification.notificationSchedule) {
            NotificationSchedule.ANYTIME -> true
            NotificationSchedule.DAYTIME -> {
                val hour = LocalDateTime.now().hour
                hour in 8..20
            }
            NotificationSchedule.BUSINESS_HOURS -> {
                val company = getCompany()
                if (company?.id != null) {
                    when (val res = businessHoursRepository.getBusinessHours(company.id)) {
                        is Result.Success -> res.data.isOpenNow()
                        else -> true
                    }
                } else {
                    true
                }
            }
        }
    }

    private suspend fun getCompany(): Company? {
        val userId = authRepository.getCurrentUserId().getOrNull() ?: return null
        val account = accountRepository.getAccountByUserId(userId).getOrNull() ?: return null
        return account.company
    }
}
