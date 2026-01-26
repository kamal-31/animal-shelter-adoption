package com.animalshelter.dto

import com.animalshelter.model.ApplicationStatus
import com.animalshelter.model.PetStatus

data class RejectApplicationResponse(
    val applicationId: Long,
    val status: ApplicationStatus,
    val petStatus: PetStatus,
    val message: String
)