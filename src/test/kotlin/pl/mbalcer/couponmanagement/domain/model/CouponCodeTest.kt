package pl.mbalcer.couponmanagement.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CouponCodeTest {
    @Test
    fun `normalizes to uppercase`() = assertEquals("WIOSNA", CouponCode.of("wiosna").value)

    @Test
    fun `trims whitespace`() = assertEquals("WIOSNA", CouponCode.of(" WIOSNA ").value)

    @Test
    fun `throws on blank`() {
        assertThrows<IllegalArgumentException> { CouponCode.of("") }
    }
}