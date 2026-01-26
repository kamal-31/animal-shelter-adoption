package com.animalshelter.dto

import jakarta.validation.constraints.Size

data class RejectApplicationRequest(
    @field:Size(max = 100)
    val reviewedBy: String? = null
)