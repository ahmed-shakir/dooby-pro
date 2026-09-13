package se.supernovait.doobypro.domain.model.order

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.screen_Order_tab_completed
import doobypro.shared.generated.resources.screen_Order_tab_new
import doobypro.shared.generated.resources.screen_Order_tab_processing
import doobypro.shared.generated.resources.screen_Order_tab_ready
import org.jetbrains.compose.resources.StringResource

/**
 * Defines the high-level operational categories for laundry orders.
 */
enum class OrderTab(val label: StringResource) {
    /** Orders recently received. */
    NEW(Res.string.screen_Order_tab_new),
    /** Orders currently being processed. */
    IN_PROGRESS(Res.string.screen_Order_tab_processing),
    /** Orders ready for the customer. */
    READY(Res.string.screen_Order_tab_ready),
    /** Orders that have been delivered/picked up. */
    COMPLETED(Res.string.screen_Order_tab_completed)
}
