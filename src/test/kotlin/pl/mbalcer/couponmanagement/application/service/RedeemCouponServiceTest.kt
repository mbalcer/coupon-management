package pl.mbalcer.couponmanagement.application.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import pl.mbalcer.couponmanagement.domain.exception.CountryNotAllowedException
import pl.mbalcer.couponmanagement.domain.exception.CouponExhaustedException
import pl.mbalcer.couponmanagement.domain.exception.CouponNotFoundException
import pl.mbalcer.couponmanagement.domain.model.CountryCode
import pl.mbalcer.couponmanagement.domain.model.Coupon
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.`in`.RedeemCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import pl.mbalcer.couponmanagement.domain.port.out.IpGeolocationPort
import java.time.Instant

class RedeemCouponServiceTest {
    private val repository = mock<CouponRepository>()
    private val geolocationAdapter = mock<IpGeolocationPort>()
    private val service = RedeemCouponService(repository, geolocationAdapter)

    @Test
    fun `redeems coupon successfully`() {
        val coupon = Coupon(null, CouponCode("BLACKFRIDAY"), CountryCode("PL"), 0, 100, Instant.now())

        whenever(repository.findByCode(any())).thenReturn(coupon)
        whenever(repository.incrementUses(any())).thenReturn(true)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")

        service.redeem(RedeemCouponUseCase.Command("BLACKFRIDAY", "user1", "192.168.1.0"))

        verify(repository).incrementUses(CouponCode.of("BLACKFRIDAY"))
    }

    @Test
    fun `throws when coupon not found`() {
        whenever(repository.findByCode(any())).thenReturn(null)
        assertThrows<CouponNotFoundException> {
            service.redeem(RedeemCouponUseCase.Command("NULL", "user1", "192.168.1.0"))
        }
    }

    @Test
    fun `throws when coupon exhausted`() {
        val coupon = Coupon(null, CouponCode("BLACKFRIDAY"), CountryCode("PL"), 100, 100, Instant.now())

        whenever(repository.findByCode(any())).thenReturn(coupon)
        whenever(repository.incrementUses(any())).thenReturn(false)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")

        assertThrows<CouponExhaustedException> {
            service.redeem(RedeemCouponUseCase.Command("BLACKFRIDAY", "user1", "192.168.1.0"))
        }
    }

    @Test
    fun `throws when client country does not match coupon country`() {
        val coupon = Coupon(null, CouponCode("BLACKFRIDAY"), CountryCode("US"), 0, 100, Instant.now())

        whenever(repository.findByCode(any())).thenReturn(coupon)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")

        assertThrows<CountryNotAllowedException> {
            service.redeem(RedeemCouponUseCase.Command("BLACKFRIDAY", "user1", "127.0.0.1"))
        }
    }
}