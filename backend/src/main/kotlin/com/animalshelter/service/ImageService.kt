package com.animalshelter.service

import com.animalshelter.dto.ImageUploadResponse
import com.animalshelter.exception.ImageUploadException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import java.util.*

@Service
class ImageService(
    @Value("\${aws.s3.endpoint}") private val endpoint: String,
    @Value("\${aws.s3.region}") private val region: String,
    @Value("\${aws.s3.bucket-name}") private val bucketName: String,
    @Value("\${aws.s3.access-key}") private val accessKey: String,
    @Value("\${aws.s3.secret-key}") private val secretKey: String
) {

    private val s3Client: S3Client by lazy {
        val builder = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                )
            )

        // Use endpoint override for LocalStack
        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                .forcePathStyle(true)
        }

        builder.build()
    }

    private val allowedContentTypes = setOf(
        "image/jpeg",
        "image/png",
        "image/webp"
    )

    private val maxFileSize = 5 * 1024 * 1024 // 5MB

    /**
     * Upload image to S3
     */
    fun uploadImage(file: MultipartFile): ImageUploadResponse {
        // Validate file
        if (file.isEmpty) {
            throw ImageUploadException("Image file is required")
        }

        if (file.contentType !in allowedContentTypes) {
            throw ImageUploadException("Invalid file type. Allowed: JPEG, PNG, WebP")
        }

        if (file.size > maxFileSize) {
            throw ImageUploadException("File size exceeds 5MB limit")
        }

        try {
            // Generate unique filename
            val originalFilename = file.originalFilename ?: "image"
            originalFilename.substringAfterLast(".", "jpg")
            val uniqueFilename = "${UUID.randomUUID()}-${originalFilename.replace(" ", "-")}"
            val key = "pets/$uniqueFilename"

            // Upload to S3
            val putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.contentType)
                .build()

            s3Client.putObject(
                putRequest,
                RequestBody.fromBytes(file.bytes)
            )

            // Construct URL
            val imageUrl = if (endpoint.isNotBlank()) {
                // LocalStack URL
                "$endpoint/$bucketName/$key"
            } else {
                // AWS S3 URL
                "https://$bucketName.s3.$region.amazonaws.com/$key"
            }

            return ImageUploadResponse(
                imageUrl = imageUrl,
                message = "Image uploaded successfully"
            )

        } catch (e: Exception) {
            throw ImageUploadException("Failed to upload image to storage: ${e.message}")
        }
    }
}