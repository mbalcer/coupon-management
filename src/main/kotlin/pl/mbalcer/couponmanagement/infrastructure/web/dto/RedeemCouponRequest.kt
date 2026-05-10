package pl.mbalcer.couponmanagement.infrastructure.web.dto

import jakarta.validation.constraints.NotBlank

data class RedeemCouponRequest(@field:NotBlank val userId: String)
