package com.animalshelter.repository

import com.animalshelter.model.AdoptionHistory
import com.animalshelter.model.AdoptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AdoptionHistoryRepository : JpaRepository<AdoptionHistory, Long> {

    // Get all adoption attempts for a pet
    fun findByPetIdOrderByAdoptedAtDesc(petId: Long): List<AdoptionHistory>

    // Get all adoptions by an applicant
    fun findByApplicantIdOrderByAdoptedAtDesc(applicantId: Long): List<AdoptionHistory>

    // Find by application
    fun findByApplicationId(applicationId: Long): AdoptionHistory?

    // Find by status
    fun findByStatusOrderByAdoptedAtAsc(status: AdoptionStatus): List<AdoptionHistory>

    // Get current active adoption for a pet (if any)
    fun findByPetIdAndStatus(petId: Long, status: AdoptionStatus): AdoptionHistory?

    // Check if pet has active adoption
    fun existsByPetIdAndStatus(petId: Long, status: AdoptionStatus): Boolean

    // Admin query: Get adoptions with filters
    @Query(
        """
        SELECT ah FROM AdoptionHistory ah
        WHERE (:status IS NULL OR ah.status = :status)
        AND (:petId IS NULL OR ah.petId = :petId)
        ORDER BY ah.adoptedAt ASC
    """
    )
    fun findByFilters(
        @Param("status") status: AdoptionStatus?,
        @Param("petId") petId: Long?
    ): List<AdoptionHistory>
}


