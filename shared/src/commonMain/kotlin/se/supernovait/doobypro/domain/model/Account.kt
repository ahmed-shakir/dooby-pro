package se.supernovait.doobypro.domain.model

import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.doobypro.domain.model.agreement.Agreement

/**
 * Domain model representing a unified Dooby Account.
 *
 * This model orchestrates user identity, company profile, and associated
 * licenses or agreements into a single aggregate root.
 *
 * @property id The unique identifier for the account, matching the [Company.id].
 * @property user The [User] associated with this account.
 * @property company The [Company] profile for this account.
 * @property license The active [License] associated with this account, if any.
 * @property agreement The equipment lease [Agreement] associated with this account, if any.
 * @property deactivatedAt The timestamp when the account was deactivated, if any.
 * @property isMarkedForDeletion Whether the account is pending permanent deletion.
 */
data class Account(
    val id: String? = null,
    val user: User,
    val company: Company,
    val license: License? = null,
    val agreement: Agreement? = null,
    val deactivatedAt: LocalDateTime? = null,
    val isMarkedForDeletion: Boolean = false
) {
    /**
     * Returns whether the account is currently active.
     */
    val isActive: Boolean
        get() = deactivatedAt == null && !isMarkedForDeletion
}
