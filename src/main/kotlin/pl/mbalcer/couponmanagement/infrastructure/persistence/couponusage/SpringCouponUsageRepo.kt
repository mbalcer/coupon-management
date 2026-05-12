package pl.mbalcer.couponmanagement.infrastructure.persistence.couponusage

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SpringCouponUsageRepo : JpaRepository<CouponUsageEntity, UUID> {
    fun existsByCouponIdAndUserId(couponId: UUID, userId: String): Boolean
}