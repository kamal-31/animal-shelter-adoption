package com.animalshelter.service

import com.animalshelter.dto.*
import com.animalshelter.exception.ApplicationNotFoundException
import com.animalshelter.exception.BusinessRuleViolationException
import com.animalshelter.exception.DuplicateApplicationException
import com.animalshelter.exception.PetNotFoundException
import com.animalshelter.model.*
import com.animalshelter.repository.AdoptionHistoryRepository
import com.animalshelter.repository.ApplicationRepository
import com.animalshelter.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val petRepository: PetRepository,
    private val applicantService: ApplicantService,
    private val adoptionHistoryRepository: AdoptionHistoryRepository
) {

    /**
     * Submit adoption application (public)
     */
    @Transactional
    fun submitApplication(request: SubmitApplicationRequest): SubmitApplicationResponse {
        // 1. Find or create applicant
        val applicant = applicantService.findOrCreate(
            name = request.applicantName,
            email = request.email,
            phone = request.phone
        )

        // 2. Validate pet
        val pet = petRepository.findByIdAndDeletedAtIsNull(request.petId)
            ?: throw PetNotFoundException(request.petId)

        require(pet.status != PetStatus.ADOPTED) {
            throw BusinessRuleViolationException("Pet is already adopted")
        }

        // 3. Check for duplicate application
        if (applicationRepository.existsByPetIdAndApplicantId(request.petId, applicant.id!!)) {
            throw DuplicateApplicationException("You have already applied for this pet")
        }

        // 4. Create application
        val application = applicationRepository.save(
            Application(
                petId = request.petId,
                applicantId = applicant.id,
                reason = request.reason,
                status = ApplicationStatus.PENDING
            )
        )

        // 5. Update pet status if first application
        if (pet.status == PetStatus.AVAILABLE) {
            pet.status = PetStatus.PENDING
            petRepository.save(pet)
        }

        return SubmitApplicationResponse(
            id = application.id!!,
            petId = application.petId,
            petName = pet.name,
            applicantName = applicant.name,
            status = application.status,
            submittedAt = application.submittedAt,
            message = "Application submitted successfully. We will review your application and contact you soon."
        )
    }

    /**
     * Get all applications with filters (admin)
     */
    fun findAllForAdmin(status: ApplicationStatus?, petId: Long?): List<AdminApplicationDto> {
        val applications = applicationRepository.findByFilters(status, petId)

        return applications.mapNotNull { application ->
            val pet = petRepository.findById(application.petId).orElse(null) ?: return@mapNotNull null
            val applicant = applicantService.findById(application.applicantId) ?: return@mapNotNull null

            AdminApplicationDto(
                id = application.id!!,
                petId = application.petId,
                petName = pet.name,
                petSpecies = pet.species.name,
                petImageUrl = pet.imageUrl,
                petStatus = pet.status,
                applicantId = application.applicantId,
                applicantName = applicant.name,
                applicantEmail = applicant.email,
                applicantPhone = applicant.phone,
                reason = application.reason,
                status = application.status,
                submittedAt = application.submittedAt,
                reviewedAt = application.reviewedAt,
                reviewedBy = application.reviewedBy
            )
        }
    }

    /**
     * Approve application (admin)
     */
    @Transactional
    fun approveApplication(
        applicationId: Long,
        request: ApproveApplicationRequest
    ): ApproveApplicationResponse {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException(applicationId) }

        require(application.status == ApplicationStatus.PENDING) {
            throw BusinessRuleViolationException(
                "Can only approve pending applications. Current status: ${application.status}"
            )
        }

        val pet = petRepository.findById(application.petId)
            .orElseThrow { PetNotFoundException(application.petId) }

        require(pet.status != PetStatus.ADOPTED) {
            throw BusinessRuleViolationException("Pet is already adopted")
        }

        // ✅ NEW: Check if there's already an approved application for this pet
        val hasApprovedApplication = applicationRepository.existsByPetIdAndStatusIn(
            application.petId,
            listOf(ApplicationStatus.APPROVED)
        )

        if (hasApprovedApplication) {
            throw BusinessRuleViolationException(
                "Cannot approve: This pet already has an approved application. " +
                        "Please reject or cancel the existing approved application first."
            )
        }

        // ✅ NEW: Check if there's already an active adoption for this pet
        val hasActiveAdoption = adoptionHistoryRepository.existsByPetIdAndStatus(
            application.petId,
            AdoptionStatus.PENDING_PICKUP
        ) || adoptionHistoryRepository.existsByPetIdAndStatus(
            application.petId,
            AdoptionStatus.ACTIVE
        )

        if (hasActiveAdoption) {
            throw BusinessRuleViolationException(
                "Cannot approve: This pet already has an active adoption."
            )
        }

        // Update application
        application.status = ApplicationStatus.APPROVED
        application.reviewedAt = Instant.now()
        application.reviewedBy = request.reviewedBy
        applicationRepository.save(application)

        // Create adoption history (PENDING_PICKUP status)
        val adoptionHistory = adoptionHistoryRepository.save(
            AdoptionHistory(
                petId = application.petId,
                applicationId = application.id!!,
                applicantId = application.applicantId,
                status = AdoptionStatus.PENDING_PICKUP
            )
        )

        return ApproveApplicationResponse(
            applicationId = application.id!!,
            status = application.status,
            adoptionHistoryId = adoptionHistory.id!!,
            adoptionStatus = adoptionHistory.status,
            message = "Application approved. Adoption history created. Waiting for family to pick up pet."
        )
    }

    /**
     * Reject application (admin)
     */
    @Transactional
    fun rejectApplication(
        applicationId: Long,
        request: RejectApplicationRequest
    ): RejectApplicationResponse {
        val application = applicationRepository.findById(applicationId)
            .orElseThrow { ApplicationNotFoundException(applicationId) }

        require(application.status == ApplicationStatus.PENDING) {
            throw BusinessRuleViolationException("Can only reject pending applications")
        }

        application.status = ApplicationStatus.REJECTED
        application.reviewedAt = Instant.now()
        application.reviewedBy = request.reviewedBy
        applicationRepository.save(application)

        // Check if pet should revert to AVAILABLE
        val pet = petRepository.findById(application.petId)
            .orElseThrow { PetNotFoundException(application.petId) }

        if (pet.status == PetStatus.PENDING) {
            val hasActiveApplications = applicationRepository.existsByPetIdAndStatusIn(
                application.petId,
                listOf(ApplicationStatus.PENDING, ApplicationStatus.APPROVED)
            )

            if (!hasActiveApplications) {
                pet.status = PetStatus.AVAILABLE
                petRepository.save(pet)
            }
        }

        return RejectApplicationResponse(
            applicationId = application.id!!,
            status = application.status,
            petStatus = pet.status,
            message = if (pet.status == PetStatus.AVAILABLE) {
                "Application rejected. Pet status updated to AVAILABLE (no pending applications remaining)."
            } else {
                "Application rejected."
            }
        )
    }
}