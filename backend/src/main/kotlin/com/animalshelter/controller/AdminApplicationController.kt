package com.animalshelter.controller

import com.animalshelter.dto.*
import com.animalshelter.model.AdoptionStatus
import com.animalshelter.model.ApplicationStatus
import com.animalshelter.service.AdoptionService
import com.animalshelter.service.ApplicationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = ["http://localhost:5173"])
class AdminApplicationController(
    private val applicationService: ApplicationService,
    private val adoptionService: AdoptionService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    // ==================== Applications ====================

    /**
     * GET /api/admin/applications
     * List all applications with filters
     */
    @GetMapping("/applications")
    fun getApplications(
        @RequestParam(required = false) status: ApplicationStatus?,
        @RequestParam(required = false) petId: Long?
    ): List<AdminApplicationDto> {
        log.info("Fetching applications with status={}, petId={}", status, petId)
        return applicationService.findAllForAdmin(status, petId)
    }

    /**
     * POST /api/admin/applications/{id}/approve
     * Approve application
     */
    @PostMapping("/applications/{id}/approve")
    fun approveApplication(
        @PathVariable id: Long,
        @RequestBody request: ApproveApplicationRequest
    ): ApproveApplicationResponse {
        log.info("Approving application id={}, reviewedBy={}", id, request.reviewedBy)
        return applicationService.approveApplication(id, request)
    }

    /**
     * POST /api/admin/applications/{id}/reject
     * Reject application
     */
    @PostMapping("/applications/{id}/reject")
    fun rejectApplication(
        @PathVariable id: Long,
        @RequestBody request: RejectApplicationRequest
    ): RejectApplicationResponse {
        log.info("Rejecting application id={}, reviewedBy={}", id, request.reviewedBy)
        return applicationService.rejectApplication(id, request)
    }

    // ==================== Adoptions ====================

    /**
     * GET /api/admin/adoptions
     * List all adoptions with filters
     */
    @GetMapping("/adoptions")
    fun getAdoptions(
        @RequestParam(required = false) status: AdoptionStatus?,
        @RequestParam(required = false) petId: Long?
    ): List<AdminAdoptionDto> {
        log.debug("Fetching adoptions with status={}, petId={}", status, petId)
        return adoptionService.findAllForAdmin(status, petId)
    }

    /**
     * POST /api/admin/adoptions/{id}/confirm
     * Confirm adoption (family picked up pet)
     */
    @PostMapping("/adoptions/{id}/confirm")
    fun confirmAdoption(
        @PathVariable id: Long,
        @RequestBody request: ConfirmAdoptionRequest
    ): ConfirmAdoptionResponse {
        log.info("Confirming adoption id={}", id)
        return adoptionService.confirmAdoption(id, request)
    }

    /**
     * POST /api/admin/adoptions/{id}/cancel
     * Cancel adoption (family didn't show up)
     */
    @PostMapping("/adoptions/{id}/cancel")
    fun cancelAdoption(
        @PathVariable id: Long,
        @RequestBody @Valid request: CancelAdoptionRequest
    ): CancelAdoptionResponse {
        log.info("Cancelling adoption id={}, reason={}", id, request.reason)
        return adoptionService.cancelAdoption(id, request)
    }

    /**
     * POST /api/admin/adoptions/{id}/return
     * Mark pet as returned
     */
    @PostMapping("/adoptions/{id}/return")
    fun returnPet(
        @PathVariable id: Long,
        @RequestBody @Valid request: ReturnPetRequest
    ): ReturnPetResponse {
        log.info("Returning pet for adoption id={}, reason={}", id, request.returnReason)
        return adoptionService.returnPet(id, request)
    }
}