package com.animalshelter.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class UpdatePetRequest(
    @field:Size(min = 1, max = 100)
    val name: String?,

    @field:PositiveOrZero
    @field:Max(50)
    val age: Int?,

    @field:Size(max = 500)
    val imageUrl: String?,

    @field:Size(max = 10000)
    val description: String?
)