package com.animalshelter.repository

import com.animalshelter.model.Species
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpeciesRepository : JpaRepository<Species, Int> {
    fun findByName(name: String): Species?
}