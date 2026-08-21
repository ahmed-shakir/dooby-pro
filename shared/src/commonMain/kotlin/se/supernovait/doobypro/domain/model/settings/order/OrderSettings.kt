package se.supernovait.doobypro.domain.model.settings.order

import kotlinx.serialization.Serializable
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption

@Serializable
data class OrderSettings(
    val defaultServiceId: String? = null,
    val defaultDeliveryOption: DeliveryOption = DeliveryOption.STANDARD,
    val defaultDeliveryMethod: DeliveryMethod = DeliveryMethod.IN_STORE_PICKUP,
    val defaultHandlingTimeDays: Int = 2,
    val autoPrintReceipts: Boolean = true
)
