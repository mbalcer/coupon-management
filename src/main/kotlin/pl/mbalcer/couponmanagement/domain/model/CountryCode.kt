package pl.mbalcer.couponmanagement.domain.model

@JvmInline
value class CountryCode(val value: String) {
    init {
        require(value.matches(Regex("[A-Z]{2}")))
    }
}
