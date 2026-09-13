package se.supernovait.doobypro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Database entity representing a Dooby Account for Room persistence.
 *
 * This entity primarily stores the links (IDs) between the different components
 * that make up an account.
 *
 * @property id The unique identifier for the account (matches the companyId).
 * @property userId The ID of the associated user.
 * @property licenseId The ID of the associated license, if any.
 * @property agreementIds The list of IDs of the associated agreements, if any.
 * @property deactivatedAt The timestamp when the account was deactivated, if any.
 * @property isMarkedForDeletion Whether the account is pending permanent deletion.
 * @property createdAt The timestamp when the account record was created.
 * @property updatedAt The timestamp when the account record was last updated.
 */
@Entity(
    tableName = "accounts",
    indices = [Index(value = ["userId"])]
)
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val licenseId: String?,
    val agreementIds: List<String>,
    val deactivatedAt: Instant? = null,
    val isMarkedForDeletion: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
