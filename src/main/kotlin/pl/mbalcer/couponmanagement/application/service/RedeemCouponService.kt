package pl.mbalcer.couponmanagement.application.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import pl.mbalcer.couponmanagement.domain.exception.CountryNotAllowedException
import pl.mbalcer.couponmanagement.domain.exception.CouponAlreadyUsedException
import pl.mbalcer.couponmanagement.domain.exception.CouponExhaustedException
import pl.mbalcer.couponmanagement.domain.exception.CouponNotFoundException
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.`in`.RedeemCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import pl.mbalcer.couponmanagement.domain.port.out.CouponUsageRepository
import pl.mbalcer.couponmanagement.domain.port.out.IpGeolocationPort

@Service
@Transactional
class RedeemCouponService(
    private val couponRepo: CouponRepository,
    private val geolocationAdapter: IpGeolocationPort,
    private val couponUsageRepo: CouponUsageRepository
) : RedeemCouponUseCase {
    override fun redeem(command: RedeemCouponUseCase.Command) {
        val code = CouponCode.of(command.code)
        val coupon = couponRepo.findByCode(code) ?: throw CouponNotFoundException(code.value)

        val clientCountryCode = geolocationAdapter.getCountryCode(command.clientIp)
        if (coupon.countryCode.value != clientCountryCode) {
            throw CountryNotAllowedException(clientCountryCode, coupon.countryCode.value)
        }

        if (couponUsageRepo.existsByCouponIdAndUserId(coupon.id!!, command.userId)) {
            throw CouponAlreadyUsedException(command.userId, code.value)
        }

        if (!couponRepo.incrementUses(code)) {
            throw CouponExhaustedException(code.value)
        }
        couponUsageRepo.save(coupon.id, command.userId)
    }
}