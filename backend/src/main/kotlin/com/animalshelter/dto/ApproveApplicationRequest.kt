package com.animalshelter.dto

import jakarta.validation.constraints.Size

data class ApproveApplicationRequest(
    @field:Size(max = 100, message = "Reviewer name must be max 100 characters")
    val reviewedBy: String? = null
)