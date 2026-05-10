package pl.mbalcer.couponmanagement.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface SpringCouponRepo : JpaRepository<CouponEntity, UUID> {
    fun findByCode(code: String): CouponEntity?

    @Modifying
    @Query(
        "UPDATE CouponEntity c SET c.currentUses = c.currentUses + 1 " +
                "WHERE c.code = :code AND c.currentUses < c.maxUses"
    )
    fun incrementUses(@Param("code") code: String): Int
}