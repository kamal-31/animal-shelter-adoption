package com.animalshelter.integration

import com.animalshelter.dto.ApproveApplicationRequest
import com.animalshelter.dto.CreatePetRequest
import com.animalshelter.dto.SubmitApplicationRequest
import com.animalshelter.dto.UpdatePetRequest
import com.animalshelter.model.*
import com.animalshelter.repository.*
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PetAndApplicationIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var speciesRepository: SpeciesRepository

    @Autowired
    private lateinit var applicantRepository: ApplicantRepository

    @Autowired
    private lateinit var applicationRepository: ApplicationRepository

    @Autowired
    private lateinit var adoptionHistoryRepository: AdoptionHistoryRepository

    private val objectMapper: JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()

    @BeforeEach
    fun cleanupDatabase() {
        // Delete in correct order to respect foreign key constraints
        adoptionHistoryRepository.deleteAll()
        applicationRepository.deleteAll()
        petRepository.deleteAll()
        applicantRepository.deleteAll()
        // Note: Species are NOT deleted - they are seeded by Flyway and shared by all tests
    }

    private fun loadJsonFile(path: String): JSONObject {
        val resource = ClassPathResource("expected-data/$path")
        val content = resource.inputStream.bufferedReader().readText()
        return JSONObject(content)
    }

    // ==================== POST /api/admin/pets (Create Pet) ====================

    @Test
    fun `POST api-admin-pets should create pet and persist to database`() {
        // Load setup data
        val setupJson = loadJsonFile("create-pet-setup.json")
        val requestJson = setupJson.getJSONObject("request")

        val request = CreatePetRequest(
            name = requestJson.getString("name"),
            speciesId = requestJson.getInt("speciesId"),
            age = requestJson.getInt("age"),
            imageUrl = requestJson.optString("imageUrl", null),
            description = requestJson.optString("description", null)
        )

        // Execute REST call
        val response = restTemplate.postForEntity(
            "/api/admin/pets",
            request,
            String::class.java
        )

        // Verify HTTP response
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)

        // Load expected data
        val expectedJson = loadJsonFile("create-pet-expected.json")
        val expectedPet = expectedJson.getJSONObject("pet")

        // Query DB and verify
        val pets = petRepository.findByDeletedAtIsNull()
        val createdPet = pets.find { it.name == request.name }
        assertNotNull(createdPet, "Pet should exist in database")

        val actualPetJson = JSONObject().apply {
            put("name", createdPet.name)
            put("speciesId", createdPet.species.id)
            put("age", createdPet.age)
            put("imageUrl", createdPet.imageUrl)
            put("description", createdPet.description)
            put("status", createdPet.status.name)
        }

        JSONAssert.assertEquals(expectedPet, actualPetJson, JSONCompareMode.LENIENT)
    }

    // ==================== PUT /api/admin/pets/{id} (Update Pet) ====================

    @Test
    fun `PUT api-admin-pets-id should update pet and persist to database`() {
        // Load setup data
        val setupJson = loadJsonFile("update-pet-setup.json")
        val petSetup = setupJson.getJSONObject("pet")
        val requestJson = setupJson.getJSONObject("request")

        // Insert prerequisite data
        val species = speciesRepository.findById(petSetup.getInt("speciesId")).orElseThrow()
        val existingPet = petRepository.save(
            Pet(
                name = petSetup.getString("name"),
                species = species,
                age = petSetup.getInt("age"),
                imageUrl = petSetup.optString("imageUrl", null),
                description = petSetup.optString("description", null),
                status = PetStatus.valueOf(petSetup.getString("status"))
            )
        )

        val updateRequest = UpdatePetRequest(
            name = requestJson.optString("name", null),
            age = if (requestJson.has("age")) requestJson.getInt("age") else null,
            imageUrl = requestJson.optString("imageUrl", null),
            description = requestJson.optString("description", null)
        )

        // Execute REST call
        val response = restTemplate.exchange(
            "/api/admin/pets/${existingPet.id}",
            HttpMethod.PUT,
            HttpEntity(updateRequest),
            String::class.java
        )

        // Verify HTTP response
        assertEquals(HttpStatus.OK, response.statusCode)

        // Load expected data
        val expectedJson = loadJsonFile("update-pet-expected.json")
        val expectedPet = expectedJson.getJSONObject("pet")

        // Query DB and verify
        val updatedPet = petRepository.findByIdAndDeletedAtIsNull(existingPet.id!!)
        assertNotNull(updatedPet, "Updated pet should exist in database")

        val actualPetJson = JSONObject().apply {
            put("name", updatedPet.name)
            put("speciesId", updatedPet.species.id)
            put("age", updatedPet.age)
            put("imageUrl", updatedPet.imageUrl)
            put("description", updatedPet.description)
            put("status", updatedPet.status.name)
        }

        JSONAssert.assertEquals(expectedPet, actualPetJson, JSONCompareMode.LENIENT)
    }

    // ==================== POST /api/applications (Submit Application) ====================

    @Test
    fun `POST api-applications should create application and applicant in database`() {
        // Load setup data
        val setupJson = loadJsonFile("submit-application-setup.json")
        val petSetup = setupJson.getJSONObject("pet")
        val requestJson = setupJson.getJSONObject("request")

        // Insert prerequisite data (pet)
        val species = speciesRepository.findById(petSetup.getInt("speciesId")).orElseThrow()
        val pet = petRepository.save(
            Pet(
                name = petSetup.getString("name"),
                species = species,
                age = petSetup.getInt("age"),
                imageUrl = petSetup.optString("imageUrl", null),
                description = petSetup.optString("description", null),
                status = PetStatus.valueOf(petSetup.getString("status"))
            )
        )

        val submitRequest = SubmitApplicationRequest(
            petId = pet.id!!,
            applicantName = requestJson.getString("applicantName"),
            email = requestJson.getString("email"),
            phone = requestJson.optString("phone", null),
            reason = requestJson.getString("reason")
        )

        // Execute REST call
        val response = restTemplate.postForEntity(
            "/api/applications",
            submitRequest,
            String::class.java
        )

        // Verify HTTP response
        assertEquals(HttpStatus.CREATED, response.statusCode)

        // Load expected data
        val expectedJson = loadJsonFile("submit-application-expected.json")
        val expectedApplicant = expectedJson.getJSONObject("applicant")
        val expectedApplication = expectedJson.getJSONObject("application")
        val expectedPet = expectedJson.getJSONObject("pet")

        // Verify Applicant in DB
        val applicant = applicantRepository.findByEmail(submitRequest.email)
        assertNotNull(applicant, "Applicant should exist in database")

        val actualApplicantJson = JSONObject().apply {
            put("name", applicant.name)
            put("email", applicant.email)
            put("phone", applicant.phone)
        }
        JSONAssert.assertEquals(expectedApplicant, actualApplicantJson, JSONCompareMode.LENIENT)

        // Verify Application in DB
        val applications = applicationRepository.findByPetIdOrderBySubmittedAtAsc(pet.id!!)
        assertEquals(1, applications.size, "Should have one application")
        val application = applications.first()

        val actualApplicationJson = JSONObject().apply {
            put("reason", application.reason)
            put("status", application.status.name)
        }
        JSONAssert.assertEquals(expectedApplication, actualApplicationJson, JSONCompareMode.LENIENT)

        // Verify Pet status changed in DB
        val updatedPet = petRepository.findByIdAndDeletedAtIsNull(pet.id!!)
        assertNotNull(updatedPet)

        val actualPetJson = JSONObject().apply {
            put("name", updatedPet.name)
            put("status", updatedPet.status.name)
        }
        JSONAssert.assertEquals(expectedPet, actualPetJson, JSONCompareMode.LENIENT)
    }

    // ==================== POST /api/admin/applications/{id}/approve ====================

    @Test
    fun `POST api-admin-applications-id-approve should approve application and create adoption history`() {
        // Load setup data
        val setupJson = loadJsonFile("approve-application-setup.json")
        val petSetup = setupJson.getJSONObject("pet")
        val applicantSetup = setupJson.getJSONObject("applicant")
        val applicationSetup = setupJson.getJSONObject("application")
        val requestJson = setupJson.getJSONObject("request")

        // Insert prerequisite data
        val species = speciesRepository.findById(petSetup.getInt("speciesId")).orElseThrow()
        val pet = petRepository.save(
            Pet(
                name = petSetup.getString("name"),
                species = species,
                age = petSetup.getInt("age"),
                imageUrl = petSetup.optString("imageUrl", null),
                description = petSetup.optString("description", null),
                status = PetStatus.valueOf(petSetup.getString("status"))
            )
        )

        val applicant = applicantRepository.save(
            Applicant(
                name = applicantSetup.getString("name"),
                email = applicantSetup.getString("email"),
                phone = applicantSetup.optString("phone", null)
            )
        )

        val application = applicationRepository.save(
            Application(
                petId = pet.id!!,
                applicantId = applicant.id!!,
                reason = applicationSetup.getString("reason"),
                status = ApplicationStatus.valueOf(applicationSetup.getString("status"))
            )
        )

        val approveRequest = ApproveApplicationRequest(
            reviewedBy = requestJson.optString("reviewedBy", null)
        )

        // Execute REST call
        val response = restTemplate.postForEntity(
            "/api/admin/applications/${application.id}/approve",
            approveRequest,
            String::class.java
        )

        // Verify HTTP response
        assertEquals(HttpStatus.OK, response.statusCode)

        // Load expected data
        val expectedJson = loadJsonFile("approve-application-expected.json")
        val expectedApplication = expectedJson.getJSONObject("application")
        val expectedAdoption = expectedJson.getJSONObject("adoptionHistory")
        val expectedPet = expectedJson.getJSONObject("pet")

        // Verify Application status in DB
        val updatedApplication = applicationRepository.findById(application.id!!).orElseThrow()
        val actualApplicationJson = JSONObject().apply {
            put("status", updatedApplication.status.name)
            put("reviewedBy", updatedApplication.reviewedBy)
        }
        JSONAssert.assertEquals(expectedApplication, actualApplicationJson, JSONCompareMode.LENIENT)

        // Verify AdoptionHistory created in DB
        val adoptionHistory = adoptionHistoryRepository.findByApplicationId(application.id!!)
        assertNotNull(adoptionHistory, "Adoption history should be created")

        val actualAdoptionJson = JSONObject().apply {
            put("status", adoptionHistory.status.name)
        }
        JSONAssert.assertEquals(expectedAdoption, actualAdoptionJson, JSONCompareMode.LENIENT)

        // Verify Pet status in DB (should remain PENDING until pickup confirmed)
        val updatedPet = petRepository.findByIdAndDeletedAtIsNull(pet.id!!)
        assertNotNull(updatedPet)

        val actualPetJson = JSONObject().apply {
            put("name", updatedPet.name)
            put("status", updatedPet.status.name)
        }
        JSONAssert.assertEquals(expectedPet, actualPetJson, JSONCompareMode.LENIENT)
    }
}
