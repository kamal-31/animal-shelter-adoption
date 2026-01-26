package com.animalshelter.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "applications",
    uniqueConstraints = [
        UniqueConstraint(
            name = "unique_pet_applicant",
            columnNames = ["pet_id", "applicant_id"]
        )
    ]
)
data class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "pet_id", nullable = false)
    val petId: Long,

    @Column(name = "applicant_id", nullable = false)
    val applicantId: Long,

    @Column(nullable = false, columnDefinition = "TEXT")
    val reason: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: ApplicationStatus = ApplicationStatus.PENDING,

    @Column(name = "submitted_at", nullable = false, updatable = false)
    val submittedAt: Instant = Instant.now(),

    @Column(name = "reviewed_at")
    var reviewedAt: Instant? = null,

    @Column(name = "reviewed_by", length = 100)
    var reviewedBy: String? = null
)

enum class ApplicationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    ADOPTION_CANCELLED
}