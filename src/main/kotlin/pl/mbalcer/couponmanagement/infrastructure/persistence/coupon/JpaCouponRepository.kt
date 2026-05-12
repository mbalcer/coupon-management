package pl.mbalcer.couponmanagement.infrastructure.persistence.coupon

import org.springframework.stereotype.Repository
import pl.mbalcer.couponmanagement.domain.model.CountryCode
import pl.mbalcer.couponmanagement.domain.model.Coupon
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import java.util.*

@Repository
class JpaCouponRepository(private val repository: SpringCouponRepo) : CouponRepository {
    override fun save(coupon: Coupon): Coupon {
        return repository.save(coupon.toEntity()).toDomain()
    }

    override fun findByCode(code: CouponCode): Coupon? {
        return repository.findByCode(code.value)?.toDomain()
    }

    override fun incrementUses(code: CouponCode): Boolean {
        return repository.incrementUses(code.value) > 0
    }

    private fun CouponEntity.toDomain(): Coupon {
        return Coupon(id, CouponCode(code), CountryCode(countryCode), currentUses, maxUses, createdAt)
    }

    private fun Coupon.toEntity(): CouponEntity {
        return CouponEntity(id ?: UUID.randomUUID(), code.value, countryCode.value, currentUses, maxUses, createdAt)
    }
}