package com.animalshelter.dto

import jakarta.validation.constraints.*

data class CreatePetRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    val name: String,

    @field:Positive(message = "Species ID must be positive")
    val speciesId: Int,

    @field:PositiveOrZero(message = "Age must be 0 or positive")
    @field:Max(value = 50, message = "Age must be less than 50")
    val age: Int,

    @field:Size(max = 500, message = "Image URL must be max 500 characters")
    val imageUrl: String?,

    @field:Size(max = 10000, message = "Description must be max 10000 characters")
    val description: String?
)