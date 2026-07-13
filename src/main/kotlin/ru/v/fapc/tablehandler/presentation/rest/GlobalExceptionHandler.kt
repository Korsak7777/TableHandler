package ru.v.fapc.tablehandler.presentation.rest

import com.example.model.ApiError
import com.example.model.ModelApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.v.fapc.tablehandler.domain.exception.DomainValidationException
import ru.v.fapc.tablehandler.utils.getLogger

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = getLogger()

    @ExceptionHandler(DomainValidationException::class)
    fun handleDomainValidation(ex: DomainValidationException): ResponseEntity<ModelApiResponse> {
        log.warn("Domain validation failed: ${ex.message}")
        return ResponseEntity.badRequest()
            .body(ModelApiResponse(
                status = ModelApiResponse.Status.ERROR,
                error = ApiError(ex.message, ex.details)))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ModelApiResponse> {
        val details = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        log.warn("Request validation failed: $details")
        return ResponseEntity.badRequest()
            .body(ModelApiResponse(
                status = ModelApiResponse.Status.ERROR,
                error = ApiError("Validation failed", details)))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBadRequest(ex: HttpMessageNotReadableException): ResponseEntity<ModelApiResponse> {
        log.warn("Malformed request: ${ex.message}")
        return ResponseEntity.badRequest()
            .body(ModelApiResponse(
                status = ModelApiResponse.Status.ERROR,
                error = ApiError("Malformed request body")))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ModelApiResponse> {
        log.error("Unexpected error", ex)
        return ResponseEntity.internalServerError()
            .body(ModelApiResponse(
                status = ModelApiResponse.Status.ERROR,
                error = ApiError(ex.message ?: "Internal server error")))
    }
}
