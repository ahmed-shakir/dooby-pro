package se.supernovait.doobypro.data.local.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import se.supernovait.app.core.data.persistence.mapper.toDomain
import se.supernovait.app.core.data.persistence.mapper.toEntity
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
    collectionMethod = collectionMethod,
    expressFeeType = expressFeeType,
    expressFeePercentage = expressFeePercentage?.toDomain(),
    expressFee = expressFee?.toDomain(),
    pickupFee = pickupFee?.toDomain(),
    deliveryFee = deliveryFee?.toDomain(),
    subunitRounding = subunitRounding?.toDomain(),
    discountType = discountType,
    discountPercentage = discountPercentage?.toDomain(),
    discount = discount?.toDomain(),
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
    collectionMethod = collectionMethod,
    expressFeeType = expressFeeType,
    expressFeePercentage = expressFeePercentage?.toEntity(),
    expressFee = expressFee?.toEntity(),
    pickupFee = pickupFee?.toEntity(),
    deliveryFee = deliveryFee?.toEntity(),
    subunitRounding = subunitRounding?.toEntity(),
    discountType = discountType,
    discountPercentage = discountPercentage?.toEntity(),
    discount = discount?.toEntity(),
    notes = notes,
    createdAt = createdAt.toInstant(TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now()
)
