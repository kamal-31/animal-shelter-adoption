package com.animalshelter.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "applicants")
data class Applicant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(nullable = false, unique = true, length = 255)
    val email: String,

    @Column(length = 20)
    var phone: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)