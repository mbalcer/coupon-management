package pl.mbalcer.couponmanagement.domain.model

import java.time.Instant
import java.util.*

data class Coupon(
    val id: UUID?,
    val code: CouponCode,
    val countryCode: CountryCode,
    val currentUses: Int,
    val maxUses: Int,
    val createdAt: Instant
)