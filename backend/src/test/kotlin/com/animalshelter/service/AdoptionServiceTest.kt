package com.animalshelter.service

import com.animalshelter.dto.CancelAdoptionRequest
import com.animalshelter.dto.ConfirmAdoptionRequest
import com.animalshelter.dto.ReturnPetRequest
import com.animalshelter.exception.AdoptionNotFoundException
import com.animalshelter.exception.BusinessRuleViolationException
import com.animalshelter.exception.PetNotFoundException
import com.animalshelter.model.*
import com.animalshelter.repository.AdoptionHistoryRepository
import com.animalshelter.repository.ApplicantRepository
import com.animalshelter.repository.ApplicationRepository
import com.animalshelter.repository.PetRepository
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
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class AdoptionServiceTest {

    @Mock
    private lateinit var adoptionHistoryRepository: AdoptionHistoryRepository

    @Mock
    private lateinit var applicationRepository: ApplicationRepository

    @Mock
    private lateinit var petRepository: PetRepository

    @Mock
    private lateinit var applicantRepository: ApplicantRepository

    private lateinit var adoptionService: AdoptionService

    private val testSpecies = Species(id = 1, name = "Dog")
    private val testPet = Pet(
        id = 1L,
        name = "Buddy",
        species = testSpecies,
        age = 3,
        status = PetStatus.PENDING
    )
    private val testApplicant = Applicant(
        id = 1L,
        name = "John Doe",
        email = "john@example.com",
        phone = "123-456-7890"
    )
    private val testApplication = Application(
        id = 1L,
        petId = 1L,
        applicantId = 1L,
        reason = "I love dogs",
        status = ApplicationStatus.APPROVED
    )
    private val testAdoption = AdoptionHistory(
        id = 1L,
        petId = 1L,
        applicationId = 1L,
        applicantId = 1L,
        status = AdoptionStatus.PENDING_PICKUP
    )

    @BeforeEach
    fun setUp() {
        adoptionService = AdoptionService(
            adoptionHistoryRepository,
            applicationRepository,
            petRepository,
            applicantRepository
        )
    }

    @Nested
    inner class FindAllForAdmin {

        @Test
        fun `should return all adoptions with filters`() {
            val adoptions = listOf(testAdoption)

            whenever(adoptionHistoryRepository.findByFilters(AdoptionStatus.PENDING_PICKUP, 1L))
                .thenReturn(adoptions)
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicantRepository.findById(1L)).thenReturn(Optional.of(testApplicant))

            val result = adoptionService.findAllForAdmin(AdoptionStatus.PENDING_PICKUP, 1L)

            assertEquals(1, result.size)
            assertEquals("Buddy", result[0].petName)
            assertEquals("John Doe", result[0].applicantName)
            assertEquals(AdoptionStatus.PENDING_PICKUP, result[0].status)
        }

        @Test
        fun `should filter out adoptions with missing pet`() {
            val adoptions = listOf(testAdoption)

            whenever(adoptionHistoryRepository.findByFilters(null, null)).thenReturn(adoptions)
            whenever(petRepository.findById(1L)).thenReturn(Optional.empty())

            val result = adoptionService.findAllForAdmin(null, null)

            assertEquals(0, result.size)
        }

        @Test
        fun `should handle missing applicant gracefully`() {
            val adoptions = listOf(testAdoption)

            whenever(adoptionHistoryRepository.findByFilters(null, null)).thenReturn(adoptions)
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicantRepository.findById(1L)).thenReturn(Optional.empty())

            val result = adoptionService.findAllForAdmin(null, null)

            assertEquals(1, result.size)
            assertEquals("", result[0].applicantName)
            assertEquals("", result[0].applicantEmail)
        }
    }

    @Nested
    inner class ConfirmAdoption {

        @Test
        fun `should confirm adoption and reject other applications`() {
            val request = ConfirmAdoptionRequest(notes = "Pet picked up successfully")
            val otherApplication = Application(
                id = 2L,
                petId = 1L,
                applicantId = 2L,
                reason = "Other applicant",
                status = ApplicationStatus.PENDING
            )

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(testAdoption))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.findByPetIdAndStatusInOrderBySubmittedAtAsc(eq(1L), any()))
                .thenReturn(listOf(testApplication, otherApplication))
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }
            whenever(applicationRepository.save(any<Application>())).thenAnswer { it.getArgument(0) }

            val result = adoptionService.confirmAdoption(1L, request)

            assertEquals(AdoptionStatus.ACTIVE, result.adoptionStatus)
            assertEquals(PetStatus.ADOPTED, result.petStatus)
            assertEquals(1, result.rejectedApplicationCount)

            verify(adoptionHistoryRepository).save(argThat<AdoptionHistory> {
                status == AdoptionStatus.ACTIVE && notes == "Pet picked up successfully"
            })
            verify(petRepository).save(argThat<Pet> { status == PetStatus.ADOPTED })
            verify(applicationRepository).save(argThat<Application> {
                id == 2L && status == ApplicationStatus.REJECTED
            })
        }

        @Test
        fun `should confirm adoption without notes`() {
            val request = ConfirmAdoptionRequest(notes = null)

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(testAdoption))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.findByPetIdAndStatusInOrderBySubmittedAtAsc(eq(1L), any()))
                .thenReturn(listOf(testApplication))
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = adoptionService.confirmAdoption(1L, request)

            assertEquals(AdoptionStatus.ACTIVE, result.adoptionStatus)
            assertEquals(0, result.rejectedApplicationCount)
        }

        @Test
        fun `should throw AdoptionNotFoundException when adoption does not exist`() {
            val request = ConfirmAdoptionRequest(notes = null)

            whenever(adoptionHistoryRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<AdoptionNotFoundException> {
                adoptionService.confirmAdoption(99L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when adoption is not pending pickup`() {
            val activeAdoption = testAdoption.copy(status = AdoptionStatus.ACTIVE)
            val request = ConfirmAdoptionRequest(notes = null)

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(activeAdoption))

            assertThrows<BusinessRuleViolationException> {
                adoptionService.confirmAdoption(1L, request)
            }
        }

        @Test
        fun `should throw PetNotFoundException when pet does not exist`() {
            val request = ConfirmAdoptionRequest(notes = null)

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(testAdoption))
            whenever(petRepository.findById(1L)).thenReturn(Optional.empty())

            assertThrows<PetNotFoundException> {
                adoptionService.confirmAdoption(1L, request)
            }
        }
    }

    @Nested
    inner class CancelAdoption {

        @Test
        fun `should cancel adoption and set pet to available when no other applications`() {
            val request = CancelAdoptionRequest(reason = "Family did not show up")

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(testAdoption))
            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(false)
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { it.getArgument(0) }
            whenever(applicationRepository.save(any<Application>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = adoptionService.cancelAdoption(1L, request)

            assertEquals(AdoptionStatus.CANCELLED, result.adoptionStatus)
            assertEquals(ApplicationStatus.ADOPTION_CANCELLED, result.applicationStatus)
            assertEquals(PetStatus.AVAILABLE, result.petStatus)

            verify(adoptionHistoryRepository).save(argThat<AdoptionHistory> {
                status == AdoptionStatus.CANCELLED && notes == "Family did not show up"
            })
        }

        @Test
        fun `should cancel adoption and set pet to pending when other applications exist`() {
            val request = CancelAdoptionRequest(reason = "Changed their mind")

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(testAdoption))
            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(true)
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { it.getArgument(0) }
            whenever(applicationRepository.save(any<Application>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = adoptionService.cancelAdoption(1L, request)

            assertEquals(AdoptionStatus.CANCELLED, result.adoptionStatus)
            assertEquals(PetStatus.PENDING, result.petStatus)
        }

        @Test
        fun `should throw AdoptionNotFoundException when adoption does not exist`() {
            val request = CancelAdoptionRequest(reason = "Test reason")

            whenever(adoptionHistoryRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<AdoptionNotFoundException> {
                adoptionService.cancelAdoption(99L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when adoption is not pending pickup`() {
            val activeAdoption = testAdoption.copy(status = AdoptionStatus.ACTIVE)
            val request = CancelAdoptionRequest(reason = "Test reason")

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(activeAdoption))

            assertThrows<BusinessRuleViolationException> {
                adoptionService.cancelAdoption(1L, request)
            }
        }
    }

    @Nested
    inner class ReturnPet {

        @Test
        fun `should return pet successfully`() {
            val activeAdoption = testAdoption.copy(status = AdoptionStatus.ACTIVE)
            val adoptedPet = testPet.copy(status = PetStatus.ADOPTED)
            val request = ReturnPetRequest(
                returnReason = "Moving to a place that doesn't allow pets",
                notes = "Pet is healthy"
            )

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(activeAdoption))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(adoptedPet))
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = adoptionService.returnPet(1L, request)

            assertEquals(AdoptionStatus.RETURNED, result.adoptionStatus)
            assertEquals(PetStatus.AVAILABLE, result.petStatus)
            assertNotNull(result.returnedAt)

            verify(adoptionHistoryRepository).save(argThat<AdoptionHistory> {
                status == AdoptionStatus.RETURNED &&
                        returnReason == "Moving to a place that doesn't allow pets" &&
                        returnedAt != null
            })
            verify(petRepository).save(argThat<Pet> { status == PetStatus.AVAILABLE })
        }

        @Test
        fun `should return pet without additional notes`() {
            val activeAdoption = testAdoption.copy(status = AdoptionStatus.ACTIVE)
            val adoptedPet = testPet.copy(status = PetStatus.ADOPTED)
            val request = ReturnPetRequest(
                returnReason = "Family circumstances changed",
                notes = null
            )

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(activeAdoption))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(adoptedPet))
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = adoptionService.returnPet(1L, request)

            assertEquals(AdoptionStatus.RETURNED, result.adoptionStatus)
        }

        @Test
        fun `should throw AdoptionNotFoundException when adoption does not exist`() {
            val request = ReturnPetRequest(returnReason = "Test reason", notes = null)

            whenever(adoptionHistoryRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<AdoptionNotFoundException> {
                adoptionService.returnPet(99L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when adoption is not active`() {
            val pendingAdoption = testAdoption.copy(status = AdoptionStatus.PENDING_PICKUP)
            val request = ReturnPetRequest(returnReason = "Test reason", notes = null)

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(pendingAdoption))

            assertThrows<BusinessRuleViolationException> {
                adoptionService.returnPet(1L, request)
            }
        }

        @Test
        fun `should throw PetNotFoundException when pet does not exist`() {
            val activeAdoption = testAdoption.copy(status = AdoptionStatus.ACTIVE)
            val request = ReturnPetRequest(returnReason = "Test reason", notes = null)

            whenever(adoptionHistoryRepository.findById(1L)).thenReturn(Optional.of(activeAdoption))
            whenever(petRepository.findById(1L)).thenReturn(Optional.empty())

            assertThrows<PetNotFoundException> {
                adoptionService.returnPet(1L, request)
            }
        }
    }
}