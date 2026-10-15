package se.supernovait.doobypro.domain.model.settings.order

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.express_fee_type_flat_fee
import doobypro.shared.generated.resources.express_fee_type_percentage
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Defines the type of express fee calculation.
 *
 * @property label The localized string resource representing the express fee type.
 */
@Serializable
enum class ExpressFeeType(val label: StringResource) {
    /**
     * Express fee is calculated as a percentage markup over the base price.
     */
    PERCENTAGE(Res.string.express_fee_type_percentage),

    /**
     * Express fee is a fixed flat fee amount.
     */
    FLAT_FEE(Res.string.express_fee_type_flat_fee)
}
