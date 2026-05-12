package pl.mbalcer.couponmanagement.domain.exception

class CouponAlreadyUsedException(userId: String, code: String) :
    RuntimeException("User $userId already used coupon $code")