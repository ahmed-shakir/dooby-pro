package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Database entity representing a service for Room persistence.
 *
 * @property id The unique identifier for the service, defaults to a generated ID.
 * @property title The service title.
 * @property description The service description.
 * @property price The service price, embedded in the table.
 * @property createdAt The timestamp when the service record was created.
 * @property updatedAt The timestamp when the service record was last updated.
 */
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(IdType.SERVICE.prefix),
    val title: String,
    val description: String,
    @Embedded
    val price: AmountEntity,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)
