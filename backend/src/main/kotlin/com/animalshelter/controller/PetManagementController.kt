package com.animalshelter.controller

import com.animalshelter.dto.CreatePetRequest
import com.animalshelter.dto.ImageUploadResponse
import com.animalshelter.dto.PetDto
import com.animalshelter.dto.UpdatePetRequest
import com.animalshelter.service.ImageService
import com.animalshelter.service.PetService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = ["http://localhost:5173"])
class PetManagementController(
    private val petService: PetService,
    private val imageService: ImageService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    // ==================== Pets ====================

    /**
     * GET /api/admin/pets
     * List all pets (including deleted)
     */
    @GetMapping("/pets")
    fun getPets(
        @RequestParam(required = false, defaultValue = "false") includeDeleted: Boolean
    ): List<PetDto> {
        log.debug("Fetching pets, includeDeleted={}", includeDeleted)
        return petService.findAllForAdmin(includeDeleted)
    }

    /**
     * POST /api/admin/pets
     * Create new pet
     */
    @PostMapping("/pets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPet(
        @RequestBody @Valid request: CreatePetRequest
    ): PetDto {
        log.info("Creating pet name={}, speciesId={}", request.name, request.speciesId)
        return petService.create(request)
    }

    /**
     * PUT /api/admin/pets/{id}
     * Update pet
     */
    @PutMapping("/pets/{id}")
    fun updatePet(
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdatePetRequest
    ): PetDto {
        log.info("Updating pet id={}", id)
        return petService.update(id, request)
    }

    /**
     * DELETE /api/admin/pets/{id}
     * Soft delete pet
     */
    @DeleteMapping("/pets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePet(@PathVariable id: Long) {
        log.info("Deleting pet id={}", id)
        petService.delete(id)
    }

    // ==================== Images ====================

    /**
     * POST /api/admin/images
     * Upload image to S3
     */
    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadImage(
        @RequestParam("image") file: MultipartFile
    ): ImageUploadResponse {
        log.info("Uploading image, filename={}, size={}", file.originalFilename, file.size)
        return imageService.uploadImage(file)
    }
}
