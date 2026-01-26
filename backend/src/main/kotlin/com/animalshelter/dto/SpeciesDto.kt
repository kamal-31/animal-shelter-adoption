package com.animalshelter.dto

import com.animalshelter.model.Species

data class SpeciesDto(
    val id: Int,
    val name: String
)

// Extension function to convert Species entity to DTO
fun Species.toDto(): SpeciesDto {
    return SpeciesDto(
        id = this.id!!,
        name = this.name
    )
}