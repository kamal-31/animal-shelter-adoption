package com.animalshelter.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "pets")
data class Pet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id", nullable = false)
    val species: Species,

    @Column(nullable = false)
    var age: Int,

    @Column(name = "image_url", length = 500)
    var imageUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PetStatus = PetStatus.AVAILABLE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null
) {
    // Helper method for soft delete
    fun softDelete() {
        deletedAt = Instant.now()
    }

    // Helper method to check if deleted
    fun isDeleted(): Boolean = deletedAt != null
}

enum class PetStatus {
    AVAILABLE,
    PENDING,
    ADOPTED
}