package se.supernovait.doobypro.domain.model.settings.order

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.order_tag_print_mode_per_order
import doobypro.shared.generated.resources.order_tag_print_mode_per_order_item
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class OrderTagPrintMode(val label: StringResource) {
    PER_ORDER(Res.string.order_tag_print_mode_per_order),
    PER_ORDER_ITEM(Res.string.order_tag_print_mode_per_order_item)
}
