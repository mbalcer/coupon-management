package pl.mbalcer.couponmanagement.domain.port.out

interface IpGeolocationPort {
    fun getCountryCode(ipAddress: String): String
}