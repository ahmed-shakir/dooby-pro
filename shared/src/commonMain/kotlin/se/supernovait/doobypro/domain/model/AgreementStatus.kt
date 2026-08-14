package se.supernovait.doobypro.domain.model

/**
 * Defines the possible states of an equipment lease agreement.
 */
enum class AgreementStatus {
    /**
     * The agreement is currently active and valid.
     */
    ACTIVE,

    /**
     * The agreement has been cancelled or terminated.
     */
    CANCELLED
}
