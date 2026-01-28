package com.animalshelter.service

import com.animalshelter.dto.CreatePetRequest
import com.animalshelter.dto.UpdatePetRequest
import com.animalshelter.exception.PetNotFoundException
import com.animalshelter.exception.SpeciesNotFoundException
import com.animalshelter.model.Pet
import com.animalshelter.model.PetStatus
import com.animalshelter.model.Species
import com.animalshelter.repository.PetRepository
import com.animalshelter.repository.SpeciesRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class PetServiceTest {

    @Mock
    private lateinit var petRepository: PetRepository

    @Mock
    private lateinit var speciesRepository: SpeciesRepository

    private lateinit var petService: PetService

    private val testSpecies = Species(id = 1, name = "Dog")
    private val testPet = Pet(
        id = 1L,
        name = "Buddy",
        species = testSpecies,
        age = 3,
        imageUrl = "http://example.com/buddy.jpg",
        description = "A friendly dog",
        status = PetStatus.AVAILABLE
    )

    @BeforeEach
    fun setUp() {
        petService = PetService(petRepository, speciesRepository)
    }

    @Nested
    inner class FindAll {

        @Test
        fun `should return all pets when no filters provided`() {
            val pets = listOf(testPet)
            whenever(petRepository.findByDeletedAtIsNull()).thenReturn(pets)
            whenever(petRepository.countPendingApplications(1L)).thenReturn(2)

            val result = petService.findAll(null, null)

            assertEquals(1, result.size)
            assertEquals("Buddy", result[0].name)
            assertEquals(2, result[0].pendingApplicationCount)
            verify(petRepository).findByDeletedAtIsNull()
        }

        @Test
        fun `should filter by status only`() {
            val pets = listOf(testPet)
            whenever(petRepository.findByStatusAndDeletedAtIsNull(PetStatus.AVAILABLE)).thenReturn(pets)
            whenever(petRepository.countPendingApplications(1L)).thenReturn(0)

            val result = petService.findAll(PetStatus.AVAILABLE, null)

            assertEquals(1, result.size)
            verify(petRepository).findByStatusAndDeletedAtIsNull(PetStatus.AVAILABLE)
        }

        @Test
        fun `should filter by species only`() {
            val pets = listOf(testPet)
            whenever(petRepository.findBySpeciesIdAndDeletedAtIsNull(1)).thenReturn(pets)
            whenever(petRepository.countPendingApplications(1L)).thenReturn(0)

            val result = petService.findAll(null, 1)

            assertEquals(1, result.size)
            verify(petRepository).findBySpeciesIdAndDeletedAtIsNull(1)
        }

        @Test
        fun `should filter by both status and species`() {
            val pets = listOf(testPet)
            whenever(petRepository.findByStatusAndSpeciesIdAndDeletedAtIsNull(PetStatus.AVAILABLE, 1))
                .thenReturn(pets)
            whenever(petRepository.countPendingApplications(1L)).thenReturn(0)

            val result = petService.findAll(PetStatus.AVAILABLE, 1)

            assertEquals(1, result.size)
            verify(petRepository).findByStatusAndSpeciesIdAndDeletedAtIsNull(PetStatus.AVAILABLE, 1)
        }

        @Test
        fun `should return empty list when no pets found`() {
            whenever(petRepository.findByDeletedAtIsNull()).thenReturn(emptyList())

            val result = petService.findAll(null, null)

            assertEquals(0, result.size)
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return pet when found`() {
            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(testPet)
            whenever(petRepository.countPendingApplications(1L)).thenReturn(3)

            val result = petService.findById(1L)

            assertEquals("Buddy", result.name)
            assertEquals("Dog", result.species)
            assertEquals(3, result.pendingApplicationCount)
        }

        @Test
        fun `should throw PetNotFoundException when pet not found`() {
            whenever(petRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(null)

            assertThrows<PetNotFoundException> {
                petService.findById(99L)
            }
        }
    }

    @Nested
    inner class Create {

        @Test
        fun `should create pet successfully`() {
            val request = CreatePetRequest(
                name = "Max",
                speciesId = 1,
                age = 2,
                imageUrl = "http://example.com/max.jpg",
                description = "A playful puppy"
            )

            whenever(speciesRepository.findById(1)).thenReturn(Optional.of(testSpecies))
            whenever(petRepository.save(any<Pet>())).thenAnswer { invocation ->
                val pet = invocation.getArgument<Pet>(0)
                pet.copy(id = 2L)
            }

            val result = petService.create(request)

            assertEquals("Max", result.name)
            assertEquals("Dog", result.species)
            assertEquals(2, result.age)
            assertEquals(PetStatus.AVAILABLE, result.status)

            verify(speciesRepository).findById(1)
            verify(petRepository).save(any())
        }

        @Test
        fun `should throw SpeciesNotFoundException when species not found`() {
            val request = CreatePetRequest(
                name = "Max",
                speciesId = 99,
                age = 2,
                imageUrl = null,
                description = null
            )

            whenever(speciesRepository.findById(99)).thenReturn(Optional.empty())

            assertThrows<SpeciesNotFoundException> {
                petService.create(request)
            }

            verify(petRepository, never()).save(any())
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `should update pet with all fields`() {
            val request = UpdatePetRequest(
                name = "Buddy Jr",
                age = 4,
                imageUrl = "http://example.com/buddy-new.jpg",
                description = "An even friendlier dog"
            )

            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(testPet)
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.countPendingApplications(1L)).thenReturn(1)

            val result = petService.update(1L, request)

            assertEquals("Buddy Jr", result.name)
            assertEquals(4, result.age)
            assertEquals("http://example.com/buddy-new.jpg", result.imageUrl)
            assertEquals("An even friendlier dog", result.description)
        }

        @Test
        fun `should update pet with partial fields`() {
            val request = UpdatePetRequest(
                name = "Buddy Updated",
                age = null,
                imageUrl = null,
                description = null
            )

            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(testPet)
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.countPendingApplications(1L)).thenReturn(0)

            val result = petService.update(1L, request)

            assertEquals("Buddy Updated", result.name)
            assertEquals(3, result.age) // Original age preserved
        }

        @Test
        fun `should throw PetNotFoundException when updating non-existent pet`() {
            val request = UpdatePetRequest(name = "Test", age = null, imageUrl = null, description = null)
            whenever(petRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(null)

            assertThrows<PetNotFoundException> {
                petService.update(99L, request)
            }

            verify(petRepository, never()).save(any())
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should soft delete pet successfully`() {
            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(testPet)
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            petService.delete(1L)

            verify(petRepository).save(argThat<Pet> { deletedAt != null })
        }

        @Test
        fun `should throw PetNotFoundException when deleting non-existent pet`() {
            whenever(petRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(null)

            assertThrows<PetNotFoundException> {
                petService.delete(99L)
            }

            verify(petRepository, never()).save(any())
        }
    }

    @Nested
    inner class FindAllForAdmin {

        @Test
        fun `should return all pets including deleted when includeDeleted is true`() {
            val deletedPet = testPet.copy(id = 2L, name = "Deleted Pet").apply { softDelete() }
            val pets = listOf(testPet, deletedPet)

            whenever(petRepository.findAll()).thenReturn(pets)
            whenever(petRepository.countPendingApplications(any())).thenReturn(0)

            val result = petService.findAllForAdmin(includeDeleted = true)

            assertEquals(2, result.size)
            verify(petRepository).findAll()
            verify(petRepository, never()).findByDeletedAtIsNull()
        }

        @Test
        fun `should return only non-deleted pets when includeDeleted is false`() {
            val pets = listOf(testPet)

            whenever(petRepository.findByDeletedAtIsNull()).thenReturn(pets)
            whenever(petRepository.countPendingApplications(1L)).thenReturn(0)

            val result = petService.findAllForAdmin(includeDeleted = false)

            assertEquals(1, result.size)
            assertNull(result[0].deletedAt)
            verify(petRepository).findByDeletedAtIsNull()
            verify(petRepository, never()).findAll()
        }
    }
}