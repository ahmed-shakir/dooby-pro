package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.data.local.entity.OrderEntity
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.Order
import se.supernovait.doobypro.domain.model.Service

/**
 * Extension function to map [OrderEntity] to [Order] domain model.
 *
 * @param customer The [User] object associated with the order.
 * @param service The [Service] object associated with the order.
 * @return The mapped [Order] model.
 */
fun OrderEntity.toDomain(customer: User, service: Service): Order {
    return Order(
        id = id,
        customer = customer,
        service = service,
        deliveryOption = deliveryOption,
        deliveryMethod = deliveryMethod,
        orderDate = orderDate,
        deliveryDate = deliveryDate,
        isPaymentDone = isPaymentDone,
        notes = notes
    )
}

/**
 * Extension function to map [Order] domain model to [OrderEntity].
 *
 * @return The mapped [OrderEntity].
 */
fun Order.toEntity(): OrderEntity {
    return OrderEntity(
        id = id ?: SupernovaIdGenerator.generateId(IdType.ORDER.prefix),
        customerId = customer.id!!,
        serviceId = service.id!!,
        deliveryOption = deliveryOption,
        deliveryMethod = deliveryMethod,
        orderDate = orderDate,
        deliveryDate = deliveryDate,
        isPaymentDone = isPaymentDone,
        notes = notes
    )
}
