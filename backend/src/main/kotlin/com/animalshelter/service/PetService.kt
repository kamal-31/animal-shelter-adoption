package com.animalshelter.service

import com.animalshelter.dto.*
import com.animalshelter.exception.PetNotFoundException
import com.animalshelter.exception.SpeciesNotFoundException
import com.animalshelter.model.Pet
import com.animalshelter.model.PetStatus
import com.animalshelter.repository.PetRepository
import com.animalshelter.repository.SpeciesRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PetService(
    private val petRepository: PetRepository,
    private val speciesRepository: SpeciesRepository
) {

    /**
     * Get all pets with optional filters (excluding deleted)
     */
    fun findAll(status: PetStatus?, speciesId: Int?): List<PetDto> {
        val pets = when {
            status != null && speciesId != null ->
                petRepository.findByStatusAndSpeciesIdAndDeletedAtIsNull(status, speciesId)

            status != null ->
                petRepository.findByStatusAndDeletedAtIsNull(status)

            speciesId != null ->
                petRepository.findBySpeciesIdAndDeletedAtIsNull(speciesId)

            else ->
                petRepository.findByDeletedAtIsNull()
        }

        return pets.map { pet ->
            val pendingCount = petRepository.countPendingApplications(pet.id!!)
            pet.toDto(pendingCount)
        }
    }

    /**
     * Get pet by ID (excluding deleted)
     */
    fun findById(id: Long): PetDto {
        val pet = petRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PetNotFoundException(id)

        val pendingCount = petRepository.countPendingApplications(id)
        return pet.toDto(pendingCount)
    }

    /**
     * Create new pet (admin)
     */
    @Transactional
    fun create(request: CreatePetRequest): PetDto {
        val species = speciesRepository.findById(request.speciesId)
            .orElseThrow { SpeciesNotFoundException(request.speciesId) }

        val pet = Pet(
            name = request.name,
            species = species,
            age = request.age,
            imageUrl = request.imageUrl,
            description = request.description,
            status = PetStatus.AVAILABLE
        )

        val savedPet = petRepository.save(pet)
        return savedPet.toDto()
    }

    /**
     * Update pet details (admin)
     */
    @Transactional
    fun update(id: Long, request: UpdatePetRequest): PetDto {
        val pet = petRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PetNotFoundException(id)

        // Update only provided fields
        request.name?.let { pet.name = it }
        request.age?.let { pet.age = it }
        request.imageUrl?.let { pet.imageUrl = it }
        request.description?.let { pet.description = it }

        val updatedPet = petRepository.save(pet)
        val pendingCount = petRepository.countPendingApplications(id)
        return updatedPet.toDto(pendingCount)
    }

    /**
     * Soft delete pet (admin)
     */
    @Transactional
    fun delete(id: Long) {
        val pet = petRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PetNotFoundException(id)

        pet.softDelete()
        petRepository.save(pet)
    }

    /**
     * Get all pets including deleted (admin only)
     */
    fun findAllForAdmin(includeDeleted: Boolean): List<PetDto> {
        val pets = if (includeDeleted) {
            petRepository.findAll()
        } else {
            petRepository.findByDeletedAtIsNull()
        }

        return pets.map { pet ->
            val pendingCount = petRepository.countPendingApplications(pet.id!!)
            pet.toDto(pendingCount)
        }
    }
}