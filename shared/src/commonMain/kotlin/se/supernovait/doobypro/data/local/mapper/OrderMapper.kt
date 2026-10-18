package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.order.Order
import se.supernovait.doobypro.domain.model.order.OrderServiceItem
import se.supernovait.doobypro.domain.model.storage.StorageLocation
import kotlin.time.Clock

/**
 * Extension function to map [OrderEntity] to [Order] domain model.
 *
 * @param customer The [User] object associated with the order.
 * @param items The list of [OrderServiceItem] line items associated with the order.
 * @param storageLocation The [StorageLocation] object associated with the order.
 * @return The mapped [Order] model.
 */
fun OrderEntity.toDomain(
    customer: User,
    items: List<OrderServiceItem>,
    storageLocation: StorageLocation
) = Order(
    id = id,
    customer = customer,
    items = items,
    storageLocation = storageLocation,
    status = status,
    isPaymentDone = isPaymentDone,
    orderDatetime = orderDatetime,
    deliveryDatetime = deliveryDatetime,
    deliveryOption = deliveryOption,
    deliveryMethod = deliveryMethod,
    expressFeeType = expressFeeType,
    expressFeePercentage = expressFeePercentage,
    expressFee = expressFee,
    deliveryFee = deliveryFee,
    notes = notes,
    createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
    updatedAt = updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
)

/**
 * Extension function to map [Order] domain model to [OrderEntity].
 *
 * @return The mapped [OrderEntity].
 */
fun Order.toEntity() = OrderEntity(
    id = id ?: SupernovaIdGenerator.generateId(IdType.ORDER.prefix),
    customerId = customer.id!!,
    serviceIds = items.joinToString(",") { "${it.service.id}:${it.quantity}" },
    storageLocationId = storageLocation.id ?: throw IllegalStateException("Storage location ID is required for persistence."),
    status = status,
    isPaymentDone = isPaymentDone,
    orderDatetime = orderDatetime,
    deliveryDatetime = deliveryDatetime,
    deliveryOption = deliveryOption,
    deliveryMethod = deliveryMethod,
    expressFeeType = expressFeeType,
    expressFeePercentage = expressFeePercentage,
    expressFee = expressFee,
    deliveryFee = deliveryFee,
    notes = notes,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
)
