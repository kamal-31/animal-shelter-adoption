package com.animalshelter.dto

import com.animalshelter.model.Pet
import com.animalshelter.model.PetStatus
import java.time.Instant

data class PetDto(
    val id: Long,
    val name: String,
    val species: String,
    val age: Int,
    val imageUrl: String?,
    val description: String?,
    val status: PetStatus,
    val pendingApplicationCount: Int = 0,
    val createdAt: Instant? = null,
    val deletedAt: Instant? = null
)

// Extension function to convert Pet entity to DTO
fun Pet.toDto(pendingApplicationCount: Int = 0): PetDto {
    return PetDto(
        id = this.id!!,
        name = this.name,
        species = this.species.name,
        age = this.age,
        imageUrl = this.imageUrl,
        description = this.description,
        status = this.status,
        pendingApplicationCount = pendingApplicationCount,
        createdAt = this.createdAt,
        deletedAt = this.deletedAt
    )
}