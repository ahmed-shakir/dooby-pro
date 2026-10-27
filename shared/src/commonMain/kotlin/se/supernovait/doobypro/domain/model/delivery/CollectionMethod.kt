package se.supernovait.doobypro.domain.model.delivery

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.collection_method_home_pickup
import doobypro.shared.generated.resources.collection_method_in_store_drop_off
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Defines the available methods for collecting/dropping off an order.
 */
@Serializable
enum class CollectionMethod(val label: StringResource) {
    /**
     * The laundry company picks up items from the customer's home.
     */
    HOME_PICKUP(Res.string.collection_method_home_pickup),

    /**
     * The customer drops off items at the store.
     */
    IN_STORE_DROP_OFF(Res.string.collection_method_in_store_drop_off)
}
