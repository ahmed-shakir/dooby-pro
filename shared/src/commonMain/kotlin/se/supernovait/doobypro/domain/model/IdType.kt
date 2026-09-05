package se.supernovait.doobypro.domain.model

/**
 * Defines the types of entities and their corresponding ID prefixes.
 *
 * @property prefix The 2-character prefix used for generating IDs.
 */
enum class IdType(val prefix: String) {
    AGREEMENT("DA"),
    COMPANY("DC"),
    LICENSE("DL"),
    ORDER("DO"),
    SERVICE("DS"),
    STORAGE_LOCATION("DP"),
    USER("DU")
}
