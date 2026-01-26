package com.animalshelter.dto

import com.animalshelter.model.AdoptionStatus
import com.animalshelter.model.PetStatus
import java.time.Instant

data class ReturnPetResponse(
    val adoptionHistoryId: Long,
    val adoptionStatus: AdoptionStatus,
    val petId: Long,
    val petStatus: PetStatus,
    val returnedAt: Instant,
    val message: String
)