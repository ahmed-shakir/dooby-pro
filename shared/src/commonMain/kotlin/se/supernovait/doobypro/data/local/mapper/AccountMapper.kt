package se.supernovait.doobypro.data.local.mapper

import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.doobypro.data.local.entity.AccountEntity
import se.supernovait.doobypro.domain.model.Account
import se.supernovait.doobypro.domain.model.Company
import se.supernovait.doobypro.domain.model.agreement.Agreement

/**
 * Extension function to map [AccountEntity] to [Account] domain model.
 *
 * This mapping requires the fully resolved components to be passed in.
 */
fun AccountEntity.toDomain(
    user: User,
    company: Company,
    license: License?,
    agreements: List<Agreement>
) = Account(
    id = id,
    user = user,
    company = company,
    license = license,
    agreements = agreements,
    deactivatedAt = deactivatedAt,
    isMarkedForDeletion = isMarkedForDeletion
)

/**
 * Extension function to map [Account] domain model to [AccountEntity].
 */
fun Account.toEntity() = AccountEntity(
    id = id ?: company.id ?: throw IllegalArgumentException("Account company ID cannot be null"),
    userId = user.id ?: throw IllegalArgumentException("Account user ID cannot be null"),
    licenseId = license?.id,
    agreementIds = agreements.mapNotNull { it.id },
    deactivatedAt = deactivatedAt,
    isMarkedForDeletion = isMarkedForDeletion
)
