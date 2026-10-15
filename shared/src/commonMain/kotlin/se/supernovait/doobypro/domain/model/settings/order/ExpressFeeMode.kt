package se.supernovait.doobypro.domain.model.settings.order

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.express_fee_mode_global
import doobypro.shared.generated.resources.express_fee_mode_individual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Defines how express service fees are configured for orders.
 *
 * @property label The localized string resource representing the express fee mode.
 */
@Serializable
enum class ExpressFeeMode(val label: StringResource) {
    /**
     * Express fee is applied globally based on shop settings.
     */
    GLOBAL(Res.string.express_fee_mode_global),

    /**
     * Express fee is specified individually for each express order.
     */
    INDIVIDUAL(Res.string.express_fee_mode_individual)
}
