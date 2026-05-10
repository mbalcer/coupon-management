package pl.mbalcer.couponmanagement.application.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import pl.mbalcer.couponmanagement.domain.exception.CountryNotAllowedException
import pl.mbalcer.couponmanagement.domain.exception.CouponExhaustedException
import pl.mbalcer.couponmanagement.domain.exception.CouponNotFoundException
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.`in`.RedeemCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import pl.mbalcer.couponmanagement.domain.port.out.IpGeolocationPort

@Service
@Transactional
class RedeemCouponService(
    private val repository: CouponRepository,
    private val geolocationAdapter: IpGeolocationPort
) : RedeemCouponUseCase {
    override fun redeem(command: RedeemCouponUseCase.Command) {
        val code = CouponCode.of(command.code)
        val coupon = repository.findByCode(code) ?: throw CouponNotFoundException(code.value)

        val clientCountryCode = geolocationAdapter.getCountryCode(command.clientIp)
        if (coupon.countryCode.value != clientCountryCode) {
            throw CountryNotAllowedException(clientCountryCode, coupon.countryCode.value)
        }

        if (!repository.incrementUses(code)) {
            throw CouponExhaustedException(code.value)
        }
    }
}