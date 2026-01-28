package com.animalshelter.integration

import com.animalshelter.dto.SpeciesDto
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SpeciesIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `GET api-species should return all seeded species sorted by name`() {
        // When - Fire real HTTP REST call to endpoint
        val response = restTemplate.exchange(
            "/api/species",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<SpeciesDto>>() {}
        )

        // Then - Validate HTTP response
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)

        val species: List<SpeciesDto> = response.body!!

        // Validate count matches seeded data (V7__seed_initial_species.sql)
        assertEquals(7, species.size, "Expected 7 species from seed data")

        // Validate sorted by name (as per SpeciesService.findAll())
        val names = species.map { it.name }
        assertEquals(names.sorted(), names, "Species should be sorted alphabetically")

        // Validate expected species are present
        val expectedSpecies = listOf("Bird", "Cat", "Dog", "Guinea Pig", "Hamster", "Other", "Rabbit")
        assertEquals(expectedSpecies, names, "Species names should match seeded data")

        // Validate each species has an ID
        species.forEach { speciesDto ->
            assertNotNull(speciesDto.id, "Each species should have an ID")
            assertTrue(speciesDto.id > 0, "Species ID should be positive")
        }
    }

    @Test
    fun `GET api-species should return consistent IDs across calls`() {
        // When - Make two real HTTP calls
        val response1 = restTemplate.exchange(
            "/api/species",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<SpeciesDto>>() {}
        )
        val response2 = restTemplate.exchange(
            "/api/species",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<SpeciesDto>>() {}
        )

        // Then - Validate responses
        assertEquals(HttpStatus.OK, response1.statusCode)
        assertEquals(HttpStatus.OK, response2.statusCode)

        val species1: List<SpeciesDto> = response1.body!!
        val species2: List<SpeciesDto> = response2.body!!

        assertEquals(species1.size, species2.size)

        // Verify same IDs across calls
        val ids1 = species1.map { it.id }.toSet()
        val ids2 = species2.map { it.id }.toSet()
        assertEquals(ids1, ids2, "Species IDs should be consistent across calls")
    }
}