package se.supernovait.doobypro.domain.model.support.faq

data class FAQ(
    val id: String,
    val category: FAQCategory,
    val question: String,
    val answer: String
)
