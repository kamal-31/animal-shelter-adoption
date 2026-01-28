package com.animalshelter.service

import com.animalshelter.dto.ApproveApplicationRequest
import com.animalshelter.dto.RejectApplicationRequest
import com.animalshelter.dto.SubmitApplicationRequest
import com.animalshelter.exception.ApplicationNotFoundException
import com.animalshelter.exception.BusinessRuleViolationException
import com.animalshelter.exception.DuplicateApplicationException
import com.animalshelter.exception.PetNotFoundException
import com.animalshelter.model.*
import com.animalshelter.repository.AdoptionHistoryRepository
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
class ApplicationServiceTest {

    @Mock
    private lateinit var applicationRepository: ApplicationRepository

    @Mock
    private lateinit var petRepository: PetRepository

    @Mock
    private lateinit var applicantService: ApplicantService

    @Mock
    private lateinit var adoptionHistoryRepository: AdoptionHistoryRepository

    private lateinit var applicationService: ApplicationService

    private val testSpecies = Species(id = 1, name = "Dog")
    private val testPet = Pet(
        id = 1L,
        name = "Buddy",
        species = testSpecies,
        age = 3,
        status = PetStatus.AVAILABLE
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
        reason = "I love dogs and have a big yard",
        status = ApplicationStatus.PENDING
    )

    @BeforeEach
    fun setUp() {
        applicationService = ApplicationService(
            applicationRepository,
            petRepository,
            applicantService,
            adoptionHistoryRepository
        )
    }

