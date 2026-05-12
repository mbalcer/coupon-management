package pl.mbalcer.couponmanagement.infrastructure.web

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import pl.mbalcer.couponmanagement.domain.exception.*
import pl.mbalcer.couponmanagement.infrastructure.web.dto.ErrorResponse

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(CouponNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: CouponNotFoundException) =
        ErrorResponse.of(HttpStatus.NOT_FOUND, ex.message!!)

    @ExceptionHandler(CouponExhaustedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleExhausted(ex: CouponExhaustedException) =
        ErrorResponse.of(HttpStatus.CONFLICT, ex.message!!)

    @ExceptionHandler(CouponAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleCouponAlreadyExists(ex: CouponAlreadyExistsException) =
        ErrorResponse.of(HttpStatus.CONFLICT, ex.message!!)

    @ExceptionHandler(CouponAlreadyUsedException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleCouponAlreadyUsed(ex: CouponAlreadyUsedException) =
        ErrorResponse.of(HttpStatus.CONFLICT, ex.message!!)

    @ExceptionHandler(CountryNotAllowedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleCountryNotAllowed(ex: CountryNotAllowedException) =
        ErrorResponse.of(HttpStatus.FORBIDDEN, ex.message!!)

    @ExceptionHandler(GeoIpResolutionException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun handleGeoIpResolutionException(ex: GeoIpResolutionException) =
        ErrorResponse.of(HttpStatus.SERVICE_UNAVAILABLE, ex.message!!)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: MethodArgumentNotValidException): ErrorResponse {
        val details = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, details.toString())
    }
}