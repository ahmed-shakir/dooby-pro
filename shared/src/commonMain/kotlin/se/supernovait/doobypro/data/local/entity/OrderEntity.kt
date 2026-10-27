package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.data.persistence.entity.PercentageEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.OrderStatus
import se.supernovait.doobypro.domain.model.settings.order.ExpressFeeType
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Database entity representing an order in the persistent store.
 *
 * @property id The unique identifier for the order, generated using [SupernovaIdGenerator].
 * @property customerId The ID of the customer who placed the order.
 * @property serviceIds Comma-separated list of service IDs being ordered.
 * @property storageLocationId The ID of the assigned storage area.
 * @property status The current lifecycle stage of the order.
 * @property isPaymentDone Whether the order has been paid for.
 * @property orderDatetime The date and time when the order was placed.
 * @property deliveryDatetime The scheduled date and time for delivery.
 * @property deliveryOption The chosen delivery priority (e.g., [DeliveryOption.EXPRESS], [DeliveryOption.STANDARD]).
 * @property deliveryMethod The chosen delivery method (e.g., [DeliveryMethod.HOME_DELIVERY], [DeliveryMethod.IN_STORE_PICKUP]).
 * @property expressFeeType The calculation type (percentage or flat fee) used when express fee was created.
 * @property expressFeePercentage The percentage value if express fee was percentage-based.
 * @property expressFee The express fee associated with the order.
 * @property deliveryFee The delivery fee associated with the order.
 * @property notes Optional notes provided by the customer.
 * @property createdAt The timestamp when the order was first created.
 * @property updatedAt The timestamp when the order was last modified.
 */
@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["customerId", "status"]),
        Index(value = ["status"]),
        Index(value = ["storageLocationId"]),
        Index(value = ["deliveryDatetime"])
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(IdType.ORDER.prefix),
    val customerId: String,
    val serviceIds: String,
    val storageLocationId: String = "default",
    val status: OrderStatus,
    val isPaymentDone: Boolean,
    val orderDatetime: LocalDateTime,
    val deliveryDatetime: LocalDateTime,
    val deliveryOption: DeliveryOption,
    val deliveryMethod: DeliveryMethod,
    val expressFeeType: ExpressFeeType? = null,
    @Embedded(prefix = "express_pct_")
    val expressFeePercentage: PercentageEntity? = null,
    @Embedded(prefix = "express_fee_")
    val expressFee: AmountEntity? = null,
    @Embedded(prefix = "delivery_fee_")
    val deliveryFee: AmountEntity? = null,
    @Embedded(prefix = "subunit_rounding_")
    val subunitRounding: AmountEntity? = null,
    val notes: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
