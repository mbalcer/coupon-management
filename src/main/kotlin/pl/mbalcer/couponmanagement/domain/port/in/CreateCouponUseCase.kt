package pl.mbalcer.couponmanagement.domain.port.`in`

import pl.mbalcer.couponmanagement.domain.model.Coupon

interface CreateCouponUseCase {
    data class Command(val code: String, val maxUses: Int, val countryCode: String)

    fun create(command: Command): Coupon
}