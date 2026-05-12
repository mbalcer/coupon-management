package pl.mbalcer.couponmanagement.infrastructure.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @Operation(summary = "Create a coupon")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Coupon created"),
        ApiResponse(responseCode = "400", description = "Invalid request data"),
        ApiResponse(responseCode = "409", description = "Coupon already exists"),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCoupon(@Valid @RequestBody request: CreateCouponRequest): CouponResponse {
        val result = createUseCase.create(request.toCommand())
        return CouponResponse.from(result)
    }

    @Operation(summary = "Redeem a coupon")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Coupon redeemed"),
        ApiResponse(responseCode = "403", description = "Country not allowed"),
        ApiResponse(responseCode = "404", description = "Coupon not found"),
        ApiResponse(responseCode = "409", description = "Coupon exhausted or already used"),
    )
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