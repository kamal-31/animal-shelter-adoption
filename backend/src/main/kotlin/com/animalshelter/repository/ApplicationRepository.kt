package com.animalshelter.repository

import com.animalshelter.model.Application
import com.animalshelter.model.ApplicationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ApplicationRepository : JpaRepository<Application, Long> {

    // Check if applicant already applied to this pet
    fun existsByPetIdAndApplicantId(petId: Long, applicantId: Long): Boolean

    // Find all applications for a pet
    fun findByPetIdOrderBySubmittedAtAsc(petId: Long): List<Application>

    // Find all applications by an applicant
    fun findByApplicantIdOrderBySubmittedAtDesc(applicantId: Long): List<Application>

    // Find by status
    fun findByStatusOrderBySubmittedAtAsc(status: ApplicationStatus): List<Application>

    // Find by pet and status
    fun findByPetIdAndStatusInOrderBySubmittedAtAsc(
        petId: Long,
        statuses: List<ApplicationStatus>
    ): List<Application>

    // Check if pet has active applications (pending or approved)
    fun existsByPetIdAndStatusIn(
        petId: Long,
        statuses: List<ApplicationStatus>
    ): Boolean

    // Admin query: Get applications with filters
    @Query(
        """
        SELECT a FROM Application a
        WHERE (:status IS NULL OR a.status = :status)
        AND (:petId IS NULL OR a.petId = :petId)
        ORDER BY a.submittedAt ASC
    """
    )
    fun findByFilters(
        @Param("status") status: ApplicationStatus?,
        @Param("petId") petId: Long?
    ): List<Application>
}