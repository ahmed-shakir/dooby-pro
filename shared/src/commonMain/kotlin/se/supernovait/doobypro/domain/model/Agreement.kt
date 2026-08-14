package se.supernovait.doobypro.domain.model

import kotlinx.datetime.LocalDate
import se.supernovait.app.core.domain.model.billing.Amount
import se.supernovait.app.core.domain.model.billing.BillingFrequency

/**
 * Domain model representing an equipment lease agreement.
 *
 * @property id The unique identifier for the agreement. Null for unsaved agreements.
 * @property status The current status of the agreement (Active, Cancelled).
 * @property equipmentId The serial number or unique identifier of the equipment.
 * @property equipmentModel The model name or number of the equipment.
 * @property title A short descriptive title for the agreement.
 * @property description Detailed terms or description of the lease.
 * @property billingFrequency How often the fee is billed (e.g., Monthly, Annual).
 * @property fee The recurring lease fee.
 * @property deposit The security deposit amount required for the lease.
 * @property issueDate The date when the agreement was issued.
 * @property cancellationDate The date when the agreement was cancelled, if applicable.
 */
data class Agreement(
    val id: String? = null,
    val status: AgreementStatus,
    val equipmentId: String,
    val equipmentModel: String,
    val title: String,
    val description: String,
    val billingFrequency: BillingFrequency,
    val fee: Amount,
    val deposit: Amount,
    val issueDate: LocalDate,
    val cancellationDate: LocalDate? = null
)
