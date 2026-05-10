package pl.mbalcer.couponmanagement.infrastructure.web.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import pl.mbalcer.couponmanagement.domain.port.`in`.CreateCouponUseCase

data class CreateCouponRequest(
    @field:NotBlank val code: String,
    @field:Min(1) val maxUses: Int,
    @field:NotBlank @field:Size(min = 2, max = 2) val countryCode: String
) {
    fun toCommand() = CreateCouponUseCase.Command(code, maxUses, countryCode)
}