package pl.mbalcer.couponmanagement.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CountryCodeTest {
    @Test
    fun `accepts valid country code`() = assertEquals("PL", CountryCode("PL").value)

    @Test
    fun `accepts another valid country code`() = assertEquals("US", CountryCode("US").value)

    @Test
    fun `throws on lowercase`() {
        assertThrows<IllegalArgumentException> { CountryCode("pl") }
    }

    @Test
    fun `throws on three letter code`() {
        assertThrows<IllegalArgumentException> { CountryCode("POL") }
    }

    @Test
    fun `throws on single letter`() {
        assertThrows<IllegalArgumentException> { CountryCode("P") }
    }

    @Test
    fun `throws on blank`() {
        assertThrows<IllegalArgumentException> { CountryCode("") }
    }

    @Test
    fun `throws on code with digits`() {
        assertThrows<IllegalArgumentException> { CountryCode("P1") }
    }
}