package org.example.notions3.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("minio")
data class MinioProperties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String
)