package pl.mbalcer.couponmanagement.domain.exception

class CouponExhaustedException(code: String) : RuntimeException("Coupon exhausted: $code")