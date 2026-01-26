package com.animalshelter.dto

import com.animalshelter.model.AdoptionStatus
import com.animalshelter.model.ApplicationStatus
import com.animalshelter.model.PetStatus

data class CancelAdoptionResponse(
    val adoptionHistoryId: Long,
    val adoptionStatus: AdoptionStatus,
    val applicationId: Long,
    val applicationStatus: ApplicationStatus,
    val petId: Long,
    val petStatus: PetStatus,
    val message: String
)