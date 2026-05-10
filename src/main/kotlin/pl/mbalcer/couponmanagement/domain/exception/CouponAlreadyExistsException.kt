package pl.mbalcer.couponmanagement.domain.exception

class CouponAlreadyExistsException(code: String) : RuntimeException("Coupon already exists: $code")