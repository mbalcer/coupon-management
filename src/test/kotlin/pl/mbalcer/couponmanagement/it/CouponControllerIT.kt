package pl.mbalcer.couponmanagement.it

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import pl.mbalcer.couponmanagement.domain.port.out.IpGeolocationPort
import pl.mbalcer.couponmanagement.infrastructure.web.dto.CreateCouponRequest
import pl.mbalcer.couponmanagement.infrastructure.web.dto.RedeemCouponRequest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CouponControllerIT {

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:18-alpine")

        @DynamicPropertySource
        @JvmStatic
        fun props(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var geolocationPort: IpGeolocationPort

    var objectMapper = ObjectMapper()

    @Test
    fun `creates coupon and returns 201`() {
        createCoupon("SPRING", 10, "US")
            .andExpect {
                status { isCreated() }
                jsonPath("$.code") { value("SPRING") }
                jsonPath("$.maxUses") { value(10) }
                jsonPath("$.currentUses") { value(0) }
                jsonPath("$.countryCode") { value("US") }
            }
    }

    @Test
    fun `returns 409 when coupon already exists`() {
        createCoupon("XMAS26", 50, "GB").andExpect { status { isCreated() } }

        createCoupon("xmas26", 50, "GB")
            .andExpect {
                status { isConflict() }
                jsonPath("$.errorMessage") { value("Coupon already exists: XMAS26") }
            }
    }

    @Test
    fun `returns 400 when coupon data are invalid`() {
        createCoupon("", -1, "PLN").andExpect {
            status { isBadRequest() }
            jsonPath("$.errorMessage") {
                value(
                    Matchers.allOf(
                        Matchers.containsString("code: must not be blank"),
                        Matchers.containsString("maxUses: must be greater than or equal to 1"),
                        Matchers.containsString("countryCode: size must be between 2 and 2")
                    )
                )
            }
        }
    }

    @Test
    fun `redeems coupon successfully`() {
        whenever(geolocationPort.getCountryCode(any())).thenReturn("PL")
        createCoupon("WIOSNA", 5, "PL").andExpect { status { isCreated() } }

        redeemCoupon("WIOSNA", "XYZ123").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `returns 404 when coupon not found`() {
        whenever(geolocationPort.getCountryCode(any())).thenReturn("PL")
        redeemCoupon("NOTFOUND", "xyz").andExpect {
            status { isNotFound() }
            jsonPath("$.errorMessage") { value("Coupon not found: NOTFOUND") }
        }
    }

    @Test
    fun `returns 409 when coupon is exhausted`() {
        whenever(geolocationPort.getCountryCode(any())).thenReturn("FR")
        createCoupon("BF26", 3, "FR").andExpect { status { isCreated() } }

        redeemCoupon("BF26", "xyz1").andExpect { status { isOk() } }
        redeemCoupon("BF26", "xyz2").andExpect { status { isOk() } }
        redeemCoupon("BF26", "xyz3").andExpect { status { isOk() } }
        redeemCoupon("BF26", "user1").andExpect {
            status { isConflict() }
            jsonPath("$.errorMessage") { value("Coupon exhausted: BF26") }
        }
    }

    @Test
    fun `returns 403 when client country does not match coupon country`() {
        whenever(geolocationPort.getCountryCode(any())).thenReturn("DE")
        createCoupon("PL100", 100, "PL").andExpect { status { isCreated() } }

        redeemCoupon("PL100", "de-user-1").andExpect {
            status { isForbidden() }
            jsonPath("$.errorMessage") { value("Coupon cannot be redeemed in DE. This offer is restricted to: PL") }
        }
    }

    @Test
    fun `concurrent redemptions respect max uses`() {
        whenever(geolocationPort.getCountryCode(any())).thenReturn("PL")
        createCoupon("FIRST5", 5, "PL").andExpect { status { isCreated() } }

        val executor = Executors.newFixedThreadPool(20)
        val successCount = AtomicInteger(0)
        val latch = CountDownLatch(20)

        repeat(20) { i ->
            executor.submit {
                try {
                    if (redeemCoupon("FIRST5", "user-$i").andReturn().response.status == 200) {
                        successCount.incrementAndGet()
                    }
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await(10, TimeUnit.SECONDS)
        executor.shutdown()

        assertEquals(successCount.get(), 5)
    }

    @Test
    fun `returns 409 when coupon already used by user`() {
        whenever(geolocationPort.getCountryCode(any())).thenReturn("PL")
        createCoupon("PL123", 5, "PL").andExpect { status { isCreated() } }
        redeemCoupon("PL123", "user-111").andExpect { status { isOk() } }
        redeemCoupon("PL123", "user-111").andExpect {
            status { isConflict() }
            jsonPath("$.errorMessage") { value("User user-111 already used coupon PL123") }
        }
    }

    private fun createCoupon(code: String, maxUses: Int, countryCode: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/coupons") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(CreateCouponRequest(code, maxUses, countryCode))
        }
    }

    private fun redeemCoupon(code: String, userId: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/coupons/$code/redeem") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(RedeemCouponRequest(userId))
        }
    }

}