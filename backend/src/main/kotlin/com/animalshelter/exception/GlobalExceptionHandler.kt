package com.animalshelter.exception

import com.animalshelter.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Handle Pet not found
     */
    @ExceptionHandler(PetNotFoundException::class)
    fun handlePetNotFound(
        ex: PetNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Pet not found: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 404,
            error = "Not Found",
            message = ex.message ?: "Pet not found",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    /**
     * Handle Application not found
     */
    @ExceptionHandler(ApplicationNotFoundException::class)
    fun handleApplicationNotFound(
        ex: ApplicationNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Application not found: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 404,
            error = "Not Found",
            message = ex.message ?: "Application not found",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    /**
     * Handle Adoption not found
     */
    @ExceptionHandler(AdoptionNotFoundException::class)
    fun handleAdoptionNotFound(
        ex: AdoptionNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Adoption not found: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 404,
            error = "Not Found",
            message = ex.message ?: "Adoption not found",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    /**
     * Handle Species not found
     */
    @ExceptionHandler(SpeciesNotFoundException::class)
    fun handleSpeciesNotFound(
        ex: SpeciesNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Species not found: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 400,
            error = "Bad Request",
            message = ex.message ?: "Species not found",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    /**
     * Handle duplicate application
     */
    @ExceptionHandler(DuplicateApplicationException::class)
    fun handleDuplicateApplication(
        ex: DuplicateApplicationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Duplicate application: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 409,
            error = "Conflict",
            message = ex.message ?: "Duplicate application",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    /**
     * Handle business rule violations
     */
    @ExceptionHandler(BusinessRuleViolationException::class)
    fun handleBusinessRuleViolation(
        ex: BusinessRuleViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Business rule violation: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 422,
            error = "Unprocessable Entity",
            message = ex.message ?: "Business rule violation",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error)
    }

    /**
     * Handle image upload errors
     */
    @ExceptionHandler(ImageUploadException::class)
    fun handleImageUpload(
        ex: ImageUploadException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Image upload failed: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 400,
            error = "Bad Request",
            message = ex.message ?: "Image upload failed",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        logger.info("Validation failed: {}", fieldErrors)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 400,
            error = "Bad Request",
            message = "Validation failed",
            path = request.requestURI,
            details = fieldErrors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Illegal argument: {}", ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 400,
            error = "Bad Request",
            message = ex.message ?: "Invalid request",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error", ex)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = 500,
            error = "Internal Server Error",
            message = "An unexpected error occurred. Please try again later.",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}