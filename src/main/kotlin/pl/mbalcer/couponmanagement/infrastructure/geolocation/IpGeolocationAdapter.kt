package pl.mbalcer.couponmanagement.infrastructure.geolocation

import com.maxmind.geoip2.DatabaseReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import pl.mbalcer.couponmanagement.domain.exception.GeoIpResolutionException
import pl.mbalcer.couponmanagement.domain.port.out.IpGeolocationPort
import java.net.InetAddress

@Component
class IpGeolocationAdapter(
    @Value("classpath:GeoLite2-Country.mmdb") mmdb: Resource,
    @Value("\${geolocation.local-country:XX}") private val localCountry: String
) : IpGeolocationPort {

    private val logger: Logger = LoggerFactory.getLogger(IpGeolocationPort::class.java)
    private val reader = DatabaseReader.Builder(mmdb.inputStream).build()

    override fun getCountryCode(ipAddress: String): String {
        return try {
            val addr = InetAddress.getByName(ipAddress)
            if (addr.isLoopbackAddress || addr.isSiteLocalAddress || addr.isLinkLocalAddress) {
                return localCountry
            }

            reader.country(InetAddress.getByName(ipAddress)).country().isoCode()
        } catch (e: Exception) {
            logger.error("Failed to resolve country for IP: $ipAddress", e)
            throw GeoIpResolutionException()
        }
    }
}

