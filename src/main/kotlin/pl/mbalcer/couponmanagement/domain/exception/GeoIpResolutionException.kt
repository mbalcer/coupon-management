package pl.mbalcer.couponmanagement.domain.exception

class GeoIpResolutionException() : RuntimeException("Unable to determine your location. Coupon cannot be applied")