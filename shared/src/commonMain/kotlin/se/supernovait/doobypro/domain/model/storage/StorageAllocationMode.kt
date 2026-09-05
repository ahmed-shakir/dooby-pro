package se.supernovait.doobypro.domain.model.storage

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.storage_allocation_mode_auto
import doobypro.shared.generated.resources.storage_allocation_mode_manual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Defines how storage locations/slots are allocated to orders.
 *
 * This mode determines whether the system or the user is responsible for 
 * deciding where an order is stored.
 *
 * @property label The localized string resource for the allocation mode name.
 */
@Serializable
enum class StorageAllocationMode(val label: StringResource) {

    /**
     * The system automatically assigns the next available storage slot
     * from the configured locations.
     */
    AUTO(Res.string.storage_allocation_mode_auto),

    /**
     * Users must manually select an available storage slot when creating or 
     * processing an order.
     */
    MANUAL(Res.string.storage_allocation_mode_manual)
}
