package se.supernovait.doobypro.domain.model.settings.common

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.currency_aed
import doobypro.shared.generated.resources.currency_eur
import doobypro.shared.generated.resources.currency_gbp
import doobypro.shared.generated.resources.currency_usd
import org.jetbrains.compose.resources.StringResource

enum class Currency(val code: String, val symbol: String, val label: StringResource) {
    AED("AED", "AED", Res.string.currency_aed),
    USD("USD", "$", Res.string.currency_usd),
    EUR("EUR", "€", Res.string.currency_eur),
    GBP("GBP", "£", Res.string.currency_gbp)
}
