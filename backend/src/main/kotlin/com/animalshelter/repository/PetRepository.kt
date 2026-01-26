package com.animalshelter.repository

import com.animalshelter.model.Pet
import com.animalshelter.model.PetStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PetRepository : JpaRepository<Pet, Long> {

    // Find all non-deleted pets
    fun findByDeletedAtIsNull(): List<Pet>

    // Find by status (excluding deleted)
    fun findByStatusAndDeletedAtIsNull(status: PetStatus): List<Pet>

    // Find by species (excluding deleted)
    fun findBySpeciesIdAndDeletedAtIsNull(speciesId: Int): List<Pet>

    // Find by status and species (excluding deleted)
    fun findByStatusAndSpeciesIdAndDeletedAtIsNull(
        status: PetStatus,
        speciesId: Int
    ): List<Pet>

    // Find by ID (excluding deleted)
    fun findByIdAndDeletedAtIsNull(id: Long): Pet?

    // Count pending applications for a pet
    @Query(
        """
        SELECT COUNT(a) FROM Application a 
        WHERE a.petId = :petId 
        AND a.status = 'PENDING'
    """
    )
    fun countPendingApplications(@Param("petId") petId: Long): Int

    // Check if pet has any pending or approved applications
    @Query(
        """
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END 
        FROM Application a 
        WHERE a.petId = :petId 
        AND a.status IN ('PENDING', 'APPROVED')
    """
    )
    fun hasPendingOrApprovedApplications(@Param("petId") petId: Long): Boolean
}