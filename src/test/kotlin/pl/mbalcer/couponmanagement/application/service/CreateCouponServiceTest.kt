package pl.mbalcer.couponmanagement.application.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.mbalcer.couponmanagement.domain.exception.CouponAlreadyExistsException
import pl.mbalcer.couponmanagement.domain.model.CountryCode
import pl.mbalcer.couponmanagement.domain.model.Coupon
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.`in`.CreateCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import java.time.Instant

class CreateCouponServiceTest {
    private val repository = mock<CouponRepository>()
    private val service = CreateCouponService(repository)

    @Test
    fun `creates coupon and saves through repository`() {
        whenever(repository.findByCode(any())).thenReturn(null)
        whenever(repository.save(any())).thenAnswer { it.arguments[0] as Coupon }

        service.create(CreateCouponUseCase.Command("XMAS26", 10, "US"))

        val captor = argumentCaptor<Coupon>()
        verify(repository).save(captor.capture())
        with(captor.firstValue) {
            assertEquals("XMAS26", code.value)
            assertEquals(10, maxUses)
            assertEquals(0, currentUses)
            assertEquals("US", countryCode.value)
        }
    }

    @Test
    fun `throws when coupon with same code already exists`() {
        whenever(repository.findByCode(CouponCode("WIOSNA"))).thenReturn(existingCoupon())

        assertThrows<CouponAlreadyExistsException> {
            service.create(CreateCouponUseCase.Command("WIOSNA", 10, "PL"))
        }
        verify(repository, never()).save(any())
    }

    @Test
    fun `throws when coupon code already exists case insensitive`() {
        whenever(repository.findByCode(CouponCode("WIOSNA"))).thenReturn(existingCoupon())

        assertThrows<CouponAlreadyExistsException> {
            service.create(CreateCouponUseCase.Command(" wiosna", 10, "PL"))
        }
        verify(repository, never()).save(any())
    }

    private fun existingCoupon() = Coupon(null, CouponCode("WIOSNA"), CountryCode("PL"), 0, 100, Instant.now())
}