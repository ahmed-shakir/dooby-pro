package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.app.core.domain.model.billing.BillingFrequency
import se.supernovait.doobypro.domain.model.IdType
import se.supernovait.doobypro.domain.model.agreement.AgreementStatus
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Database entity representing an equipment lease agreement for Room persistence.
 *
 * @property id The unique identifier for the agreement, defaults to a generated ID.
 * @property accountId The identifier of the account that owns the agreement.
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
 * @property createdAt The timestamp when the agreement record was created.
 * @property updatedAt The timestamp when the agreement record was last updated.
 */
@Entity(
    tableName = "agreements",
    indices = [Index(value = ["accountId"])]
)
data class AgreementEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(IdType.AGREEMENT.prefix),
    val accountId: String,
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
    val cancellationDate: LocalDate?,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
