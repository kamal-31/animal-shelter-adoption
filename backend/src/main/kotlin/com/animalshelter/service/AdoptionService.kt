package com.animalshelter.service

import com.animalshelter.dto.*
import com.animalshelter.exception.*
import com.animalshelter.model.*
import com.animalshelter.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AdoptionService(
    private val adoptionHistoryRepository: AdoptionHistoryRepository,
    private val applicationRepository: ApplicationRepository,
    private val petRepository: PetRepository,
    private val applicantRepository: ApplicantRepository
) {

    /**
     * Get all adoptions with filters (admin)
     */
    fun findAllForAdmin(status: AdoptionStatus?, petId: Long?): List<AdminAdoptionDto> {
        val adoptions = adoptionHistoryRepository.findByFilters(status, petId)

        return adoptions.mapNotNull { adoption ->
            val pet = petRepository.findById(adoption.petId).orElse(null) ?: return@mapNotNull null
            val applicant = applicantRepository.findById(adoption.applicantId).orElse(null)

            AdminAdoptionDto(
                id = adoption.id!!,
                petId = adoption.petId,
                petName = pet.name,
                applicationId = adoption.applicationId,
                applicantId = adoption.applicantId,
                applicantName = applicant?.name ?: "",
                applicantEmail = applicant?.email ?: "",
                status = adoption.status,
                adoptedAt = adoption.adoptedAt,
                returnedAt = adoption.returnedAt,
                returnReason = adoption.returnReason,
                notes = adoption.notes
            )
        }
    }

    /**
     * Confirm adoption - family picked up pet (admin)
     */
    @Transactional
    fun confirmAdoption(
        adoptionHistoryId: Long,
        request: ConfirmAdoptionRequest
    ): ConfirmAdoptionResponse {
        val adoption = adoptionHistoryRepository.findById(adoptionHistoryId)
            .orElseThrow { AdoptionNotFoundException(adoptionHistoryId) }

        require(adoption.status == AdoptionStatus.PENDING_PICKUP) {
            throw BusinessRuleViolationException(
                "Can only confirm adoptions in PENDING_PICKUP status. Current status: ${adoption.status}"
            )
        }

        // Update adoption status
        adoption.status = AdoptionStatus.ACTIVE
        if (request.notes != null) {
            adoption.notes = request.notes
        }
        adoptionHistoryRepository.save(adoption)

        // Update pet status to ADOPTED
        val pet = petRepository.findById(adoption.petId)
            .orElseThrow { PetNotFoundException(adoption.petId) }
        pet.status = PetStatus.ADOPTED
        petRepository.save(pet)

        // Reject all other PENDING/APPROVED applications for this pet
        val otherApplications = applicationRepository
            .findByPetIdAndStatusInOrderBySubmittedAtAsc(
                adoption.petId,
                listOf(ApplicationStatus.PENDING, ApplicationStatus.APPROVED)
            )
            .filter { it.id != adoption.applicationId }

        otherApplications.forEach { app ->
            app.status = ApplicationStatus.REJECTED
            app.reviewedAt = Instant.now()
            applicationRepository.save(app)
        }

        return ConfirmAdoptionResponse(
            adoptionHistoryId = adoption.id!!,
            adoptionStatus = adoption.status,
            petId = pet.id!!,
            petStatus = pet.status,
            rejectedApplicationCount = otherApplications.size,
            message = "Adoption confirmed. Pet marked as ADOPTED. ${otherApplications.size} other application(s) rejected."
        )
    }

    /**
     * Cancel adoption - family didn't show up (admin)
     */
    @Transactional
    fun cancelAdoption(
        adoptionHistoryId: Long,
        request: CancelAdoptionRequest
    ): CancelAdoptionResponse {
        val adoption = adoptionHistoryRepository.findById(adoptionHistoryId)
            .orElseThrow { AdoptionNotFoundException(adoptionHistoryId) }

        require(adoption.status == AdoptionStatus.PENDING_PICKUP) {
            throw BusinessRuleViolationException("Can only cancel adoptions in PENDING_PICKUP status")
        }

        // Update adoption history
        adoption.status = AdoptionStatus.CANCELLED
        adoption.notes = request.reason
        adoptionHistoryRepository.save(adoption)

        // Update application status
        val application = applicationRepository.findById(adoption.applicationId)
            .orElseThrow { ApplicationNotFoundException(adoption.applicationId) }
        application.status = ApplicationStatus.ADOPTION_CANCELLED
        applicationRepository.save(application)

        // Check if pet should revert to AVAILABLE or stay PENDING
        val pet = petRepository.findById(adoption.petId)
            .orElseThrow { PetNotFoundException(adoption.petId) }

        val hasActiveApplications = applicationRepository.existsByPetIdAndStatusIn(
            adoption.petId,
            listOf(ApplicationStatus.PENDING, ApplicationStatus.APPROVED)
        )

        if (!hasActiveApplications) {
            pet.status = PetStatus.AVAILABLE
        } else {
            pet.status = PetStatus.PENDING
        }
        petRepository.save(pet)

        return CancelAdoptionResponse(
            adoptionHistoryId = adoption.id!!,
            adoptionStatus = adoption.status,
            applicationId = application.id!!,
            applicationStatus = application.status,
            petId = pet.id!!,
            petStatus = pet.status,
            message = "Adoption cancelled. Application marked as ADOPTION_CANCELLED. Pet status: ${pet.status}."
        )
    }

    /**
     * Return pet - family returned pet to shelter (admin)
     */
    @Transactional
    fun returnPet(
        adoptionHistoryId: Long,
        request: ReturnPetRequest
    ): ReturnPetResponse {
        val adoption = adoptionHistoryRepository.findById(adoptionHistoryId)
            .orElseThrow { AdoptionNotFoundException(adoptionHistoryId) }

        require(adoption.status == AdoptionStatus.ACTIVE) {
            throw BusinessRuleViolationException("Can only return pets with ACTIVE adoption status")
        }

        // Update adoption history
        adoption.status = AdoptionStatus.RETURNED
        adoption.returnedAt = Instant.now()
        adoption.returnReason = request.returnReason
        if (request.notes != null) {
            adoption.notes = (adoption.notes ?: "") + "\n" + request.notes
        }
        adoptionHistoryRepository.save(adoption)

        // Update pet status - back to AVAILABLE
        val pet = petRepository.findById(adoption.petId)
            .orElseThrow { PetNotFoundException(adoption.petId) }
        pet.status = PetStatus.AVAILABLE
        petRepository.save(pet)

        return ReturnPetResponse(
            adoptionHistoryId = adoption.id!!,
            adoptionStatus = adoption.status,
            petId = pet.id!!,
            petStatus = pet.status,
            returnedAt = adoption.returnedAt!!,
            message = "Pet marked as returned. Pet status updated to AVAILABLE for re-adoption."
        )
    }
}