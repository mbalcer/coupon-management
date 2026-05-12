package pl.mbalcer.couponmanagement.infrastructure.persistence.couponusage

import org.springframework.stereotype.Repository
import pl.mbalcer.couponmanagement.domain.port.out.CouponUsageRepository
import java.util.*

@Repository
class JpaCouponUsageRepository(private val repository: SpringCouponUsageRepo) : CouponUsageRepository {

    override fun existsByCouponIdAndUserId(couponId: UUID, userId: String): Boolean {
        return repository.existsByCouponIdAndUserId(couponId, userId)
    }

    override fun save(couponId: UUID, userId: String) {
        repository.save(CouponUsageEntity(couponId = couponId, userId = userId))
    }
}