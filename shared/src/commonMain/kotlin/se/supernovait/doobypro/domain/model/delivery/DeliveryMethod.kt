package se.supernovait.doobypro.domain.model.delivery

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.delivery_method_home_delivery
import doobypro.shared.generated.resources.delivery_method_in_store_pickup
import org.jetbrains.compose.resources.StringResource

/**
 * Defines the available methods for delivering an order.
 */
enum class DeliveryMethod(val label: StringResource) {
    /**
     * The order is delivered to the customer's home.
     */
    HOME_DELIVERY(Res.string.delivery_method_home_delivery),

    /**
     * The customer picks up the order from a designated location.
     */
    IN_STORE_PICKUP(Res.string.delivery_method_in_store_pickup)
}
