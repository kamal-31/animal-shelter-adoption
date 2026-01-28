package com.animalshelter.service

import com.animalshelter.exception.ImageUploadException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.lang.reflect.Field
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class ImageServiceTest {

    @Mock
    private lateinit var s3Client: S3Client

    @Mock
    private lateinit var multipartFile: MultipartFile

    private lateinit var imageService: ImageService

    @BeforeEach
    fun setUp() {
        imageService = ImageService(
            endpoint = "http://localhost:4566",
            region = "us-east-1",
            bucketName = "pet-images",
            accessKey = "test-access-key",
            secretKey = "test-secret-key"
        )

        // Inject the mocked S3Client using reflection
        val s3ClientField: Field = ImageService::class.java.getDeclaredField("s3Client\$delegate")
        s3ClientField.isAccessible = true
        s3ClientField.set(imageService, lazy { s3Client })
    }

    @Nested
    inner class UploadImage {

        @Test
        fun `should upload JPEG image successfully`() {
            val imageBytes = "fake image content".toByteArray()

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/jpeg")
            whenever(multipartFile.size).thenReturn(1024L)
            whenever(multipartFile.originalFilename).thenReturn("test-image.jpg")
            whenever(multipartFile.bytes).thenReturn(imageBytes)
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenReturn(PutObjectResponse.builder().build())

            val result = imageService.uploadImage(multipartFile)

            assertNotNull(result.imageUrl)
            assertTrue(result.imageUrl.contains("pet-images"))
            assertTrue(result.imageUrl.contains("pets/"))
            assertTrue(result.imageUrl.contains("test-image.jpg"))
            assertEquals("Image uploaded successfully", result.message)

            verify(s3Client).putObject(any<PutObjectRequest>(), any<RequestBody>())
        }

        @Test
        fun `should upload PNG image successfully`() {
            val imageBytes = "fake png content".toByteArray()

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/png")
            whenever(multipartFile.size).thenReturn(2048L)
            whenever(multipartFile.originalFilename).thenReturn("test-image.png")
            whenever(multipartFile.bytes).thenReturn(imageBytes)
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenReturn(PutObjectResponse.builder().build())

            val result = imageService.uploadImage(multipartFile)

            assertNotNull(result.imageUrl)
            assertTrue(result.imageUrl.contains("test-image.png"))
        }

        @Test
        fun `should upload WebP image successfully`() {
            val imageBytes = "fake webp content".toByteArray()

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/webp")
            whenever(multipartFile.size).thenReturn(512L)
            whenever(multipartFile.originalFilename).thenReturn("test-image.webp")
            whenever(multipartFile.bytes).thenReturn(imageBytes)
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenReturn(PutObjectResponse.builder().build())

            val result = imageService.uploadImage(multipartFile)

            assertNotNull(result.imageUrl)
        }

        @Test
        fun `should throw ImageUploadException when file is empty`() {
            whenever(multipartFile.isEmpty).thenReturn(true)

            val exception = assertThrows<ImageUploadException> {
                imageService.uploadImage(multipartFile)
            }

            assertEquals("Image file is required", exception.message)
            verify(s3Client, never()).putObject(any<PutObjectRequest>(), any<RequestBody>())
        }

        @Test
        fun `should throw ImageUploadException when content type is invalid`() {
            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("application/pdf")

            val exception = assertThrows<ImageUploadException> {
                imageService.uploadImage(multipartFile)
            }

            assertEquals("Invalid file type. Allowed: JPEG, PNG, WebP", exception.message)
            verify(s3Client, never()).putObject(any<PutObjectRequest>(), any<RequestBody>())
        }

        @Test
        fun `should throw ImageUploadException when content type is GIF`() {
            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/gif")

            val exception = assertThrows<ImageUploadException> {
                imageService.uploadImage(multipartFile)
            }

            assertEquals("Invalid file type. Allowed: JPEG, PNG, WebP", exception.message)
        }

        @Test
        fun `should throw ImageUploadException when file exceeds 5MB`() {
            val oversizedFile = 6 * 1024 * 1024L // 6MB

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/jpeg")
            whenever(multipartFile.size).thenReturn(oversizedFile)

            val exception = assertThrows<ImageUploadException> {
                imageService.uploadImage(multipartFile)
            }

            assertEquals("File size exceeds 5MB limit", exception.message)
            verify(s3Client, never()).putObject(any<PutObjectRequest>(), any<RequestBody>())
        }

        @Test
        fun `should throw ImageUploadException when file is exactly at 5MB limit`() {
            val exactLimit = 5 * 1024 * 1024L // exactly 5MB

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/jpeg")
            whenever(multipartFile.size).thenReturn(exactLimit)
            whenever(multipartFile.originalFilename).thenReturn("test.jpg")
            whenever(multipartFile.bytes).thenReturn(ByteArray(exactLimit.toInt()))
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenReturn(PutObjectResponse.builder().build())

            // Should succeed as it's exactly at the limit, not over
            val result = imageService.uploadImage(multipartFile)
            assertNotNull(result)
        }

        @Test
        fun `should throw ImageUploadException when S3 upload fails`() {
            val imageBytes = "fake image content".toByteArray()

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/jpeg")
            whenever(multipartFile.size).thenReturn(1024L)
            whenever(multipartFile.originalFilename).thenReturn("test-image.jpg")
            whenever(multipartFile.bytes).thenReturn(imageBytes)
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenThrow(RuntimeException("S3 connection failed"))

            val exception = assertThrows<ImageUploadException> {
                imageService.uploadImage(multipartFile)
            }

            assertTrue(exception.message!!.contains("Failed to upload image to storage"))
        }

        @Test
        fun `should handle filename with spaces`() {
            val imageBytes = "fake image content".toByteArray()

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/jpeg")
            whenever(multipartFile.size).thenReturn(1024L)
            whenever(multipartFile.originalFilename).thenReturn("my pet photo.jpg")
            whenever(multipartFile.bytes).thenReturn(imageBytes)
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenReturn(PutObjectResponse.builder().build())

            val result = imageService.uploadImage(multipartFile)

            assertNotNull(result.imageUrl)
            assertTrue(result.imageUrl.contains("my-pet-photo.jpg"))
        }

        @Test
        fun `should handle null original filename`() {
            val imageBytes = "fake image content".toByteArray()

            whenever(multipartFile.isEmpty).thenReturn(false)
            whenever(multipartFile.contentType).thenReturn("image/jpeg")
            whenever(multipartFile.size).thenReturn(1024L)
            whenever(multipartFile.originalFilename).thenReturn(null)
            whenever(multipartFile.bytes).thenReturn(imageBytes)
            whenever(s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>()))
                .thenReturn(PutObjectResponse.builder().build())

            val result = imageService.uploadImage(multipartFile)

            assertNotNull(result.imageUrl)
            assertTrue(result.imageUrl.contains("image"))
        }
    }
}