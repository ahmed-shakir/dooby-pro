package se.supernovait.doobypro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity representing a Dooby Account for Room persistence.
 *
 * This entity primarily stores the links (IDs) between the different components
 * that make up an account.
 *
 * @property id The unique identifier for the account (matches the companyId).
 * @property userId The ID of the associated user.
 * @property licenseId The ID of the associated license, if any.
 * @property agreementId The ID of the associated equipment lease agreement, if any.
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val licenseId: String?,
    val agreementId: String?
)
