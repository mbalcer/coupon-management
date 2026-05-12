package pl.mbalcer.couponmanagement.domain.port.out

import java.util.*

interface CouponUsageRepository {
    fun existsByCouponIdAndUserId(couponId: UUID, userId: String): Boolean
    fun save(couponId: UUID, userId: String)
}