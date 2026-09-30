package se.supernovait.doobypro.domain.model.support.faq

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.faq_category_orders
import doobypro.shared.generated.resources.faq_category_printing
import doobypro.shared.generated.resources.faq_category_services
import doobypro.shared.generated.resources.faq_category_storage
import org.jetbrains.compose.resources.StringResource
import se.supernovait.app.core.domain.model.faq.FAQCategory

enum class DoobyFAQCategory(override val label: StringResource) : FAQCategory {
    ORDERS(Res.string.faq_category_orders),
    PRINTING(Res.string.faq_category_printing),
    SERVICES(Res.string.faq_category_services),
    STORAGE(Res.string.faq_category_storage);

    override val id: String
        get() = name
}
