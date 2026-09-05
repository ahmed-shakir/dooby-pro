package se.supernovait.doobypro.domain.model.storage

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.storage_type_cabinet
import doobypro.shared.generated.resources.storage_type_drawer
import doobypro.shared.generated.resources.storage_type_hanger
import doobypro.shared.generated.resources.storage_type_other
import doobypro.shared.generated.resources.storage_type_shelf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Categorizes the physical nature of a storage area.
 *
 * This helps staff identify what kind of equipment is used for storing the items
 * (e.g., hanging them on a rack versus placing them on a shelf).
 *
 * @property label The localized string resource for the storage type name.
 */
@Serializable
enum class StorageType(val label: StringResource) {
    /** Hanging space for garments. */
    HANGER(Res.string.storage_type_hanger),
    
    /** Horizontal storage space. */
    SHELF(Res.string.storage_type_shelf),
    
    /** Enclosed pull-out storage. */
    DRAWER(Res.string.storage_type_drawer),
    
    /** Larger enclosed storage unit. */
    CABINET(Res.string.storage_type_cabinet),
    
    /** Catch-all for other storage equipment. */
    OTHER(Res.string.storage_type_other)
}
