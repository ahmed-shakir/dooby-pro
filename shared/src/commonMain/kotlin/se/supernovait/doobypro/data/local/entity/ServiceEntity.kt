package se.supernovait.doobypro.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import se.supernovait.app.core.data.persistence.entity.AmountEntity
import se.supernovait.app.core.domain.id.SupernovaIdGenerator
import se.supernovait.doobypro.domain.model.IdType

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey
    val id: String = SupernovaIdGenerator.generateId(IdType.SERVICE.prefix),
    val title: String,
    val description: String,
    @Embedded
    val price: AmountEntity
)
