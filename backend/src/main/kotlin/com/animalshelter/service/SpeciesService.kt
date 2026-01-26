package com.animalshelter.service

import com.animalshelter.dto.SpeciesDto
import com.animalshelter.dto.toDto
import com.animalshelter.repository.SpeciesRepository
import org.springframework.stereotype.Service

@Service
class SpeciesService(
    private val speciesRepository: SpeciesRepository
) {

    /**
     * Get all species (reference data)
     */
    fun findAll(): List<SpeciesDto> {
        return speciesRepository.findAll()
            .map { it.toDto() }
            .sortedBy { it.name }
    }
}