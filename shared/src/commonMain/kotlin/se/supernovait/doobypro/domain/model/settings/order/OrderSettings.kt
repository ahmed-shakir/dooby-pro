package se.supernovait.doobypro.domain.model.settings.order

import kotlinx.serialization.Serializable
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption

/**
 * Settings related to order management and processing.
 *
 * @property defaultServiceId The ID of the service selected by default when creating a new order.
 * @property defaultDeliveryOption The delivery priority (e.g., Express) used by default.
 * @property defaultDeliveryMethod The delivery method (e.g., Pickup) used by default.
 * @property defaultDeliveryDaysOffset The expected time to complete an order in days.
 * @property autoPrintReceipts Whether the app should automatically trigger printing when an order is created.
 * @property autoPrintOrderTag Whether to automatically print a tag for the order.
 * @property orderTagPrintMode The mode for printing order tags (per order or per order item).
 * @property expressFeeMode The mode for determining express service fees (global or individual per order).
 * @property expressFeeType The type of express fee calculation (percentage markup or flat fee).
 * @property expressFeeValue The default express fee value (percentage or flat amount in minor units) when using global express fee settings.
 */
@Serializable
data class OrderSettings(
    val defaultServiceId: String? = null,
    val defaultDeliveryOption: DeliveryOption = DeliveryOption.STANDARD,
    val defaultDeliveryMethod: DeliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
    val defaultDeliveryDaysOffset: Int = 2,
    val autoPrintReceipts: Boolean = false,
    val autoPrintOrderTag: Boolean = true,
    val orderTagPrintMode: OrderTagPrintMode = OrderTagPrintMode.PER_ORDER,
    val expressFeeMode: ExpressFeeMode = ExpressFeeMode.GLOBAL,
    val expressFeeType: ExpressFeeType = ExpressFeeType.PERCENTAGE,
    val expressFeeValue: Long = 50
)