    @Nested
    inner class SubmitApplication {

        @Test
        fun `should submit application successfully for available pet`() {
            val request = SubmitApplicationRequest(
                petId = 1L,
                applicantName = "John Doe",
                email = "john@example.com",
                phone = "123-456-7890",
                reason = "I love dogs and have a big yard for them to play in"
            )

            whenever(applicantService.findOrCreate(any(), any(), any())).thenReturn(testApplicant)
            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(testPet)
            whenever(applicationRepository.existsByPetIdAndApplicantId(1L, 1L)).thenReturn(false)
            whenever(applicationRepository.save(any<Application>())).thenAnswer { invocation ->
                val app = invocation.getArgument<Application>(0)
                app.copy(id = 1L)
            }
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = applicationService.submitApplication(request)

            assertEquals(1L, result.id)
            assertEquals("Buddy", result.petName)
            assertEquals("John Doe", result.applicantName)
            assertEquals(ApplicationStatus.PENDING, result.status)
            assertNotNull(result.message)

            verify(petRepository).save(argThat<Pet> { status == PetStatus.PENDING })
        }

        @Test
        fun `should submit application without changing pet status when already pending`() {
            val pendingPet = testPet.copy(status = PetStatus.PENDING)
            val request = SubmitApplicationRequest(
                petId = 1L,
                applicantName = "Jane Doe",
                email = "jane@example.com",
                phone = null,
                reason = "I also love dogs and want to give them a home"
            )

            whenever(applicantService.findOrCreate(any(), any(), anyOrNull())).thenReturn(
                testApplicant.copy(id = 2L, email = "jane@example.com")
            )
            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(pendingPet)
            whenever(applicationRepository.existsByPetIdAndApplicantId(1L, 2L)).thenReturn(false)
            whenever(applicationRepository.save(any<Application>())).thenAnswer { invocation ->
                val app = invocation.getArgument<Application>(0)
                app.copy(id = 2L)
            }

            val result = applicationService.submitApplication(request)

            assertEquals(ApplicationStatus.PENDING, result.status)
            verify(petRepository, never()).save(any())
        }

        @Test
        fun `should throw PetNotFoundException when pet does not exist`() {
            val request = SubmitApplicationRequest(
                petId = 99L,
                applicantName = "John Doe",
                email = "john@example.com",
                phone = null,
                reason = "I want to adopt this pet"
            )

            whenever(applicantService.findOrCreate(any(), any(), anyOrNull())).thenReturn(testApplicant)
            whenever(petRepository.findByIdAndDeletedAtIsNull(99L)).thenReturn(null)

            assertThrows<PetNotFoundException> {
                applicationService.submitApplication(request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when pet is already adopted`() {
            val adoptedPet = testPet.copy(status = PetStatus.ADOPTED)
            val request = SubmitApplicationRequest(
                petId = 1L,
                applicantName = "John Doe",
                email = "john@example.com",
                phone = null,
                reason = "I want this adopted pet"
            )

            whenever(applicantService.findOrCreate(any(), any(), anyOrNull())).thenReturn(testApplicant)
            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(adoptedPet)

            assertThrows<BusinessRuleViolationException> {
                applicationService.submitApplication(request)
            }
        }

        @Test
        fun `should throw DuplicateApplicationException when applicant already applied`() {
            val request = SubmitApplicationRequest(
                petId = 1L,
                applicantName = "John Doe",
                email = "john@example.com",
                phone = null,
                reason = "I want to apply again"
            )

            whenever(applicantService.findOrCreate(any(), any(), anyOrNull())).thenReturn(testApplicant)
            whenever(petRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(testPet)
            whenever(applicationRepository.existsByPetIdAndApplicantId(1L, 1L)).thenReturn(true)

            assertThrows<DuplicateApplicationException> {
                applicationService.submitApplication(request)
            }
        }
    }

    @Nested
    inner class FindAllForAdmin {

        @Test
        fun `should return all applications with filters`() {
            val applications = listOf(testApplication)

            whenever(applicationRepository.findByFilters(ApplicationStatus.PENDING, 1L))
                .thenReturn(applications)
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicantService.findById(1L)).thenReturn(testApplicant)

            val result = applicationService.findAllForAdmin(ApplicationStatus.PENDING, 1L)

            assertEquals(1, result.size)
            assertEquals("Buddy", result[0].petName)
            assertEquals("John Doe", result[0].applicantName)
            assertEquals(ApplicationStatus.PENDING, result[0].status)
        }

        @Test
        fun `should filter out applications with missing pet or applicant`() {
            val applications = listOf(testApplication)

            whenever(applicationRepository.findByFilters(null, null)).thenReturn(applications)
            whenever(petRepository.findById(1L)).thenReturn(Optional.empty())

            val result = applicationService.findAllForAdmin(null, null)

            assertEquals(0, result.size)
        }
    }

    @Nested
    inner class ApproveApplication {

        @Test
        fun `should approve pending application successfully`() {
            val request = ApproveApplicationRequest(reviewedBy = "Admin User")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(false)
            whenever(adoptionHistoryRepository.existsByPetIdAndStatus(1L, AdoptionStatus.PENDING_PICKUP))
                .thenReturn(false)
            whenever(adoptionHistoryRepository.existsByPetIdAndStatus(1L, AdoptionStatus.ACTIVE))
                .thenReturn(false)
            whenever(applicationRepository.save(any<Application>())).thenAnswer { it.getArgument(0) }
            whenever(adoptionHistoryRepository.save(any<AdoptionHistory>())).thenAnswer { invocation ->
                val history = invocation.getArgument<AdoptionHistory>(0)
                history.copy(id = 1L)
            }

            val result = applicationService.approveApplication(1L, request)

            assertEquals(ApplicationStatus.APPROVED, result.status)
            assertEquals(AdoptionStatus.PENDING_PICKUP, result.adoptionStatus)
            assertNotNull(result.adoptionHistoryId)

            verify(applicationRepository).save(argThat<Application> {
                status == ApplicationStatus.APPROVED && reviewedBy == "Admin User"
            })
        }

        @Test
        fun `should throw ApplicationNotFoundException when application does not exist`() {
            val request = ApproveApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<ApplicationNotFoundException> {
                applicationService.approveApplication(99L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when application is not pending`() {
            val approvedApplication = testApplication.copy(status = ApplicationStatus.APPROVED)
            val request = ApproveApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(approvedApplication))

            assertThrows<BusinessRuleViolationException> {
                applicationService.approveApplication(1L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when pet is already adopted`() {
            val adoptedPet = testPet.copy(status = PetStatus.ADOPTED)
            val request = ApproveApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(adoptedPet))

            assertThrows<BusinessRuleViolationException> {
                applicationService.approveApplication(1L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when pet already has approved application`() {
            val request = ApproveApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(true)

            assertThrows<BusinessRuleViolationException> {
                applicationService.approveApplication(1L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when pet has active adoption`() {
            val request = ApproveApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(testPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(false)
            whenever(adoptionHistoryRepository.existsByPetIdAndStatus(1L, AdoptionStatus.PENDING_PICKUP))
                .thenReturn(true)

            assertThrows<BusinessRuleViolationException> {
                applicationService.approveApplication(1L, request)
            }
        }
    }

    @Nested
    inner class RejectApplication {

        @Test
        fun `should reject pending application and keep pet pending when other applications exist`() {
            val pendingPet = testPet.copy(status = PetStatus.PENDING)
            val request = RejectApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(applicationRepository.save(any<Application>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(pendingPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(true)

            val result = applicationService.rejectApplication(1L, request)

            assertEquals(ApplicationStatus.REJECTED, result.status)
            assertEquals(PetStatus.PENDING, result.petStatus)
            verify(petRepository, never()).save(any())
        }

        @Test
        fun `should reject application and set pet to available when no other applications`() {
            val pendingPet = testPet.copy(status = PetStatus.PENDING)
            val request = RejectApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication))
            whenever(applicationRepository.save(any<Application>())).thenAnswer { it.getArgument(0) }
            whenever(petRepository.findById(1L)).thenReturn(Optional.of(pendingPet))
            whenever(applicationRepository.existsByPetIdAndStatusIn(eq(1L), any())).thenReturn(false)
            whenever(petRepository.save(any<Pet>())).thenAnswer { it.getArgument(0) }

            val result = applicationService.rejectApplication(1L, request)

            assertEquals(ApplicationStatus.REJECTED, result.status)
            assertEquals(PetStatus.AVAILABLE, result.petStatus)
            verify(petRepository).save(argThat<Pet> { status == PetStatus.AVAILABLE })
        }

        @Test
        fun `should throw ApplicationNotFoundException when application does not exist`() {
            val request = RejectApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(99L)).thenReturn(Optional.empty())

            assertThrows<ApplicationNotFoundException> {
                applicationService.rejectApplication(99L, request)
            }
        }

        @Test
        fun `should throw BusinessRuleViolationException when application is not pending`() {
            val approvedApplication = testApplication.copy(status = ApplicationStatus.APPROVED)
            val request = RejectApplicationRequest(reviewedBy = "Admin")

            whenever(applicationRepository.findById(1L)).thenReturn(Optional.of(approvedApplication))

            assertThrows<BusinessRuleViolationException> {
                applicationService.rejectApplication(1L, request)
            }
        }
    }
}