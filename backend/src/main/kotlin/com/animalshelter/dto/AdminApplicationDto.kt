package com.animalshelter.dto

import com.animalshelter.model.ApplicationStatus
import com.animalshelter.model.PetStatus
import java.time.Instant

data class AdminApplicationDto(
    val id: Long,
    val petId: Long,
    val petName: String,
    val petSpecies: String,
    val petImageUrl: String?,
    val petStatus: PetStatus,
    val applicantId: Long,
    val applicantName: String,
    val applicantEmail: String,
    val applicantPhone: String?,
    val reason: String,
    val status: ApplicationStatus,
    val submittedAt: Instant,
    val reviewedAt: Instant?,
    val reviewedBy: String?
)