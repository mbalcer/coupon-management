package pl.mbalcer.couponmanagement.infrastructure.geolocation

import com.maxmind.geoip2.DatabaseReader
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

    private val reader = DatabaseReader.Builder(mmdb.inputStream).build()

    companion object {
        private val LOCAL_IP_ADDRESSES = setOf("127.0.0.1", "::1", "0:0:0:0:0:0:0:1")
    }

    override fun getCountryCode(ipAddress: String): String = when {
        ipAddress in LOCAL_IP_ADDRESSES -> localCountry
        else -> try {
            reader.country(InetAddress.getByName(ipAddress)).country().isoCode()
        } catch (e: Exception) {
            throw GeoIpResolutionException()
        }
    }
}

