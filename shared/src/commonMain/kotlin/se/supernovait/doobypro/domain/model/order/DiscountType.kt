package se.supernovait.doobypro.domain.model.order

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.discount_type_flat_amount
import doobypro.shared.generated.resources.discount_type_percentage
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Defines the type of discount calculation on an order (flat amount or percentage).
 *
 * @property label The localized string resource representing the discount type.
 */
@Serializable
enum class DiscountType(val label: StringResource) {
    /**
     * Discount is a fixed flat amount value.
     */
    FLAT_AMOUNT(Res.string.discount_type_flat_amount),

    /**
     * Discount is calculated as a percentage markup/reduction over the items subtotal.
     */
    PERCENTAGE(Res.string.discount_type_percentage)
}
