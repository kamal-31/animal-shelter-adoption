package com.animalshelter.controller

import com.animalshelter.dto.PetDto
import com.animalshelter.dto.SpeciesDto
import com.animalshelter.dto.SubmitApplicationRequest
import com.animalshelter.dto.SubmitApplicationResponse
import com.animalshelter.model.PetStatus
import com.animalshelter.service.ApplicationService
import com.animalshelter.service.PetService
import com.animalshelter.service.SpeciesService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:5173"])
class PublicController(
    private val petService: PetService,
    private val speciesService: SpeciesService,
    private val applicationService: ApplicationService
) {

    /**
     * GET /api/pets
     * List all pets with optional filters
     */
    @GetMapping("/pets")
    fun getPets(
        @RequestParam(required = false) status: PetStatus?,
        @RequestParam(required = false) speciesId: Int?
    ): List<PetDto> {
        return petService.findAll(status, speciesId)
    }

    /**
     * GET /api/pets/{id}
     * Get single pet by ID
     */
    @GetMapping("/pets/{id}")
    fun getPetById(@PathVariable id: Long): PetDto {
        return petService.findById(id)
    }

    /**
     * GET /api/species
     * Get all species
     */
    @GetMapping("/species")
    fun getAllSpecies(): List<SpeciesDto> {
        return speciesService.findAll()
    }

    /**
     * POST /api/applications
     * Submit adoption application
     */
    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    fun submitApplication(
        @RequestBody @Valid request: SubmitApplicationRequest
    ): SubmitApplicationResponse {
        return applicationService.submitApplication(request)
    }
}