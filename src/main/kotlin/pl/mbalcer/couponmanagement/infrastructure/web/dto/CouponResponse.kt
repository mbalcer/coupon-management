package pl.mbalcer.couponmanagement.infrastructure.web.dto

import pl.mbalcer.couponmanagement.domain.model.Coupon
import java.time.Instant
import java.util.*

data class CouponResponse(
    val id: UUID,
    val code: String,
    val countryCode: String,
    val currentUses: Int,
    val maxUses: Int,
    val createdAt: Instant
) {
    companion object {
        fun from(coupon: Coupon) = CouponResponse(
            coupon.id!!,
            coupon.code.value,
            coupon.countryCode.value,
            coupon.currentUses,
            coupon.maxUses,
            coupon.createdAt
        )
    }
}