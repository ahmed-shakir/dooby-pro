package se.supernovait.doobypro.domain.model

enum class DoobyIdType(val prefix: String) {
    AGREEMENT("DA"),
    COMPANY("DC"),
    LICENSE("DL"),
    ORDER("DO"),
    SERVICE("DS"),
    USER("DU")
}
