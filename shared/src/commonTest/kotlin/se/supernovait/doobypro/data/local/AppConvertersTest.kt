package se.supernovait.doobypro.data.local

import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [AppConverters].
 */
class AppConvertersTest {

    @Test
    fun `DeliveryOption converters should work bidirectional`() {
        val option = DeliveryOption.EXPRESS
        val string = "EXPRESS"

        assertEquals(string, AppConverters.fromDeliveryOption(option))
        assertEquals(option, AppConverters.toDeliveryOption(string))
    }

    @Test
    fun `DeliveryOption converters should handle nulls`() {
        assertNull(AppConverters.fromDeliveryOption(null))
        assertNull(AppConverters.toDeliveryOption(null))
    }

    @Test
    fun `DeliveryMethod converters should work bidirectional`() {
        val method = DeliveryMethod.HOME_DELIVERY
        val string = "HOME_DELIVERY"

        assertEquals(string, AppConverters.fromDeliveryMethod(method))
        assertEquals(method, AppConverters.toDeliveryMethod(string))
    }

    @Test
    fun `DeliveryMethod converters should handle nulls`() {
        assertNull(AppConverters.fromDeliveryMethod(null))
        assertNull(AppConverters.toDeliveryMethod(null))
    }
}
