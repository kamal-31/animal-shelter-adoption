package com.animalshelter.dto

import com.animalshelter.model.AdoptionStatus
import com.animalshelter.model.PetStatus

data class ConfirmAdoptionResponse(
    val adoptionHistoryId: Long,
    val adoptionStatus: AdoptionStatus,
    val petId: Long,
    val petStatus: PetStatus,
    val rejectedApplicationCount: Int,
    val message: String
)