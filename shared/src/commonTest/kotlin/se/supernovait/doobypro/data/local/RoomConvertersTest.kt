package se.supernovait.doobypro.data.local

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import se.supernovait.app.core.data.persistence.RoomConverters
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [RoomConverters] from the app-core library.
 */
class RoomConvertersTest {

    private val converters = RoomConverters()

    @Test
    fun `LocalDate converters should work bidirectional`() {
        val date = LocalDate(2026, 8, 14)
        val string = "2026-08-14"

        assertEquals(string, converters.fromLocalDate(date))
        assertEquals(date, converters.toLocalDate(string))
    }

    @Test
    fun `LocalDate converters should handle nulls`() {
        assertNull(converters.fromLocalDate(null))
        assertNull(converters.toLocalDate(null))
    }

    @Test
    fun `LocalDateTime converters should work bidirectional`() {
        val dateTime = LocalDateTime(2026, 8, 14, 20, 43, 12)
        val string = "2026-08-14T20:43:12"

        assertEquals(string, converters.fromLocalDateTime(dateTime))
        assertEquals(dateTime, converters.toLocalDateTime(string))
    }

    @Test
    fun `LocalDateTime converters should handle nulls`() {
        assertNull(converters.fromLocalDateTime(null))
        assertNull(converters.toLocalDateTime(null))
    }
}
