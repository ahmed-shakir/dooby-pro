package se.supernovait.doobypro.data.local.mapper

import se.supernovait.doobypro.data.local.entity.embedded.AmountEntity
import se.supernovait.doobypro.domain.model.Amount

fun AmountEntity.mapToModel(): Amount {
    return Amount(
        value = value,
        currency = currency
    )
}

fun Amount.mapToEntity(): AmountEntity {
    return AmountEntity(
        value = value,
        currency = currency
    )
}
