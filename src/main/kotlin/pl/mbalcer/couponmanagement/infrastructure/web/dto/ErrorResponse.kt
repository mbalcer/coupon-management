package pl.mbalcer.couponmanagement.infrastructure.web.dto

import org.springframework.http.HttpStatus
import java.time.OffsetDateTime

data class ErrorResponse(
    val timestamp: OffsetDateTime,
    val statusCode: Int,
    val statusMessage: String,
    val errorMessage: String
) {
    companion object {
        fun of(status: HttpStatus, errorMessage: String) = ErrorResponse(OffsetDateTime.now(), status.value(), status.reasonPhrase, errorMessage)
    }
}