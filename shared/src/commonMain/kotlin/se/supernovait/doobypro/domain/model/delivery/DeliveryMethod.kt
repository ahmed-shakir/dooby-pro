package se.supernovait.doobypro.domain.model.delivery

/**
 * Defines the available methods for delivering an order.
 */
enum class DeliveryMethod {
    /**
     * The order is delivered to the customer's home.
     */
    HOME_DELIVERY,

    /**
     * The customer picks up the order from a designated location.
     */
    PICKUP
}
