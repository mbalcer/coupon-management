package pl.mbalcer.couponmanagement.domain.exception

class CouponNotFoundException(code: String) : RuntimeException("Coupon not found: $code")