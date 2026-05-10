package pl.mbalcer.couponmanagement.infrastracture.geolocation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.ClassPathResource
import pl.mbalcer.couponmanagement.domain.exception.GeoIpResolutionException
import pl.mbalcer.couponmanagement.infrastructure.geolocation.IpGeolocationAdapter

class IpGeolocationAdapterTest {

    private val adapter = IpGeolocationAdapter(ClassPathResource("GeoLite2-Country.mmdb"), "PL")

    @Test
    fun `returns local country for loopback IPv4`() {
        assertThat(adapter.getCountryCode("127.0.0.1")).isEqualTo("PL")
    }

    @Test
    fun `returns local country for loopback IPv6`() {
        assertThat(adapter.getCountryCode("::1")).isEqualTo("PL")
    }

    @Test
    fun `returns US for Google public DNS`() {
        assertThat(adapter.getCountryCode("8.8.8.8")).isEqualTo("US")
    }

    @Test
    fun `returns PL for NASK ip`() {
        assertThat(adapter.getCountryCode("192.195.72.0")).isEqualTo("PL")
    }

    @Test
    fun `throws GeoIpResolutionException for invalid IP`() {
        assertThrows<GeoIpResolutionException> {
            adapter.getCountryCode("not-an-ip")
        }
    }
}