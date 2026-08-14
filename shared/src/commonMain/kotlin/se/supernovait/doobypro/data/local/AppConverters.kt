package se.supernovait.doobypro.data.local

import androidx.room.TypeConverter
import se.supernovait.doobypro.domain.model.AgreementStatus
import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption

/**
 * Room type converters for capturing custom data types in the database.
 *
 * This class provides methods to convert enums related to delivery
 * and agreements to and from their string representations for database storage.
 */
object AppConverters {

    /**
     * Converts a string to an [AgreementStatus].
     */
    @TypeConverter
    fun toAgreementStatus(value: String?): AgreementStatus? = value?.let { AgreementStatus.valueOf(it) }

    /**
     * Converts an [AgreementStatus] to a string.
     */
    @TypeConverter
    fun fromAgreementStatus(status: AgreementStatus?): String? = status?.name

    /**
     * Converts a string to a [DeliveryOption].
     */
    @TypeConverter
    fun toDeliveryOption(value: String?): DeliveryOption? = value?.let { DeliveryOption.valueOf(it) }

    /**
     * Converts a [DeliveryOption] to a string.
     */
    @TypeConverter
    fun fromDeliveryOption(option: DeliveryOption?): String? = option?.name

    /**
     * Converts a string to a [DeliveryMethod].
     */
    @TypeConverter
    fun toDeliveryMethod(value: String?): DeliveryMethod? = value?.let { DeliveryMethod.valueOf(it) }

    /**
     * Converts a [DeliveryMethod] to a string.
     */
    @TypeConverter
    fun fromDeliveryMethod(method: DeliveryMethod?): String? = method?.name
}
