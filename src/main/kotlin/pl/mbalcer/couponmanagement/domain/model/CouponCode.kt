package pl.mbalcer.couponmanagement.domain.model

@JvmInline
value class CouponCode(val value: String) {
    init {
        require(value.isNotBlank()) {
            "Coupon code cannot be blank"
        }
    }

    companion object {
        fun of(value: String) = CouponCode(value.uppercase().trim())
    }
}
