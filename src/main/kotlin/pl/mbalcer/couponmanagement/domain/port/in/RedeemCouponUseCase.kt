package pl.mbalcer.couponmanagement.domain.port.`in`

import pl.mbalcer.couponmanagement.domain.model.CouponCode

interface RedeemCouponUseCase {
    data class Command(val code: CouponCode, val userId: String, val clientIp: String)
    fun redeem(command: Command)
}