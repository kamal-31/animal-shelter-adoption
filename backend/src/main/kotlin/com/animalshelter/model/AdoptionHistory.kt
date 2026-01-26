package com.animalshelter.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "adoption_history")
data class AdoptionHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "pet_id", nullable = false)
    val petId: Long,

    @Column(name = "application_id", nullable = false)
    val applicationId: Long,

    @Column(name = "applicant_id", nullable = false)
    val applicantId: Long,

    @Column(name = "adopted_at", nullable = false, updatable = false)
    val adoptedAt: Instant = Instant.now(),

    @Column(name = "returned_at")
    var returnedAt: Instant? = null,

    @Column(name = "return_reason", columnDefinition = "TEXT")
    var returnReason: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: AdoptionStatus = AdoptionStatus.PENDING_PICKUP,

    @Column(columnDefinition = "TEXT")
    var notes: String? = null
)

enum class AdoptionStatus {
    PENDING_PICKUP,
    ACTIVE,
    RETURNED,
    CANCELLED
}