package se.supernovait.doobypro.domain.model

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
 */
data class Account(
    val id: String? = null,
    val user: User,
    val company: Company,
    val license: License? = null,
    val agreement: Agreement? = null
)
