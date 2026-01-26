package com.animalshelter.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CancelAdoptionRequest(
    @field:NotBlank(message = "Reason is required")
    @field:Size(min = 10, max = 5000, message = "Reason must be 10-5000 characters")
    val reason: String
)