package se.supernovait.doobypro.domain.model.order

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.decimal.Decimal
import se.supernovait.app.core.domain.model.decimal.Percentage
import se.supernovait.doobypro.domain.model.AppDefaults
import se.supernovait.doobypro.domain.model.delivery.CollectionMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeType
import se.supernovait.doobypro.domain.model.storage.StorageLocation

/**
 * Domain model representing a customer order for laundry services.
 *
 * This model contains the full details of the customer, the service items requested,
 * scheduling, and internal operational details like storage placement.
 *
 * @property id The unique identifier for the order. Null for unsaved orders.
 * @property customer The [User] who placed the order.
 * @property items The list of [OrderServiceItem] line items being ordered.
 * @property storageLocation The assigned [StorageLocation] area.
 * @property status The current lifecycle stage of the order.
 * @property isPaymentDone Whether the order has been paid for.
 * @property orderDatetime The date and time the order was placed.
 * @property deliveryDatetime The scheduled completion or delivery date and time.
 * @property deliveryOption The chosen delivery priority (e.g., Express, Standard).
 * @property deliveryMethod The chosen delivery method (e.g., Home Delivery, In-Store Pickup).
 * @property collectionMethod The chosen collection/drop-off method (e.g., In-Store Drop-off, Home Pickup).
 * @property expressFeeType The [ExpressFeeType] calculation mode (percentage or flat fee) used when the express fee was set.
 * @property expressFeePercentage The percentage markup value if express fee was percentage-based.
 * @property expressFee The express fee applied when [deliveryOption] is [DeliveryOption.EXPRESS].
 * @property pickupFee The fixed pickup fee applied when [collectionMethod] is [CollectionMethod.HOME_PICKUP].
 * @property deliveryFee The fixed delivery fee applied when [deliveryMethod] is [DeliveryMethod.HOME_DELIVERY].
 * @property subunitRounding The subunit rounding amount applied to the order.
 * @property notes Optional internal or customer-provided notes.
 * @property createdAt The timestamp when the order was first created.
 * @property updatedAt The timestamp when the order was last modified.
 */
data class Order(
    val id: String? = null,
    val customer: User,
    val items: List<OrderServiceItem> = emptyList(),
    val storageLocation: StorageLocation,
    val status: OrderStatus = OrderStatus.NEW,
    val isPaymentDone: Boolean = false,
    val orderDatetime: LocalDateTime = LocalDateTime.now(),
    val deliveryDatetime: LocalDateTime = LocalDateTime.now(),
    val deliveryOption: DeliveryOption = DeliveryOption.STANDARD,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
    val collectionMethod: CollectionMethod = CollectionMethod.IN_STORE_DROP_OFF,
    val expressFeeType: ExpressFeeType? = null,
    val expressFeePercentage: Percentage? = null,
    val expressFee: Amount? = null,
    val pickupFee: Amount? = null,
    val deliveryFee: Amount? = null,
    val subunitRounding: Amount? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Calculates the total price of all order items.
     */
    val totalPrice: Amount
        get() {
            val currency = items.firstOrNull()?.service?.price?.currency ?: AppDefaults.CURRENCY
            val totalDecimal = items.fold(Decimal.ZERO) { acc, item -> acc + item.subtotal.decimal } +
                (expressFee?.decimal ?: Decimal.ZERO) +
                (deliveryFee?.decimal ?: Decimal.ZERO) +
                (pickupFee?.decimal ?: Decimal.ZERO) +
                (subunitRounding?.decimal ?: Decimal.ZERO)
            return Amount(totalDecimal, currency)
        }

    /**
     * Determines the next logical status in the workflow based on the current state.
     *
     * @return The next [OrderStatus], or null if the order is already in a terminal state.
     */
    fun getNextStatus(): OrderStatus? = status.next(deliveryMethod)

    /**
     * Checks if the order is eligible for cancellation.
     */
    fun canCancel(): Boolean = !status.isTerminal()

    /**
     * Checks if the order can be permanently deleted from the active list.
     */
    fun canDelete(): Boolean = status.canBeDeleted()

    /**
     * Checks if the order is considered late based on the current time.
     * Late orders are orders in status NEW or IN_PROGRESS, or HOME_DELIVERY orders in READY or OUT_FOR_DELIVERY status,
     * that are at least 24 hours past the delivery date.
     */
    fun isLate(): Boolean {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val lateThreshold = deliveryDatetime.toInstant(systemTimeZone)
            .plus(LATE_THRESHOLD_HOURS, DateTimeUnit.HOUR)
            .toLocalDateTime(systemTimeZone)
        val isLateThresholdPassed = lateThreshold < LocalDateTime.now() && !status.isTerminal()
        val isNotReady = status == OrderStatus.NEW || status == OrderStatus.IN_PROGRESS
        val isHomeDelivery = deliveryMethod == DeliveryMethod.HOME_DELIVERY
        val isNotDelivered = isHomeDelivery && (status == OrderStatus.READY || status == OrderStatus.OUT_FOR_DELIVERY)

        return isLateThresholdPassed && (isNotReady || isNotDelivered)
    }

    /**
     * Checks if the order is not picked up based on the current time.
     * Not picked up is for orders with delivery method IN_STORE_PICKUP, status READY, and past delivery datetime by at least 24 hours.
     */
    fun isNotPickedUp(): Boolean {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val overdueThreshold = deliveryDatetime.toInstant(systemTimeZone)
            .plus(LATE_THRESHOLD_HOURS, DateTimeUnit.HOUR)
            .toLocalDateTime(systemTimeZone)
        return deliveryMethod == DeliveryMethod.IN_STORE_PICKUP &&
            status == OrderStatus.READY &&
            overdueThreshold < LocalDateTime.now()
    }

    /**
     * Checks if the order is not delivered based on the current time.
     * Not delivered is for orders with delivery method HOME_DELIVERY, status READY, and past delivery datetime by at least 24 hours.
     */
    fun isNotDelivered(): Boolean {
        val systemTimeZone = TimeZone.currentSystemDefault()
        val overdueThreshold = deliveryDatetime.toInstant(systemTimeZone)
            .plus(LATE_THRESHOLD_HOURS, DateTimeUnit.HOUR)
            .toLocalDateTime(systemTimeZone)
        return deliveryMethod == DeliveryMethod.HOME_DELIVERY &&
            status == OrderStatus.READY &&
            overdueThreshold < LocalDateTime.now()
    }

    /**
     * Checks if the order is overdue (either late, not picked up, or not delivered).
     */
    fun isOverdue(): Boolean = isLate() || isNotPickedUp() || isNotDelivered()

    companion object {
        const val LATE_THRESHOLD_HOURS = 24
    }
}
