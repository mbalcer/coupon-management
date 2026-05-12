package pl.mbalcer.couponmanagement.application.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import pl.mbalcer.couponmanagement.domain.exception.CountryNotAllowedException
import pl.mbalcer.couponmanagement.domain.exception.CouponAlreadyUsedException
import pl.mbalcer.couponmanagement.domain.exception.CouponExhaustedException
import pl.mbalcer.couponmanagement.domain.exception.CouponNotFoundException
import pl.mbalcer.couponmanagement.domain.model.CountryCode
import pl.mbalcer.couponmanagement.domain.model.Coupon
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.`in`.RedeemCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import pl.mbalcer.couponmanagement.domain.port.out.CouponUsageRepository
import pl.mbalcer.couponmanagement.domain.port.out.IpGeolocationPort
import java.time.Instant
import java.util.UUID

class RedeemCouponServiceTest {
    private val couponRepo = mock<CouponRepository>()
    private val geolocationAdapter = mock<IpGeolocationPort>()
    private val couponUsageRepo = mock<CouponUsageRepository>()
    private val service = RedeemCouponService(couponRepo, geolocationAdapter, couponUsageRepo)

    @Test
    fun `redeems coupon successfully`() {
        val coupon = Coupon(UUID.randomUUID(), CouponCode("BLACKFRIDAY"), CountryCode("PL"), 0, 100, Instant.now())

        whenever(couponRepo.findByCode(any())).thenReturn(coupon)
        whenever(couponRepo.incrementUses(any())).thenReturn(true)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")
        whenever(couponUsageRepo.existsByCouponIdAndUserId(any(), any())).thenReturn(false)

        service.redeem(RedeemCouponUseCase.Command("BLACKFRIDAY", "user1", "192.168.1.0"))

        verify(couponRepo).incrementUses(CouponCode.of("BLACKFRIDAY"))
        verify(couponUsageRepo).save(coupon.id!!, "user1")
    }

    @Test
    fun `throws when coupon not found`() {
        whenever(couponRepo.findByCode(any())).thenReturn(null)
        assertThrows<CouponNotFoundException> {
            service.redeem(RedeemCouponUseCase.Command("NULL", "user1", "192.168.1.0"))
        }
    }

    @Test
    fun `throws when coupon exhausted`() {
        val coupon = Coupon(UUID.randomUUID(), CouponCode("BLACKFRIDAY"), CountryCode("PL"), 100, 100, Instant.now())

        whenever(couponRepo.findByCode(any())).thenReturn(coupon)
        whenever(couponRepo.incrementUses(any())).thenReturn(false)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")

        assertThrows<CouponExhaustedException> {
            service.redeem(RedeemCouponUseCase.Command("BLACKFRIDAY", "user1", "192.168.1.0"))
        }
    }

    @Test
    fun `throws when client country does not match coupon country`() {
        val coupon = Coupon(UUID.randomUUID(), CouponCode("BLACKFRIDAY"), CountryCode("US"), 0, 100, Instant.now())

        whenever(couponRepo.findByCode(any())).thenReturn(coupon)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")

        assertThrows<CountryNotAllowedException> {
            service.redeem(RedeemCouponUseCase.Command("BLACKFRIDAY", "user1", "127.0.0.1"))
        }
    }

    @Test
    fun `throws when coupon already used by user`() {
        val coupon = Coupon(UUID.randomUUID(), CouponCode("XMAS"), CountryCode("PL"), 0, 100, Instant.now())

        whenever(couponRepo.findByCode(any())).thenReturn(coupon)
        whenever(geolocationAdapter.getCountryCode(any())).thenReturn("PL")
        whenever(couponUsageRepo.existsByCouponIdAndUserId(any(), any())).thenReturn(true)

        assertThrows<CouponAlreadyUsedException> {
            service.redeem(RedeemCouponUseCase.Command("XMAS", "user1", "127.0.0.1"))
        }
    }
}