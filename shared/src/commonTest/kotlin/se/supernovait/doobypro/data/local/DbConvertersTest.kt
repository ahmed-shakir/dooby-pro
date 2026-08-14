package se.supernovait.doobypro.data.local

import se.supernovait.doobypro.domain.model.delivery.DeliveryMethod
import se.supernovait.doobypro.domain.model.delivery.DeliveryOption
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [DbConverters].
 */
class DbConvertersTest {

    @Test
    fun `DeliveryOption converters should work bidirectional`() {
        val option = DeliveryOption.EXPRESS
        val string = "EXPRESS"

        assertEquals(string, DbConverters.fromDeliveryOption(option))
        assertEquals(option, DbConverters.toDeliveryOption(string))
    }

    @Test
    fun `DeliveryOption converters should handle nulls`() {
        assertNull(DbConverters.fromDeliveryOption(null))
        assertNull(DbConverters.toDeliveryOption(null))
    }

    @Test
    fun `DeliveryMethod converters should work bidirectional`() {
        val method = DeliveryMethod.HOME_DELIVERY
        val string = "HOME_DELIVERY"

        assertEquals(string, DbConverters.fromDeliveryMethod(method))
        assertEquals(method, DbConverters.toDeliveryMethod(string))
    }

    @Test
    fun `DeliveryMethod converters should handle nulls`() {
        assertNull(DbConverters.fromDeliveryMethod(null))
        assertNull(DbConverters.toDeliveryMethod(null))
    }
}
