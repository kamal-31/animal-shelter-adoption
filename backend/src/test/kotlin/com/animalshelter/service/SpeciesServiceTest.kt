package com.animalshelter.service

import com.animalshelter.model.Species
import com.animalshelter.repository.SpeciesRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class SpeciesServiceTest {

    @Mock
    private lateinit var speciesRepository: SpeciesRepository

    private lateinit var speciesService: SpeciesService

    @BeforeEach
    fun setUp() {
        speciesService = SpeciesService(speciesRepository)
    }

    @Nested
    inner class FindAll {

        @Test
        fun `should return all species sorted by name`() {
            val species = listOf(
                Species(id = 1, name = "Dog"),
                Species(id = 2, name = "Cat"),
                Species(id = 3, name = "Bird"),
                Species(id = 4, name = "Rabbit")
            )

            whenever(speciesRepository.findAll()).thenReturn(species)

            val result = speciesService.findAll()

            assertEquals(4, result.size)
            // Verify sorted order
            assertEquals("Bird", result[0].name)
            assertEquals("Cat", result[1].name)
            assertEquals("Dog", result[2].name)
            assertEquals("Rabbit", result[3].name)
        }

        @Test
        fun `should return empty list when no species exist`() {
            whenever(speciesRepository.findAll()).thenReturn(emptyList())

            val result = speciesService.findAll()

            assertEquals(0, result.size)
        }

        @Test
        fun `should return single species`() {
            val species = listOf(Species(id = 1, name = "Dog"))

            whenever(speciesRepository.findAll()).thenReturn(species)

            val result = speciesService.findAll()

            assertEquals(1, result.size)
            assertEquals("Dog", result[0].name)
            assertEquals(1, result[0].id)
        }

        @Test
        fun `should handle species with similar names`() {
            val species = listOf(
                Species(id = 1, name = "Dog"),
                Species(id = 2, name = "Domestic Cat"),
                Species(id = 3, name = "Dog (Small)")
            )

            whenever(speciesRepository.findAll()).thenReturn(species)

            val result = speciesService.findAll()

            assertEquals(3, result.size)
            assertEquals("Dog", result[0].name)
            assertEquals("Dog (Small)", result[1].name)
            assertEquals("Domestic Cat", result[2].name)
        }

        @Test
        fun `should convert species to DTOs correctly`() {
            val species = listOf(
                Species(id = 1, name = "Dog"),
                Species(id = 2, name = "Cat")
            )

            whenever(speciesRepository.findAll()).thenReturn(species)

            val result = speciesService.findAll()

            assertEquals(2, result.size)

            // Verify DTO conversion
            val catDto = result.first { it.name == "Cat" }
            assertEquals(2, catDto.id)
            assertEquals("Cat", catDto.name)

            val dogDto = result.first { it.name == "Dog" }
            assertEquals(1, dogDto.id)
            assertEquals("Dog", dogDto.name)
        }
    }
}