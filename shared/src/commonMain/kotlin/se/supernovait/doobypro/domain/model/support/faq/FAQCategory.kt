package se.supernovait.doobypro.domain.model.support.faq

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.faq_category_account
import doobypro.shared.generated.resources.faq_category_billing
import doobypro.shared.generated.resources.faq_category_delivery
import doobypro.shared.generated.resources.faq_category_getting_started
import doobypro.shared.generated.resources.faq_category_orders
import doobypro.shared.generated.resources.faq_category_other
import doobypro.shared.generated.resources.faq_category_technical
import org.jetbrains.compose.resources.StringResource

enum class FAQCategory(val label: StringResource) {
    GETTING_STARTED(Res.string.faq_category_getting_started),
    ORDERS(Res.string.faq_category_orders),
    DELIVERY(Res.string.faq_category_delivery),
    ACCOUNT(Res.string.faq_category_account),
    TECHNICAL(Res.string.faq_category_technical),
    BILLING(Res.string.faq_category_billing),
    OTHER(Res.string.faq_category_other)
}
