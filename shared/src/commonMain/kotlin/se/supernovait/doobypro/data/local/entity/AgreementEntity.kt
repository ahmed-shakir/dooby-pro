package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.doobypro.domain.model.AgreementStatus
import se.supernovait.doobypro.domain.model.DoobyIdType

/**
 * Database entity representing an equipment lease agreement for Room persistence.
 *
 * @property id The unique identifier for the agreement, defaults to a generated ID.
 * @property status The current status of the agreement.
 * @property equipmentId The serial number of the equipment.
 * @property equipmentModel The model name of the equipment.
 * @property title The agreement title.
 * @property description The agreement description.
 * @property billingFrequency The billing frequency.
 * @property fee The recurring fee, embedded in the table.
 * @property deposit The security deposit, embedded in the table.
 * @property issueDate The date of issue.
 * @property cancellationDate The date of cancellation, if any.
 */
@Entity(tableName = "agreements")
data class AgreementEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(DoobyIdType.AGREEMENT.prefix),
    val status: AgreementStatus,
    val equipmentId: String,
    val equipmentModel: String,
    val title: String,
    val description: String,
    val billingFrequency: BillingFrequency,
    @Embedded(prefix = "fee_")
    val fee: AmountEntity,
    @Embedded(prefix = "deposit_")
    val deposit: AmountEntity,
    val issueDate: LocalDate,
    val cancellationDate: LocalDate?
)
