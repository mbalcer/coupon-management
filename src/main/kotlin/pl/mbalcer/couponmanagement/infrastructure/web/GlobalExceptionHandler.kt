package pl.mbalcer.couponmanagement.infrastructure.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pl.mbalcer.couponmanagement.domain.exception.CouponAlreadyExistsException
import pl.mbalcer.couponmanagement.domain.exception.CouponExhaustedException
import pl.mbalcer.couponmanagement.domain.exception.CouponNotFoundException
import pl.mbalcer.couponmanagement.infrastructure.web.dto.ErrorResponse

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CouponNotFoundException::class)
    fun handleNotFound(ex: CouponNotFoundException) =
        ErrorResponse.of(HttpStatus.NOT_FOUND, ex.message!!)

    @ExceptionHandler(CouponExhaustedException::class)
    fun handleExhausted(ex: CouponExhaustedException) =
        ErrorResponse.of(HttpStatus.CONFLICT, ex.message!!)

    @ExceptionHandler(CouponAlreadyExistsException::class)
    fun handleExhausted(ex: CouponAlreadyExistsException) =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.message!!)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ErrorResponse {
        val details = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, details.toString())
    }
}