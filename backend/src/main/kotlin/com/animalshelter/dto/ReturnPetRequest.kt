package com.animalshelter.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ReturnPetRequest(
    @field:NotBlank(message = "Return reason is required")
    @field:Size(min = 10, max = 5000, message = "Return reason must be 10-5000 characters")
    val returnReason: String,

    @field:Size(max = 5000, message = "Notes must be max 5000 characters")
    val notes: String? = null
)