package pl.mbalcer.couponmanagement.domain.exception

class CountryNotAllowedException(clientCountry: String, couponCountry: String) :
    RuntimeException("Coupon cannot be redeemed in $clientCountry. This offer is restricted to: $couponCountry")