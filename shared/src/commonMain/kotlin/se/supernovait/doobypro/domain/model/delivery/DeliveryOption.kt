package se.supernovait.doobypro.domain.model.delivery

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.delivery_option_express
import doobypro.shared.generated.resources.delivery_option_standard
import org.jetbrains.compose.resources.StringResource

/**
 * Defines the priority options for order delivery.
 */
enum class DeliveryOption(val label: StringResource) {
    /**
     * Fast delivery, typically with a higher cost.
     */
    EXPRESS(Res.string.delivery_option_express),

    /**
     * Standard delivery timeframe.
     */
    STANDARD(Res.string.delivery_option_standard)
}
