package se.supernovait.doobypro.domain.model

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import se.supernovait.app.core.domain.auth.User
import se.supernovait.app.core.domain.extension.now
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.location.Address
import se.supernovait.app.core.domain.model.license.License
import se.supernovait.app.core.domain.model.license.LicenseStatus
import se.supernovait.app.core.domain.model.license.Tier
import se.supernovait.doobypro.domain.model.company.Company

object TestUser {
    val andy = User(
        id = SupernovaIdGenerator.generateId(IdType.USER.prefix),
        username = "andy_b",
        firstname = "Andy",
        lastname = "Black",
        birthdate = LocalDate(1990, 6, 12),
        email = "andy.black@example.com",
        phoneNumber = "+971559342250"
    )
}

object TestCompany {
    val laundryZahia = Company(
        id = SupernovaIdGenerator.generateId(IdType.COMPANY.prefix),
        legalName = "Laundry Al Zahia FZCO",
        displayName = "Zahia Laundry",
        licenseNumber = "TL-12300",
        phoneNumber = "+971501003535",
        email = "zahia.laundry@example.com",
        address = TestAddress.laundryZahia
    )
}

object TestAddress {
    val laundryZahia = Address(
        street = "Imaginary Street 3",
        city = "Sharjah",
        subdivision = Emirate.SHARJAH.value,
        country = AppDefaults.COUNTRY
    )
}

object TestAccount {
    val andyBusinessAccount = Account(
        id = TestCompany.laundryZahia.id,
        user = TestUser.andy,
        company = TestCompany.laundryZahia,
        license = License(
            id = SupernovaIdGenerator.generateId(IdType.LICENSE.prefix),
            accountId = TestCompany.laundryZahia.id ?: "",
            licenseStatus = LicenseStatus.Active,
            tier = Tier.Free,
            title = "Dooby Pro Free Tier License",
            description = "Get started with essential order management for your laundry or dry cleaning business. The Free Tier includes basic features to manage staff operations, track orders, and connect with customers—perfect for small teams or those new to digital workflow management. Upgrade anytime to unlock advanced features.",
            issueDate = LocalDate.now(),
            expiryDate = LocalDate.now().plus(DatePeriod(years = 1))
        )
    )
}
