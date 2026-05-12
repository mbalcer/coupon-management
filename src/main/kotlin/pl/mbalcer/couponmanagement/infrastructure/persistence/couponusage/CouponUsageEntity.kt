package pl.mbalcer.couponmanagement.infrastructure.persistence.couponusage

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "coupon_usages")
class CouponUsageEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val couponId: UUID,
    val userId: String,
    val usedAt: Instant = Instant.now()
)