package pl.mbalcer.couponmanagement.domain.port.`in`

import pl.mbalcer.couponmanagement.domain.model.CountryCode
import pl.mbalcer.couponmanagement.domain.model.Coupon

interface CreateCouponUseCase {
    data class Command(val code: String, val maxUses: Int, val countryCode: CountryCode)
    fun create(command: Command): Coupon
}