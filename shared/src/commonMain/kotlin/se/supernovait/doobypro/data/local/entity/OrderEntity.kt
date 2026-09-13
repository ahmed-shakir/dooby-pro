package se.supernovait.doobypro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import se.supernovait.doobypro.domain.model.order.OrderStatus
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Database entity representing an order in the persistent store.
 *
 * @property id The unique identifier for the order, generated using [SupernovaIdGenerator].
 * @property customerId The ID of the customer who placed the order.
 * @property serviceId The ID of the service being ordered.
 * @property storageLocationId The ID of the assigned storage area.
 * @property status The current lifecycle stage of the order.
 * @property orderDatetime The date and time when the order was placed.
 * @property deliveryDatetime The scheduled date and time for delivery.
 * @property deliveryOption The chosen delivery priority (e.g., [DeliveryOption.EXPRESS], [DeliveryOption.STANDARD]).
 * @property deliveryMethod The chosen delivery method (e.g., [DeliveryMethod.HOME_DELIVERY], [DeliveryMethod.IN_STORE_PICKUP]).
 * @property isPaymentDone Whether the order has been paid for.
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
    val serviceId: String,
    val storageLocationId: String = "default",
    val status: OrderStatus,
    val orderDatetime: LocalDateTime,
    val deliveryDatetime: LocalDateTime,
    val deliveryOption: DeliveryOption,
    val deliveryMethod: DeliveryMethod,
    val isPaymentDone: Boolean,
    val notes: String?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
