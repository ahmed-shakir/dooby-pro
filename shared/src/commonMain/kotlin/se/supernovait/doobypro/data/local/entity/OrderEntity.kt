package se.supernovait.doobypro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.DoobyIdType
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption

/**
 * Database entity representing an order in the system.
 *
 * @property id The unique identifier for the order, generated using [SupernovaIdGenerator].
 * @property customerId The ID of the customer who placed the order.
 * @property serviceId The ID of the service being ordered.
 * @property deliveryOption The chosen delivery option (e.g., Express, Standard).
 * @property deliveryMethod The chosen delivery method (e.g., Home Delivery, Pickup).
 * @property orderDate The date and time when the order was placed.
 * @property deliveryDate The scheduled date and time for delivery.
 * @property note An optional note provided by the customer.
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(DoobyIdType.ORDER.prefix),
    val customerId: String,
    val serviceId: String,
    val deliveryOption: DeliveryOption,
    val deliveryMethod: DeliveryMethod,
    val orderDate: LocalDateTime,
    val deliveryDate: LocalDateTime,
    val note: String
)
