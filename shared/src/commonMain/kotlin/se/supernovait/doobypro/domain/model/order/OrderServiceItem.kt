package se.supernovait.doobypro.domain.model.order

import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.doobypro.domain.model.Service

/**
 * Represents a line item in an order consisting of a service and its quantity.
 *
 * @property service The [Service] being ordered.
 * @property quantity The number of items/pieces for this service.
 */
data class OrderServiceItem(
    val service: Service,
    val quantity: Int = 1
) {
    /**
     * Calculates the subtotal amount for this line item (unit price * quantity).
     */
    val subtotal: Amount
        get() = Amount(service.price.raw * quantity, service.price.currency)
}
