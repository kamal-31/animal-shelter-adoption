package com.animalshelter.dto

import jakarta.validation.constraints.Size

data class ConfirmAdoptionRequest(
    @field:Size(max = 5000)
    val notes: String? = null
)