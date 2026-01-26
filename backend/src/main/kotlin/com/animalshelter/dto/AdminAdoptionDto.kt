package com.animalshelter.dto

import com.animalshelter.model.AdoptionStatus
import java.time.Instant

data class AdminAdoptionDto(
    val id: Long,
    val petId: Long,
    val petName: String,
    val applicationId: Long,
    val applicantId: Long,
    val applicantName: String,
    val applicantEmail: String,
    val status: AdoptionStatus,
    val adoptedAt: Instant,
    val returnedAt: Instant?,
    val returnReason: String?,
    val notes: String?
)