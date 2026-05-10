package pl.mbalcer.couponmanagement.infrastructure.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.mbalcer.couponmanagement.domain.port.`in`.CreateCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.`in`.RedeemCouponUseCase
import pl.mbalcer.couponmanagement.infrastructure.web.dto.CouponResponse
import pl.mbalcer.couponmanagement.infrastructure.web.dto.CreateCouponRequest
import pl.mbalcer.couponmanagement.infrastructure.web.dto.RedeemCouponRequest

@RestController
@RequestMapping("/api/v1/coupons")
class CouponController(
    private val createUseCase: CreateCouponUseCase,
    private val redeemCouponUseCase: RedeemCouponUseCase
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCoupon(@Valid @RequestBody request: CreateCouponRequest): CouponResponse {
        val result = createUseCase.create(request.toCommand())
        return CouponResponse.from(result)
    }

    @PostMapping("/{code}/redeem")
    @ResponseStatus(HttpStatus.OK)
    fun redeem(
        @PathVariable code: String,
        @Valid @RequestBody request: RedeemCouponRequest,
        httpRequest: HttpServletRequest
    ) {
        val clientIp = httpRequest.getHeader("X-Forwarded-For")?.split(",")?.first()?.trim() ?: httpRequest.remoteAddr
        redeemCouponUseCase.redeem(RedeemCouponUseCase.Command(code, request.userId, clientIp))
    }
}