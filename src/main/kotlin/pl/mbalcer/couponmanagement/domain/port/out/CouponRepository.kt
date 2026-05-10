package pl.mbalcer.couponmanagement.domain.port.out

import pl.mbalcer.couponmanagement.domain.model.Coupon
import pl.mbalcer.couponmanagement.domain.model.CouponCode

interface CouponRepository {
    fun save(coupon: Coupon): Coupon
    fun findByCode(code: CouponCode): Coupon?
    fun incrementUses(code: CouponCode): Boolean
}