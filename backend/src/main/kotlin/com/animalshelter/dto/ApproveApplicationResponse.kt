package com.animalshelter.dto

import com.animalshelter.model.AdoptionStatus
import com.animalshelter.model.ApplicationStatus

data class ApproveApplicationResponse(
    val applicationId: Long,
    val status: ApplicationStatus,
    val adoptionHistoryId: Long,
    val adoptionStatus: AdoptionStatus,
    val message: String
)