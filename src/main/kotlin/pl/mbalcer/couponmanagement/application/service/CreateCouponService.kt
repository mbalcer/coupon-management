package pl.mbalcer.couponmanagement.application.service

import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.mbalcer.couponmanagement.domain.exception.CouponAlreadyExistsException
import pl.mbalcer.couponmanagement.domain.model.CountryCode
import pl.mbalcer.couponmanagement.domain.model.Coupon
import pl.mbalcer.couponmanagement.domain.model.CouponCode
import pl.mbalcer.couponmanagement.domain.port.`in`.CreateCouponUseCase
import pl.mbalcer.couponmanagement.domain.port.out.CouponRepository
import java.time.Instant

@Service
@Transactional
class CreateCouponService(private val repository: CouponRepository): CreateCouponUseCase {

    private val logger: Logger = LoggerFactory.getLogger(CreateCouponService::class.java)

    override fun create(command: CreateCouponUseCase.Command): Coupon {
        val code = CouponCode.of(command.code)
        if (repository.findByCode(code) != null) {
            logger.warn("Coupon already exists for code {}", code)
            throw CouponAlreadyExistsException(code.value)
        }

        val result = repository.save(Coupon(null, code, CountryCode(command.countryCode), 0, command.maxUses, Instant.now()))
        logger.info("Coupon created: ${code.value}")
        return result
    }
}