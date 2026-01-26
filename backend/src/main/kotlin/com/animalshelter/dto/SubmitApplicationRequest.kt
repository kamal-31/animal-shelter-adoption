package com.animalshelter.dto

import jakarta.validation.constraints.*

data class SubmitApplicationRequest(
    @field:Positive(message = "Pet ID must be positive")
    val petId: Long,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 200, message = "Name must be 1-200 characters")
    val applicantName: String,

    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:Pattern(
        regexp = "^[0-9\\-\\+\\(\\)\\s]*$",
        message = "Invalid phone number format"
    )
    @field:Size(max = 20, message = "Phone must be max 20 characters")
    val phone: String?,

    @field:NotBlank(message = "Reason is required")
    @field:Size(min = 50, max = 5000, message = "Reason must be 50-5000 characters")
    val reason: String
)