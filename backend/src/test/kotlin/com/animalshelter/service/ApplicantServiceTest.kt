package com.animalshelter.service

import com.animalshelter.model.Applicant
import com.animalshelter.repository.ApplicantRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class ApplicantServiceTest {

    @Mock
    private lateinit var applicantRepository: ApplicantRepository

    private lateinit var applicantService: ApplicantService

    private val testApplicant = Applicant(
        id = 1L,
        name = "John Doe",
        email = "john@example.com",
        phone = "123-456-7890"
    )

    @BeforeEach
    fun setUp() {
        applicantService = ApplicantService(applicantRepository)
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return applicant when found`() {
            whenever(applicantRepository.findById(1L)).thenReturn(Optional.of(testApplicant))

            val result = applicantService.findById(1L)

            assertNotNull(result)
            assertEquals("John Doe", result.name)
            assertEquals("john@example.com", result.email)
        }

        @Test
        fun `should return null when applicant not found`() {
            whenever(applicantRepository.findById(99L)).thenReturn(Optional.empty())

            val result = applicantService.findById(99L)

            assertNull(result)
        }
    }

    @Nested
    inner class FindOrCreate {

        @Test
        fun `should return existing applicant when email matches`() {
            whenever(applicantRepository.findByEmail("john@example.com")).thenReturn(testApplicant)

            val result = applicantService.findOrCreate(
                name = "John Doe",
                email = "john@example.com",
                phone = "123-456-7890"
            )

            assertEquals(1L, result.id)
            assertEquals("John Doe", result.name)
            verify(applicantRepository, never()).save(any())
        }

        @Test
        fun `should update name when existing applicant has different name`() {
            whenever(applicantRepository.findByEmail("john@example.com")).thenReturn(testApplicant)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { it.getArgument(0) }

            val result = applicantService.findOrCreate(
                name = "John D. Doe",
                email = "john@example.com",
                phone = "123-456-7890"
            )

            assertEquals("John D. Doe", result.name)
            verify(applicantRepository).save(argThat<Applicant> { name == "John D. Doe" })
        }

        @Test
        fun `should update phone when existing applicant has different phone`() {
            whenever(applicantRepository.findByEmail("john@example.com")).thenReturn(testApplicant)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { it.getArgument(0) }

            val result = applicantService.findOrCreate(
                name = "John Doe",
                email = "john@example.com",
                phone = "999-999-9999"
            )

            assertEquals("999-999-9999", result.phone)
            verify(applicantRepository).save(argThat<Applicant> { phone == "999-999-9999" })
        }

        @Test
        fun `should update both name and phone when both differ`() {
            whenever(applicantRepository.findByEmail("john@example.com")).thenReturn(testApplicant)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { it.getArgument(0) }

            val result = applicantService.findOrCreate(
                name = "Jonathan Doe",
                email = "john@example.com",
                phone = "555-555-5555"
            )

            assertEquals("Jonathan Doe", result.name)
            assertEquals("555-555-5555", result.phone)
            verify(applicantRepository).save(any())
        }

        @Test
        fun `should create new applicant when email not found`() {
            whenever(applicantRepository.findByEmail("new@example.com")).thenReturn(null)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { invocation ->
                val applicant = invocation.getArgument<Applicant>(0)
                applicant.copy(id = 2L)
            }

            val result = applicantService.findOrCreate(
                name = "Jane Doe",
                email = "new@example.com",
                phone = "111-222-3333"
            )

            assertEquals(2L, result.id)
            assertEquals("Jane Doe", result.name)
            assertEquals("new@example.com", result.email)
            assertEquals("111-222-3333", result.phone)

            verify(applicantRepository).save(argThat<Applicant> {
                name == "Jane Doe" && email == "new@example.com" && phone == "111-222-3333"
            })
        }

        @Test
        fun `should create new applicant without phone`() {
            whenever(applicantRepository.findByEmail("nophone@example.com")).thenReturn(null)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { invocation ->
                val applicant = invocation.getArgument<Applicant>(0)
                applicant.copy(id = 3L)
            }

            val result = applicantService.findOrCreate(
                name = "No Phone",
                email = "nophone@example.com",
                phone = null
            )

            assertEquals(3L, result.id)
            assertNull(result.phone)

            verify(applicantRepository).save(argThat<Applicant> { phone == null })
        }

        @Test
        fun `should update phone from null to value`() {
            val applicantWithoutPhone = testApplicant.copy(phone = null)
            whenever(applicantRepository.findByEmail("john@example.com")).thenReturn(applicantWithoutPhone)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { it.getArgument(0) }

            val result = applicantService.findOrCreate(
                name = "John Doe",
                email = "john@example.com",
                phone = "123-456-7890"
            )

            assertEquals("123-456-7890", result.phone)
            verify(applicantRepository).save(any())
        }

        @Test
        fun `should update phone from value to null`() {
            whenever(applicantRepository.findByEmail("john@example.com")).thenReturn(testApplicant)
            whenever(applicantRepository.save(any<Applicant>())).thenAnswer { it.getArgument(0) }

            val result = applicantService.findOrCreate(
                name = "John Doe",
                email = "john@example.com",
                phone = null
            )

            assertNull(result.phone)
            verify(applicantRepository).save(argThat<Applicant> { phone == null })
        }
    }
}