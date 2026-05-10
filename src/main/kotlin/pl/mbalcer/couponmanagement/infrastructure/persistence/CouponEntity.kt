package pl.mbalcer.couponmanagement.infrastructure.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "coupons")
class CouponEntity(
    @Id val id: UUID = UUID.randomUUID(),
    val code: String,
    val countryCode: String,
    val maxUses: Int,
    var currentUses: Int = 0,
    val createdAt: Instant
)