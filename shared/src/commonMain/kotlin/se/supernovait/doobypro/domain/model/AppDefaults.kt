package se.supernovait.doobypro.domain.model

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.default_license_description
import doobypro.shared.generated.resources.default_license_title
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.getString
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.app.core.domain.model.license.LicenseStatus
import se.supernovait.app.core.domain.model.license.Tier

object AppDefaults {
    const val COUNTRY = "UAE"
    const val COUNTRY_CODE = "+971"

    suspend fun license(accountId: String) = License(
        id = SupernovaIdGenerator.generateId(IdType.LICENSE.prefix),
        accountId = accountId,
        licenseStatus = LicenseStatus.ACTIVE,
        tier = Tier.FREE,
        title = getString(Res.string.default_license_title),
        description = getString(Res.string.default_license_description),
        issueDate = LocalDate.now(),
        expiryDate = LocalDate.now().plus(DatePeriod(years = 1))
    )
}
