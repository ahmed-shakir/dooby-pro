package se.supernovait.doobypro.domain.model

data class Service(
    val id: String,
    val title: String,
    val description: String,
    val price: Amount
)
