package pl.mbalcer.couponmanagement.domain.port.`in`

interface RedeemCouponUseCase {
    data class Command(val code: String, val userId: String, val clientIp: String)

    fun redeem(command: Command)
}