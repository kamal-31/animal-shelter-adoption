package com.animalshelter.dto

import com.animalshelter.model.ApplicationStatus
import java.time.Instant

data class SubmitApplicationResponse(
    val id: Long,
    val petId: Long,
    val petName: String,
    val applicantName: String,
    val status: ApplicationStatus,
    val submittedAt: Instant,
    val message: String
)