package se.supernovait.doobypro.domain.model.settings.order

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.storage_allocation_mode_auto
import doobypro.shared.generated.resources.storage_allocation_mode_manual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Defines how storage locations/slots are allocated to orders.
 */
@Serializable
enum class StorageAllocationMode(val label: StringResource) {

    /**
     * The system automatically assigns the next available storage slot.
     */
    AUTO(Res.string.storage_allocation_mode_auto),

    /**
     * Users manually assign a storage slot to the order.
     */
    MANUAL(Res.string.storage_allocation_mode_manual)
}
