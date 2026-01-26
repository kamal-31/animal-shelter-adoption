package com.animalshelter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
data class S3Config(
    var endpoint: String = "",
    var region: String = "us-east-1",
    var bucketName: String = "",
    var accessKey: String = "",
    var secretKey: String = ""
)